package jsettlers.logic.algorithms.landmarks;

import java.util.List;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * Tests the partitions and if the partition encloses a blocked tile area, the
 * area is taken over by the partition.
 * <p>
 * retestUnblocked(x, y) tests after a pioneer took over land.
 * 
 * @author michael
 */
public class NewLandmarkCorrection {
	private final ILandmarksThreadGrid map;

	public NewLandmarkCorrection(ILandmarksThreadGrid map) {
		this.map = map;

	}

	// startx, starty are blocked.
	// (startx, starty) + unblockDir is the point that has the new (destination)
	// partition and is unblocked.
	private void startRelabel(short startx, short starty, short destPartition,
	        EDirection unblockedDir) {
		if (map.getPartitionAt(startx, starty) == destPartition) {
			// nothing more to do.
			return;
		}

		if (!needsRelabel(startx, starty, destPartition)) {
			throw new IllegalArgumentException();
		}
		if (needsRelabel(unblockedDir.getNextTileX(startx),
		        unblockedDir.getNextTileY(starty), destPartition)) {
			throw new IllegalArgumentException();
		}

		short blockedx = startx;
		short blockedy = starty;
		EDirection lookOutside = unblockedDir;

		System.out.println("Starting...");

		do {
			// search next point.
			// there are two ways this algo can continue: Go to next point
			// inside blocked area or next outside (=> change look direction)


			EDirection newDir = lookOutside.getNeighbor(1);
			short newx = newDir.getNextTileX(blockedx);
			short newy = newDir.getNextTileY(blockedy);
			if (newx < 0 || newy < 0 || newx >= map.getWidth()
			        || newy >= map.getHeight()) {
				System.out.println("Border case");
				newDir = newDir.getNeighbor(2);
				boolean onBoder = true;
				while (onBoder) {
					short nx = newDir.getNextTileX(blockedx);
					short ny = newDir.getNextTileY(blockedy);
					if (nx < 0) {
						newDir = EDirection.SOUTH_WEST;
					} else if (ny < 0) {
						newDir = EDirection.WEST;
					} else if (nx >= map.getWidth()) {
						newDir = EDirection.NORTH_EAST;
					} else if (ny >= map.getHeight()) {
						newDir = EDirection.EAST;
					} else if (map.isBlocked(nx, ny)) {
						blockedx = nx;
						blockedy = ny;
					} else {
						// left border.
						onBoder = false;
					}
				}
			} else {
				if (map.isBlocked(newx, newy)) {
					blockedx = newx;
					blockedy = newy;
					lookOutside = lookOutside.getNeighbor(-1);
				} else {
					if (map.getPartitionAt(newx, newy) != destPartition) {
						// break here, we are not continuing.
						return;
					}
					lookOutside = newDir;
				}
			}
		} while (blockedx != startx || blockedy != starty
		        || !lookOutside.equals(unblockedDir));

		relabel(startx, starty, destPartition, true, true);
	}

	// relabels a blocked partition
	private void relabel(short startx, short starty, short destPartition,
	        boolean up, boolean down) {
		assert (needsRelabel(startx, starty, destPartition)) : "Start point needs to be relabled";
		
		short leftx = startx;
		short rightx = startx;
		while (leftx > 1 && needsRelabel((short) (leftx - 1), starty, destPartition)) {
			leftx--;
		}
		while (rightx + 1 < map.getWidth()
		        && needsRelabel((short) (rightx + 1), starty, destPartition)) {
			rightx++;
		}
		relabelBlock(leftx, rightx, starty, destPartition);
		if (down) {
			relabelDownwards(leftx, rightx, starty, destPartition);
		}
		if (up) {
			relabelUpwards(leftx, rightx, starty, destPartition);
		}
	}

	// relabels below a line.
	private void relabelUpwards(short leftx, short rightx, short starty,
	        short destPartition) {
		short y = (short) (starty - 1);
		while (y >= 0) {
			short newleft = leftx > 0 ? (short) (leftx - 1) : 0;
			if (needsRelabel(newleft, y, destPartition)) {
				// go more left
				while (newleft > 0
				        && needsRelabel((short) (newleft - 1), y, destPartition)) {
					newleft--;
					if (newleft - 1 >= 0
					        && needsRelabel((short) (newleft - 1),
					                (short) (y + 1), destPartition)) {
						// mark the point to relabel.
						markToRelabel((short) (newleft - 1), (short) (y + 1),
						        destPartition, false);
					}
				}
			} else {
				while (!needsRelabel(newleft, y, destPartition)) {
					newleft++;
					if (newleft > rightx + 1 || newleft >= map.getWidth()) {
						// Main break condition: No more blocked lines...
						return;
					}
				}
			}

			short newright = newleft;
			while (newright + 1 < map.getWidth()
			        && needsRelabel((short) (newright + 1), y, destPartition)) {
				newright++;
				if (newright + 1 < map.getWidth() && newright >= rightx) {
					if (needsRelabel((short) (newright + 1), (short) (y + 1),
					        destPartition)) {
						markToRelabel((short) (newright + 1), (short) (y + 1),
						        destPartition, false);
					}
				}
			}

			relabelBlock(newleft, newright, y, destPartition);

			for (short x = (short) (newright + 1); x <= rightx + 1
			        && x < map.getWidth(); x++) {
				if (needsRelabel(x, y, destPartition)) {
					markToRelabel(x, y, destPartition, true);
				}
			}

			leftx = newleft;
			rightx = newright;
			y--;
		}
	}

	// relabels below a line.
	private void relabelDownwards(short leftx, short rightx, short starty,
	        short destPartition) {
		short y = (short) (starty + 1);
		while (y < map.getHeight()) {
			short newleft = leftx;
			if (needsRelabel(newleft, y, destPartition)) {
				// go more left
				while (newleft > 0
				        && needsRelabel((short) (newleft - 1), y, destPartition)) {
					newleft--;
					if (newleft - 1 >= 0
					        && needsRelabel((short) (newleft - 1),
					                (short) (y - 1), destPartition)) {
						// mark the point to relabel.
						markToRelabel((short) (newleft - 1), (short) (y - 1),
						        destPartition, true);
					}
				}
			} else {
				while (!needsRelabel(newleft, y, destPartition)) {
					newleft++;
					if (newleft > rightx + 1 || newleft >= map.getWidth()) {
						// Main break condition: No more blocked lines...
						return;
					}
				}
			}

			short newright = newleft;
			while (newright + 1 < map.getWidth()
			        && needsRelabel((short) (newright + 1), y, destPartition)) {
				newright++;
				if (newright > rightx) {
					if (needsRelabel(newright, (short) (y - 1), destPartition)) {
						markToRelabel(newright, (short) (y - 1), destPartition,
						        true);
					}
				}
			}

			relabelBlock(newleft, newright, y, destPartition);

			for (short x = (short) (newright + 1); x <= rightx + 1
			        && x < map.getWidth(); x++) {
				if (needsRelabel(x, y, destPartition)) {
					markToRelabel(x, y, destPartition, false);
				}
			}

			leftx = newleft;
			rightx = newright;
			y++;
		}
	}

	private boolean needsRelabel(short x, short y, short destPartition) {
		return map.isBlocked(x, y) && map.getPartitionAt(x, y) != destPartition;
	}

	private void markToRelabel(short x, short y, short destPartition,
	        boolean upwards) {
		 System.out.println("Restarting at " + x + ", " + y + ", upwards = " +
		 upwards);
		relabel(x, y, destPartition, upwards, !upwards);
	}

	private void relabelBlock(short leftx, short rightx, short y,
	        short destPartition) {
		System.out.println("Relabeling line " + y + " from " + leftx + " to "
		        + rightx);
		for (short x = leftx; x <= rightx; x++) {
			map.setPartitionAndPlayerAt(x, y, destPartition);
		}
	}

	/**
	 * To be called after the parition of (x,y) changed.
	 * 
	 * @param x
	 * @param y
	 */
	public void reTest(short x, short y) {
		if (!map.isBlocked(x, y)) {
			short destPartition = map.getPartitionAt(x, y);
			for (EDirection dir : EDirection.values) {
				short nx = dir.getNextTileX(x);
				short ny = dir.getNextTileY(y);
				if (map.isInBounds(nx, ny) && map.isBlocked(nx, ny)) {
					System.out.println("trying to relabel: " + nx + "," + ny);
					startRelabel(nx, ny, destPartition,
					        dir.getInverseDirection());
				}
			}
		}
	}

	public void addLandmarkedPositions(List<ShortPoint2D> occupiedPositions) {
		for (ShortPoint2D pos : occupiedPositions) {
			reTest(pos.getX(), pos.getY());
		}
	}
}
