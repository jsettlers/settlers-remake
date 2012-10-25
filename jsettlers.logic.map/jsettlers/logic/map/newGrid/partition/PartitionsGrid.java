package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.Color;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.partitions.IPartionsAlgorithmMap;
import jsettlers.logic.algorithms.partitions.PartitionsAlgorithm;
import jsettlers.logic.algorithms.path.astar.normal.IAStar;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

/**
 * This class handles the partitions of the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionsGrid implements IPartionsAlgorithmMap, Serializable {
	private static final long serialVersionUID = 8919380724171427679L;

	private final short width;
	private final short height;
	private final short[] partitions;
	private final byte[] towers;

	/**
	 * This array stores the partition objects handled by this class.<br>
	 */
	private final Partition[] partitionObjects = new Partition[1024]; // TODO make the array grow dynamically
	private final Partition nullPartition;
	transient private PartitionsAlgorithm partitionsAlgorithm;
	private final IPartitionableGrid grid;

	public PartitionsGrid(final short width, final short height, IPartitionableGrid grid) {
		this.width = width;
		this.height = height;
		this.grid = grid;
		this.partitions = new short[width * height];
		this.towers = new byte[width * height];
		this.nullPartition = new Partition((byte) -1, height * width);

		for (int idx = 0; idx < partitions.length; idx++) {
			this.partitions[idx] = -1;
		}
	}

	private final int getIdx(int x, int y) {
		return y * width + x;
	}

	public final void initPartitionsAlgorithm(IAStar aStar) {
		this.partitionsAlgorithm = new PartitionsAlgorithm(this, aStar);
	}

	@Override
	public final byte getPlayerAt(ShortPoint2D position) {
		return isInBounds(position) ? this.getPartitionObject(position.getX(), position.getY()).getPlayer() : -1;
	}

	public final byte getPlayerAt(short x, short y) {
		return getPartitionObject(x, y).getPlayer();
	}

	public final short getPartitionAt(short x, short y) {
		return this.partitions[getIdx(x, y)];
	}

	private final Partition getPartitionObject(ShortPoint2D pos) {
		return getPartitionObject(getPartition(pos));
	}

	private final Partition getPartitionObject(short x, short y) {
		return getPartitionObject(getPartitionAt(x, y));
	}

	private final Partition getPartitionObject(short partition) {
		if (partition >= 0)
			return this.partitionObjects[partition];
		else
			return nullPartition;
	}

	@Override
	public final short getPartition(ShortPoint2D position) {
		return getPartition(position.getX(), position.getY());
	}

	@Override
	public final short getPartition(short x, short y) {
		return this.partitions[getIdx(x, y)];
	}

	@Override
	public final void setPartition(final short x, final short y, short newPartition) {
		Partition newPartitionObject = getPartitionObject(newPartition);

		Partition oldPartitionObject = getPartitionObject(x, y);
		oldPartitionObject.removePositionTo(x, y, newPartitionObject);
		grid.setDebugColor(x, y, Color.GREEN);

		this.partitions[getIdx(x, y)] = newPartition;

		grid.changedPartitionAt(x, y);
	}

	public final void setPartitionAndPlayerAt(short x, short y, short partition) {
		this.partitions[getIdx(x, y)] = partition;
	}

	@Override
	public final short mergePartitions(final short x1, final short y1, final short x2, final short y2) {
		System.out.println("MERGE!!");

		short firstPartition = getPartition(x1, y1);
		short secondPartition = getPartition(x2, y2);

		assert firstPartition != -1 && secondPartition != -1 : "-1 partitions can not be merged!!";
		assert x1 != x2 || y1 != y2 : "can not merge two equal partitions";

		Partition firstObject = partitionObjects[firstPartition];
		Partition secondObject = partitionObjects[secondPartition];

		short newPartition;

		// for better performance, relabel the smaller partition
		if (firstObject.getNumberOfElements() > secondObject.getNumberOfElements()) {
			newPartition = firstPartition;
			relabelPartition(x2, y2, secondPartition, firstPartition, true);
			secondObject.mergeInto(firstObject);
		} else {
			newPartition = secondPartition;
			relabelPartition(x1, y1, firstPartition, secondPartition, true);
			firstObject.mergeInto(secondObject);

		}

		return newPartition;
	}

	@Override
	public final void createPartition(final short x, final short y, byte player) {
		short partition = initializeNewPartition(player);
		setPartition(x, y, partition);
	}

	private final short initializeNewPartition(byte player) {
		short partition = getFreePartitionIndex();
		this.partitionObjects[partition] = new Partition(player);
		return partition;
	}

	private final short getFreePartitionIndex() {
		for (short i = 0; i < this.partitionObjects.length; i++) {
			if (this.partitionObjects[i] == null || this.partitionObjects[i].isEmpty())
				return i;
		}

		System.err.println("HAVE NO PARTITIONS LEFT!!!");
		return (short) (this.partitionObjects.length - 1);
	}

	@Override
	public final void dividePartition(final short x, final short y, ShortPoint2D firstPos, ShortPoint2D secondPos) {
		System.out.println("DIVIDE!!");
		short newPartition = initializeNewPartition(getPlayerAt(firstPos));
		short oldPartition = getPartition(firstPos);

		// partitions[getIdx(x, y)] = -1;// this is needed, because the new partition is not determined yet
		relabelPartition(secondPos.getX(), secondPos.getY(), oldPartition, newPartition, false);
		// partitions[getIdx(x, y)] = oldPartition;
	}

	private final byte[] neighborX = EDirection.getXDeltaArray();
	private final byte[] neighborY = EDirection.getYDeltaArray();

	/**
	 * 
	 * @param inX
	 * @param inY
	 * @param oldPartition
	 * @param newPartition
	 * @param justRelabel
	 *            if true, only the partition will be changed.<br>
	 *            if false, the partition will be changed and for every changed position the contents of that position in the old manager will be
	 *            moved to the new manager.
	 */
	private final void relabelPartition(short inX, short inY, short oldPartition, short newPartition, boolean justRelabel) {
		final short MAX_LENGTH = 10000;
		final short[] pointsBuffer = new short[MAX_LENGTH]; // array is used to reduce the number of recursions
		pointsBuffer[0] = inX;
		pointsBuffer[1] = inY;
		short length = 2;

		while (length > 0) {
			short y = pointsBuffer[--length];
			short x = pointsBuffer[--length];
			if (partitions[getIdx(x, y)] != oldPartition) {
				continue; // the partition may already have changed.
			}

			if (justRelabel) {
				this.partitions[getIdx(x, y)] = newPartition;
			} else {
				setPartition(x, y, newPartition);
			}
			boolean currIsBlocked = grid.isBlocked(x, y);

			for (byte i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				short currX = (short) (x + neighborX[i]);
				short currY = (short) (y + neighborY[i]);
				if (isInBounds(currX, currY) && partitions[getIdx(currX, currY)] == oldPartition && (!currIsBlocked || grid.isBlocked(currX, currY))) {
					if (length < MAX_LENGTH) {
						pointsBuffer[length++] = currX;
						pointsBuffer[length++] = currY;
					} else {
						relabelPartition(currX, currY, oldPartition, newPartition, justRelabel);
					}
				}
			}
		}
	}

	public final void changePlayerAt(short x, short y, byte newPlayer) {
		if (getPlayerAt(x, y) != newPlayer) {
			this.partitionsAlgorithm.calculateNewPartition(x, y, newPlayer);
		}
	}

	private final boolean isInBounds(ShortPoint2D position) {
		return isInBounds(position.getX(), position.getY());
	}

	@Override
	public final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	public final boolean pushMaterial(ShortPoint2D position, EMaterialType materialType) {
		return getPartitionObject(position.getX(), position.getY()).addOffer(position, materialType);
	}

	public final void addJobless(IManageableBearer bearer) {
		getPartitionObject(bearer.getPos()).addJobless(bearer);
	}

	public void removeJobless(IManageableBearer bearer) {
		getPartitionObject(bearer.getPos()).removeJobless(bearer);
	}

	public final void addJobless(IManageableWorker worker) {
		getPartitionObject(worker.getPos()).addJobless(worker);
	}

	public void removeJobless(IManageableWorker worker) {
		getPartitionObject(worker.getPos()).removeJobless(worker);
	}

	public final void addJobless(IManageableBricklayer bricklayer) {
		getPartitionObject(bricklayer.getPos()).addJobless(bricklayer);
	}

	public void removeJobless(IManageableBricklayer bricklayer) {
		getPartitionObject(bricklayer.getPos()).removeJobless(bricklayer);
	}

	public final void addJobless(IManageableDigger digger) {
		getPartitionObject(digger.getPos()).addJobless(digger);
	}

	public void removeJobless(IManageableDigger digger) {
		getPartitionObject(digger.getPos()).removeJobless(digger);
	}

	public final void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
		getPartitionObject(requester.getPos()).request(requester, materialType, priority);
	}

	public final void requestDiggers(IDiggerRequester requester, byte amount) {
		getPartitionObject(requester.getPos()).requestDiggers(requester, amount);
	}

	public final void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		getPartitionObject(building.getPos()).requestBricklayer(building, bricklayerTargetPos, direction);
	}

	public final void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
		getPartitionObject(workerBuilding.getPos()).requestBuildingWorker(workerType, workerBuilding);
	}

	@Override
	public final boolean isBlockedForPeople(short x, short y) {
		return grid.isBlocked(x, y);
	}

	public final void requestSoilderable(IBarrack barrack) {
		getPartitionObject(barrack.getDoor()).requestSoilderable(barrack);
	}

	public final void releaseRequestsAt(ShortPoint2D position, EMaterialType materialType) {
		getPartitionObject(position).releaseRequestsAt(position, materialType);
	}

	public final List<ShortPoint2D> occupyArea(IMapArea toBeOccupied, IMapArea groundArea, byte newPlayer) {
		MilliStopWatch watch = new MilliStopWatch();
		watch.start();

		List<ShortPoint2D> occupiedPositions = new ArrayList<ShortPoint2D>();

		Iterator<ShortPoint2D> groundAreaIter = groundArea.iterator();
		ShortPoint2D firstPos = groundAreaIter.next();
		changePlayerAt(firstPos.getX(), firstPos.getY(), newPlayer);
		short newPartition = getPartition(firstPos);
		occupiedPositions.add(firstPos);

		while (groundAreaIter.hasNext()) {
			ShortPoint2D curr = groundAreaIter.next();
			short x = curr.getX();
			short y = curr.getY();
			if (getPartition(x, y) != newPartition) {
				this.setPartition(x, y, newPartition);
				occupiedPositions.add(curr);
			}
		}

		List<ShortPoint2D> checkForMerge = new ArrayList<ShortPoint2D>();

		ShortPoint2D unblockedOccupied = null;

		for (ShortPoint2D curr : toBeOccupied) {
			short x = curr.getX();
			short y = curr.getY();

			if (!isInBounds(x, y)) {
				continue;
			}

			short currPartition = getPartitionAt(x, y);

			if (currPartition != newPartition) {
				if (currPartition >= 0 && partitionObjects[currPartition].getPlayer() == newPlayer) {
					checkForMerge.add(curr);

				} else if (towers[getIdx(x, y)] <= 0) {
					setPartition(x, y, newPartition);
					occupiedPositions.add(curr);

					if (unblockedOccupied == null && !grid.isBlocked(x, y)) {
						unblockedOccupied = curr;
					}
				}
			}

			if (getPlayerAt(x, y) == newPlayer) {
				towers[getIdx(x, y)]++;

				for (ShortPoint2D neighbor : new MapNeighboursArea(curr)) {
					if (isInBounds(neighbor) && !toBeOccupied.contains(neighbor)) {
						checkForMerge.add(neighbor);
					}
				}
			}
		}

		ShortPoint2D[] foundPartions = new ShortPoint2D[partitionObjects.length];
		for (ShortPoint2D curr : checkForMerge) {
			foundPartions[getPartition(curr) + 1] = curr; // +1 to have no conflict with unoccupied area
		}

		for (short i = 0; i < foundPartions.length; i++) {
			ShortPoint2D pos = foundPartions[i];
			if (pos != null && (i - 1) != newPartition) {
				if (getPartitionObject((short) (i - 1)).getPlayer() == newPlayer) {
					this.mergePartitions(pos.getX(), pos.getY(), unblockedOccupied.getX(), unblockedOccupied.getY());
				}
			}
		}

		watch.stop("occupying area needed---------------------------------------------------------------------------------------------");
		return occupiedPositions;
	}

	public final boolean isEnforcedByTower(short x, short y) {
		return towers[getIdx(x, y)] > 0;
	}

	public final List<ShortPoint2D> freeOccupiedArea(IMapArea occupied, ShortPoint2D occupiersPosition) {
		short partiton = getPartition(occupiersPosition);
		// a LinkedList is used, because the user needs to delete random elements
		List<ShortPoint2D> totallyFreePositions = new LinkedList<ShortPoint2D>();

		for (ShortPoint2D curr : occupied) {
			short x = curr.getX();
			short y = curr.getY();
			if (isInBounds(x, y) && getPartitionAt(x, y) == partiton) {
				final int idx = getIdx(x, y);
				towers[idx]--;
				if (towers[idx] <= 0) {
					totallyFreePositions.add(curr);
					towers[idx] = 0; // just to ensure that the array is ok (<0 would be wrong)
				}
			}
		}

		return totallyFreePositions;
	}

	public final void removeOfferAt(ShortPoint2D pos, EMaterialType materialType) {
		getPartitionObject(pos).removeOfferAt(pos, materialType);
	}

	public final int getTowerCounterAt(short x, short y) {
		return towers[getIdx(x, y)];
	}

	/**
	 * Used by towers to occupy a single position.
	 * 
	 * @param x
	 * @param y
	 * @param newPlayer
	 */
	public final void occupyAt(short x, short y, byte newPlayer) {
		changePlayerAt(x, y, newPlayer);
		towers[getIdx(x, y)]++;
	}

	public EMaterialType popToolProduction(ShortPoint2D pos) {
		return getPartitionObject(pos).popToolProduction(pos);
	}

}
