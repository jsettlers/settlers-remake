package jsettlers.logic.algorithms.partitions;

import jsettlers.common.movable.EDirection;

/**
 * Calculates the partitioning on a grid that has blocked positions.
 * 
 * @author Andreas Eberle
 * 
 */
public final class BlockedPartitioner {
	public static final short BLOCKED_PARTITION = -1;
	private final IBlockedPartitionerGrid grid;
	private final short[] partitions;
	private final int width;
	private final int height;

	private short[] unionTable;
	private short newPartitionCtr = 0;

	/**
	 * Creates a new {@link BlockedPartitioner}.
	 * 
	 * @param grid
	 *            {@link IBlockedPartitionerGrid} that offers an isBlocked method.
	 * @param width
	 *            Width of the given grid.
	 * @param height
	 *            Height of the given grid.
	 */
	public BlockedPartitioner(IBlockedPartitionerGrid grid, int width, int height) {
		this.grid = grid;
		this.partitions = new short[width * height];
		this.width = width;
		this.height = height;

		this.unionTable = new short[width];
		this.unionTable[BLOCKED_PARTITION + 1] = BLOCKED_PARTITION;
	}

	public short[] getPartitions() {
		return partitions;
	}

	/**
	 * Calculates the partitions for the given grid.
	 */
	public void calculate() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (grid.isBlocked(x, y)) {
					setPartition(x, y, BLOCKED_PARTITION);
				} else {
					short northEast = getEnsuredPartition(EDirection.NORTH_EAST.gridDeltaX + x, EDirection.NORTH_EAST.gridDeltaY + y);
					short northWest = getEnsuredPartition(EDirection.NORTH_WEST.gridDeltaX + x, EDirection.NORTH_WEST.gridDeltaY + y);
					short west = getEnsuredPartition(EDirection.WEST.gridDeltaX + x, EDirection.WEST.gridDeltaY + y);

					short newPartition = (short) Math.max(northWest, Math.max(northEast, west));

					if (newPartition == BLOCKED_PARTITION) {
						newPartition = createNewPartition();
					} else {
						if (northEast != BLOCKED_PARTITION && northEast != newPartition) {
							merge(northEast, newPartition);
						} else if (northWest != BLOCKED_PARTITION && northWest != newPartition) {
							merge(northWest, newPartition);
						} else if (west != BLOCKED_PARTITION && west != newPartition) {
							merge(west, newPartition);
						}
					}

					setPartition(x, y, newPartition);
				}
			}
		}

		// print();
		// System.out.println("\nunionTable: " + Arrays.toString(unionTable) + "\n");

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setPartition(x, y, unionTable[getPartition(x, y) + 1]);
			}
		}
	}

	private void merge(short toBeMerged, short newPartition) {
		// System.out.println("merged: " + toBeMerged + "   " + newPartition);

		unionTable[toBeMerged + 1] = newPartition;

		// fix uniontable
		for (int i = 0; i < newPartitionCtr; i++) {
			short partition = unionTable[i + 1];
			if (partition == toBeMerged) { // only work on the ones that will be changed.
				do {
					partition = unionTable[partition + 1];
				} while (unionTable[partition + 1] != partition);

				unionTable[i + 1] = partition;
			}
		}
	}

	/**
	 * Prints the calculated partitioning.
	 */
	public void print() {
		for (int y = 0; y < height; y++) {
			for (int i = 0; i < 4 * (height - y); i++) {
				System.out.print(" ");
			}
			for (int x = 0; x < width; x++) {
				StringBuilder builder = new StringBuilder("        ");
				String string = String.valueOf(getPartition(x, y));
				builder.replace(0, string.length(), string);
				System.out.print(builder.toString());
			}
			System.out.println();
		}
	}

	private short createNewPartition() {
		short newPartition = newPartitionCtr++;
		if (newPartition + 1 >= unionTable.length) {
			short[] tempTable = new short[unionTable.length * 2];
			System.arraycopy(unionTable, 0, tempTable, 0, unionTable.length);
			unionTable = tempTable;
		}

		unionTable[newPartition + 1] = newPartition;
		return newPartition;
	}

	private short getEnsuredPartition(int x, int y) {
		if (isInBounds(x, y)) {
			return unionTable[getPartition(x, y) + 1];
		} else {
			return BLOCKED_PARTITION;
		}
	}

	private boolean isInBounds(int x, int y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	private void setPartition(int x, int y, short newPartition) {
		partitions[x + y * width] = newPartition;
	}

	private int getPartition(int x, int y) {
		return partitions[x + y * width];
	}
}
