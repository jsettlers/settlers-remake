package jsettlers.logic.algorithms.partitions;

import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStar;

/**
 * Calculates the partitions of the Map and combines partitions of the same player when they get in contact with each other or divides partitions that
 * get separated.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionsAlgorithm {
	private final IPartionsAlgorithmMap grid;
	private final IAStar aStar;
	private final AStarPathable aStarPathable;

	/**
	 * Constructor of {@link PartitionsAlgorithm}.
	 * 
	 * @param partitionsMap
	 *            The {@link PartitionsAlgorithm} is operating on this map.
	 * @param aStar
	 *            This is the aStar used by the algorithm.
	 */
	public PartitionsAlgorithm(final IPartionsAlgorithmMap partitionsMap, final IAStar aStar) {
		this.grid = partitionsMap;
		this.aStar = aStar;
		this.aStarPathable = new AStarPathable();
	}

	/**
	 * Calculates the new partition for the given position.<br>
	 * It also merges or divides partitions if it became necessary by the given change to position.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param newPlayer
	 *            the player that is now occupying the given position.
	 */
	public void calculateNewPartition(short x, short y, byte newPlayer) {
		short oldPartition = grid.getPartition(x, y);

		if (newPlayer != -1) { // if the new player is really a player
			addToNewPartiton(x, y, newPlayer);
		}

		if (oldPartition != -1) {
			removeFromOldPartition(x, y, oldPartition);
		}
	}

	/**
	 * This method checks if one of the neighbors of the given position is from the same player to find a matching partition.<br>
	 * If multiple neighbors are from the same player but have different partitions, a merge of the partitions is done.
	 * 
	 * @param changedPosition
	 *            position that needs to get a new partition
	 * @param newPlayer
	 *            player that is now occupying the position
	 */
	private void addToNewPartiton(final short x, final short y, final byte newPlayer) {
		short newPartition = -1;

		for (ShortPoint2D currPos : new MapNeighboursArea(x, y)) {
			if (grid.getPlayerAt(currPos) == newPlayer) {
				if (newPartition == -1) { // neighbor has same player and we have no partition found yet -> add to the same partition
					newPartition = grid.getPartition(currPos);
					grid.setPartition(x, y, newPartition);
					if (this.grid.isBlockedForPeople(x, y)) {
						break;
					}
				} else if (!this.grid.isBlockedForPeople(currPos.getX(), currPos.getY())) {
					if (grid.getPartition(currPos) != newPartition) { // neighbor is an other partition but has same player
						newPartition = grid.mergePartitions(currPos.getX(), currPos.getY(), x, y);
					}// else: neighbor has same player and same partition
				}
			}
		}

		if (newPartition == -1) { // no partition found -> create one
			grid.createPartition(x, y, newPlayer);
		}
	}

	/**
	 * Checks if the old partition is separated by removing the given position and if so, it lets divided the partition in new partitions.
	 * 
	 * @param position
	 *            position that has been removed
	 * @param oldPartition
	 *            old partition of the removed position
	 */
	private void removeFromOldPartition(final short x, final short y, final short oldPartition) {
		ShortPoint2D[] disconnected = new ShortPoint2D[3]; // at maximum 3 neighbors can be disconnected on the hex grid
		byte disconnectedCtr = 0;

		boolean lastWasOldPartition = false;

		for (EDirection dir : EDirection.values) {
			ShortPoint2D currPos = dir.getNextHexPoint(x, y);
			if (!grid.isInBounds(currPos.getX(), currPos.getY())) {
				continue;
			}

			short currPartition = grid.getPartition(currPos);

			if (lastWasOldPartition) {
				if (currPartition == oldPartition) {
					// nothing to do, it's connected
				} else { // it's not connected
					lastWasOldPartition = false;
				}
			} else {
				if (currPartition == oldPartition && !grid.isBlockedForPeople(x, y)) {
					lastWasOldPartition = true;
					disconnected[disconnectedCtr] = currPos;
					disconnectedCtr++;
				}// else: not the oldPartition, so ignore it
			}
		}

		ShortPoint2D lastPosition = EDirection.values[5].getNextHexPoint(x, y);
		if (grid.getPartition(lastPosition) == oldPartition) {
			disconnectedCtr--;
		}

		if (disconnectedCtr > 1) {
			byte oldPlayer = grid.getPlayerAt(disconnected[0]);
			if (!existsPathBetween(disconnected[1], disconnected[0], oldPlayer)) { // [0] and [1] are not connected
				grid.dividePartition(x, y, disconnected[1], disconnected[0]);

				if (disconnectedCtr == 3) {
					if (!existsPathBetween(disconnected[2], disconnected[1], oldPlayer)) { // [2] and [1] are not connected
						grid.dividePartition(x, y, disconnected[2], disconnected[1]);

						if (existsPathBetween(disconnected[2], disconnected[0], oldPlayer)) { // [2] and [0] are not connected
							grid.dividePartition(x, y, disconnected[2], disconnected[0]);
						} else {
							// [2] and [0] are connected
						}
					} else {
						// [2] and [1] are connected
					}
				} else {
					// we're done because there is no [2]
				}
			} else { // [0] and [1] are connected
				if (disconnectedCtr == 3) {
					if (!existsPathBetween(disconnected[2], disconnected[1], oldPlayer)) { // but [2] is not connected to [0] and [1]
						grid.dividePartition(x, y, disconnected[2], disconnected[1]);
					} else {
						// [0], [1], [2] are connected
					}
				} else {
					// [0] and [1] are connected
				}
			}
		} else {
			// there can not be any disconnection
		}
	}

	private boolean existsPathBetween(final ShortPoint2D firstPos, final ShortPoint2D secondPos, final byte player) {
		aStarPathable.pos = firstPos;
		aStarPathable.player = player;

		// TODO PERFORMANCE IMPROVE: develop an optimized algorithm to detect if two tiles are connected by one partition

		return aStar.findPath(aStarPathable, secondPos) != null;
	}

	/**
	 * Class that's needed to be able to use the {@link HexAStar} algorithm.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private class AStarPathable implements IPathCalculateable {
		private byte player;
		private ShortPoint2D pos;

		@Override
		public byte getPlayer() {
			return player;
		}

		@Override
		public ShortPoint2D getPos() {
			return pos;
		}

		@Override
		public boolean needsPlayersGround() {
			return true;
		}

	}
}
