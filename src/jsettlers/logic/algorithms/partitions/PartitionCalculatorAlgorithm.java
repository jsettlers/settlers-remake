package jsettlers.logic.algorithms.partitions;

import java.util.BitSet;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * An algorithm to calculate partitions for a given set of positions.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionCalculatorAlgorithm {
	private static final int MAX_NUMBER_OF_PARTITIONS = 1000;
	private static final int[] neighborX = { EDirection.WEST.gridDeltaX, EDirection.NORTH_WEST.gridDeltaX, EDirection.NORTH_EAST.gridDeltaX,
			EDirection.EAST.gridDeltaX, EDirection.SOUTH_EAST.gridDeltaX, EDirection.SOUTH_WEST.gridDeltaX };
	private static final int[] neighborY = { EDirection.WEST.gridDeltaY, EDirection.NORTH_WEST.gridDeltaY, EDirection.NORTH_EAST.gridDeltaY,
			EDirection.EAST.gridDeltaY, EDirection.SOUTH_EAST.gridDeltaY, EDirection.SOUTH_WEST.gridDeltaY };
	private static final int INCREASE_FACTOR = 2;

	private final int minX;
	private final int minY;
	private final int width;
	private final int height;
	private final BitSet containing;
	private final short[] partitionsGrid;
	private final boolean invertBitSet;

	private short[] partitions = new short[MAX_NUMBER_OF_PARTITIONS];
	private ShortPoint2D[] partitionBorderPositions = new ShortPoint2D[MAX_NUMBER_OF_PARTITIONS];

	private short nextFreePartition = 1;
	private short neededPartitions;

	/**
	 * Creates a new {@link PartitionCalculatorAlgorithm}. The given positions are positions in the created partitions. Non mentioned positions are
	 * seen as outside of partitions.
	 * 
	 * @param positions
	 *            The positions of the calculated partitions.
	 * @param minX
	 *            The smallest x coordinate in the list of positions.
	 * @param minY
	 *            The smallest y coordinate in the list of positions.
	 * @param maxX
	 *            The biggest x coordinate in the list of positions.
	 * @param maxY
	 *            The biggest y coordinate in the list of positions.
	 */
	public PartitionCalculatorAlgorithm(Iterable<ShortPoint2D> positions, int minX, int minY, int maxX, int maxY) {
		this.minX = --minX; // this increases the window, so that no position can lay on the border.
		this.minY = --minY;
		maxX++;
		maxY++;
		this.width = maxX - minX + 1;
		this.height = maxY - minY + 1;

		this.containing = new BitSet(width * height);
		for (ShortPoint2D curr : positions) {
			containing.set((curr.getX() - minX) + (curr.getY() - minY) * width);
		}

		this.partitionsGrid = new short[width * height];
		this.invertBitSet = false;
	}

	/**
	 * Creates a new {@link PartitionCalculatorAlgorithm}. The given {@link BitSet} defines the positions that need to be in the partitions and the
	 * ones that mustn't.
	 * 
	 * @param minX
	 *            The x offset of the {@link BitSet}.
	 * @param minY
	 *            The y offset of the {@link BitSet}.
	 * @param width
	 *            The width of the grid defined by the {@link BitSet}.
	 * @param height
	 *            The height of the grid defined by the {@link BitSet}.
	 * @param containing
	 *            The {@link BitSet} defining the positions in the partitions and the ones not. <br>
	 *            NOTE: The {@link BitSet} must be indexed with x + y * width
	 * @param invertBitSet
	 *            If false: all positions returning TRUE are seen as in the partitions.<br>
	 *            If true: all positions returning FALSE are seen as in the partitions.
	 */
	public PartitionCalculatorAlgorithm(int minX, int minY, int width, int height, BitSet containing, boolean invertBitSet) {
		this.minX = minX;
		this.minY = minY;
		this.width = width;
		this.height = height;

		this.containing = containing;
		this.invertBitSet = invertBitSet;
		this.partitionsGrid = new short[width * height];
	}

	/**
	 * Calculates the partitions. <br>
	 * The results can be accessed with the supplied getter methods.
	 */
	public void calculatePartitions() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = x + y * width;
				if (containing.get(index) ^ invertBitSet) {

					int westX = x + neighborX[0];
					int westY = y + neighborY[0];
					int northWestX = x + neighborX[1];
					int northWestY = y + neighborY[1];
					int northEastX = x + neighborX[2];
					int northEastY = y + neighborY[2];

					int partition = -1;
					int westPartition = -1;
					if (containing.get(westX + westY * width) ^ invertBitSet) {
						westPartition = partitionsGrid[westX + westY * width];
						partition = westPartition;
					}

					if (containing.get(northWestX + northWestY * width) ^ invertBitSet) {
						partition = partitionsGrid[northWestX + northWestY * width];
					}

					int northEastPartition = -1;
					if (containing.get(northEastX + northEastY * width) ^ invertBitSet) {
						northEastPartition = partitionsGrid[northEastX + northEastY * width];
						partition = northEastPartition;
					}

					if (westPartition != -1 && northEastPartition != -1 && partitions[westPartition] != partitions[northEastPartition]) {
						// mergePartitions if west and northeast are not equal but set
						short newPartition = (short) Math.min(partitions[westPartition], partitions[northEastPartition]);
						partitions[westPartition] = newPartition;
						partitions[northEastPartition] = newPartition;
						partitionsGrid[index] = newPartition;

					} else if (partition != -1) { // just set the value.
						partitionsGrid[index] = partitions[partition];

					} else { // create a new partition
						partitionsGrid[index] = nextFreePartition;
						partitions[nextFreePartition] = nextFreePartition;
						partitionBorderPositions[nextFreePartition] = new ShortPoint2D(x + minX, y + minY);

						nextFreePartition++;

						if (nextFreePartition >= partitions.length) {
							short[] oldPartitions = partitions;
							partitions = new short[oldPartitions.length * INCREASE_FACTOR];
							System.arraycopy(oldPartitions, 0, partitions, 0, oldPartitions.length);

							ShortPoint2D[] oldBorderPositions = partitionBorderPositions;
							partitionBorderPositions = new ShortPoint2D[oldBorderPositions.length * INCREASE_FACTOR];
							System.arraycopy(oldBorderPositions, 0, partitionBorderPositions, 0, oldBorderPositions.length);
						}
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
				partitionBorderPositions[compactedCount] = partitionBorderPositions[representative];
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
		return partitions[partitionsGrid[x + y * width]];
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
		return partitionBorderPositions[partitionIdx];
	}
}
