package jsettlers.common.utils.partitioning;

import java.util.BitSet;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

public final class PartitionCalculator {
	private static final int MAX_NUMBER_OF_PARTITIONS = 1000;
	private static final int[] neighborX = { EDirection.WEST.gridDeltaX, EDirection.NORTH_WEST.gridDeltaX, EDirection.NORTH_EAST.gridDeltaX };
	private static final int[] neighborY = { EDirection.WEST.gridDeltaY, EDirection.NORTH_WEST.gridDeltaY, EDirection.NORTH_EAST.gridDeltaY };

	private final int minX;
	private final int minY;
	private final int width;
	private final int height;
	private final BitSet containing;
	private final short[] partitionGrid;

	private final ShortPoint2D[] partitionBorderPos = new ShortPoint2D[MAX_NUMBER_OF_PARTITIONS];

	private short[] partitions = new short[MAX_NUMBER_OF_PARTITIONS];
	private short nextFreePartition = 1;
	private short neededPartitions;

	public PartitionCalculator(Iterable<ShortPoint2D> positions, int minX, int minY, int maxX, int maxY) {
		this.minX = --minX; // this increases the window, so that no position can lay on the border.
		this.minY = --minY;
		maxX++;
		maxY++;
		width = maxX - minX + 1;
		height = maxY - minY + 1;

		containing = new BitSet(width * height);
		for (ShortPoint2D curr : positions) {
			containing.set((curr.getX() - minX) + (curr.getY() - minY) * width);
		}

		partitionGrid = new short[width * height];
	}

	public void calculatePartitions() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = x + y * width;
				if (containing.get(index)) {

					int westX = x + neighborX[0];
					int westY = y + neighborY[0];
					int northWestX = x + neighborX[1];
					int northWestY = y + neighborY[1];
					int northEastX = x + neighborX[2];
					int northEastY = y + neighborY[2];

					int partition = -1;
					int westPartition = -1;
					if (containing.get(westX + westY * width)) {
						westPartition = partitionGrid[westX + westY * width];
						partition = westPartition;
					}

					if (containing.get(northWestX + northWestY * width)) {
						partition = partitionGrid[northWestX + northWestY * width];
					}

					int northEastPartition = -1;
					if (containing.get(northEastX + northEastY * width)) {
						northEastPartition = partitionGrid[northEastX + northEastY * width];
						partition = northEastPartition;
					}

					if (westPartition != -1 && northEastPartition != -1 && partitions[westPartition] != partitions[northEastPartition]) {
						// mergePartitions if west and northeast are not equal but set
						short newPartition = (short) Math.min(partitions[westPartition], partitions[northEastPartition]);
						partitions[westPartition] = newPartition;
						partitions[northEastPartition] = newPartition;
						partitionGrid[index] = newPartition;

					} else if (partition != -1) { // just set the value.
						partitionGrid[index] = partitions[partition];

					} else { // create a new partition
						partitionGrid[index] = nextFreePartition;
						partitions[nextFreePartition] = nextFreePartition;
						partitionBorderPos[nextFreePartition] = new ShortPoint2D(x + minX, y + minY);

						nextFreePartition++;
					}

				}
			}
		}

		// post processing
		short[] compacted = new short[partitions.length];
		short compactedCount = 0;

		for (short i = 1; i < nextFreePartition; i++) {
			short representative = i;
			short nextRep;

			while (representative != (nextRep = partitions[representative])) {
				representative = nextRep;
			}

			if (compacted[representative] == 0) {
				compacted[representative] = ++compactedCount;
				partitionBorderPos[compactedCount] = partitionBorderPos[representative];
			}

			partitions[i] = representative;
			compacted[i] = compacted[representative];
		}
		partitions = compacted;
		neededPartitions = compactedCount;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public short getPartitionAt(int x, int y) {
		return partitions[partitionGrid[x + y * width]];
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getNumberOfPartitions() {
		return neededPartitions;
	}

	public ShortPoint2D getPartitionBorderPos(int partitionIdx) {
		return partitionBorderPos[partitionIdx];
	}
}
