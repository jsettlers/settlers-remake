/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms.partitions;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;

import java.util.BitSet;

/**
 * An algorithm to calculate partitions for a given set of positions.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionCalculatorAlgorithm {
	public static final short NO_PARTITION = 0;
	public static final short BLOCKED_PARTITION = 1;
	public static final short NUMBER_OF_RESERVED_PARTITIONS = 2;

	private static final int NUMBER_OF_START_PARTITIONS = 1000;
	private static final int[] neighborX = { EDirection.WEST.gridDeltaX, EDirection.NORTH_WEST.gridDeltaX, EDirection.NORTH_EAST.gridDeltaX };
	private static final int[] neighborY = { EDirection.WEST.gridDeltaY, EDirection.NORTH_WEST.gridDeltaY, EDirection.NORTH_EAST.gridDeltaY };
	private static final int INCREASE_FACTOR = 2;

	private final int minX;
	private final int minY;
	private final int width;
	private final int height;
	private final BitSet containing;
	private final short[] partitionsGrid;
	private final IBlockingProvider blockingProvider;

	private short[] partitions = new short[NUMBER_OF_START_PARTITIONS];
	private ShortPoint2D[] partitionBorderPositions = new ShortPoint2D[NUMBER_OF_START_PARTITIONS];

	private short nextFreePartition = NUMBER_OF_RESERVED_PARTITIONS;
	private short neededPartitions;

	/**
	 * Creates a new {@link PartitionCalculatorAlgorithm}. The given positions are positions in the created partitions. Non mentioned positions are
	 * seen as outside of partitions.
	 * 
	 * @param positions
	 *            The positions of the calculated partitions.
	 * @param blockingProvider
	 *            Provides the information if a position is blocked or not.
	 * @param minX
	 *            The smallest x coordinate in the list of positions.
	 * @param minY
	 *            The smallest y coordinate in the list of positions.
	 * @param maxX
	 *            The biggest x coordinate in the list of positions.
	 * @param maxY
	 *            The biggest y coordinate in the list of positions.
	 */
	public PartitionCalculatorAlgorithm(CoordinateStream positions, IBlockingProvider blockingProvider, int minX, int minY, int maxX, int maxY) {
		this.minX = --minX; // this increases the window, so that no position can lay on the border.
		this.minY = --minY;
		maxX++;
		maxY++;
		this.width = maxX - minX + 1;
		this.height = maxY - minY + 1;

		this.blockingProvider = blockingProvider;

		this.containing = new BitSet(width * height);
		positions.forEach((x, y) -> containing.set((x - this.minX) + (y - this.minY) * width));

		this.partitionsGrid = new short[width * height];
		this.partitions[BLOCKED_PARTITION] = BLOCKED_PARTITION;
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
	 * @param blockingProvider
	 *            Provides the information if a position is blocked or not.
	 */
	public PartitionCalculatorAlgorithm(int minX, int minY, int width, int height, BitSet containing, IBlockingProvider blockingProvider) {
		this.minX = minX;
		this.minY = minY;
		this.width = width;
		this.height = height;

		this.containing = containing;
		this.blockingProvider = blockingProvider;
		this.partitionsGrid = new short[width * height];
		this.partitions[BLOCKED_PARTITION] = BLOCKED_PARTITION;
	}

	/**
	 * Calculates the partitions. <br>
	 * The results can be accessed with the supplied getter methods.
	 */
	public void calculatePartitions() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = x + y * width;
				if (containing.get(index)) {

					if (blockingProvider.isBlocked(minX + x, minY + y)) {
						partitionsGrid[index] = BLOCKED_PARTITION;
						continue;
					}

					int westX = x + neighborX[0];
					int westY = y + neighborY[0];
					int northWestX = x + neighborX[1];
					int northWestY = y + neighborY[1];
					int northEastX = x + neighborX[2];
					int northEastY = y + neighborY[2];

					int partition = -1;
					int westPartition = -1;
					int northEastPartition = -1;

					if (containing.get(westX + westY * width)) {
						short currPartition = partitions[partitionsGrid[westX + westY * width]];
						if (currPartition != BLOCKED_PARTITION) {
							westPartition = currPartition;
							partition = currPartition;
						}
					}

					if (containing.get(northWestX + northWestY * width)) {
						short currPartition =  partitions[partitionsGrid[northWestX + northWestY * width]];
						if (currPartition != BLOCKED_PARTITION) {
							partition = currPartition;
						}
					}

					if (containing.get(northEastX + northEastY * width)) {
						short currPartition =  partitions[partitionsGrid[northEastX + northEastY * width]];
						if (currPartition != BLOCKED_PARTITION) {
							northEastPartition = currPartition;
							partition = currPartition;
						}
					}

					if (westPartition != -1 && northEastPartition != -1 && partitions[westPartition] != partitions[northEastPartition]) {
						// mergePartitions if west and northeast are not equal, not blocked but already set
						short newPartition = (short) Math.min(partitions[westPartition], partitions[northEastPartition]);
						partitions[westPartition] = newPartition;
						partitions[northEastPartition] = newPartition;
						partitionsGrid[index] = newPartition;

					} else if (partition != -1) { // just set the value.
						partitionsGrid[index] = partitions[partition];

					} else { // create a new partition
						partitionsGrid[index] = createNewPartition(y, x);
					}
				}
			}
		}

		// post processing
		normalizePartitions();
	}

	private short createNewPartition(int y, int x) {
		short newPartition = nextFreePartition;

		partitions[newPartition] = newPartition;
		partitionBorderPositions[newPartition] = new ShortPoint2D(minX + x, minY + y);

		nextFreePartition++;

		if (nextFreePartition >= partitions.length) {
			increasePartitionArraySize();
		}

		return newPartition;
	}

	/**
	 * Normalizes the partitions and compacts them.
	 */
	private void normalizePartitions() {
		short[] compacted = new short[nextFreePartition];
		compacted[NO_PARTITION] = NO_PARTITION;
		compacted[BLOCKED_PARTITION] = BLOCKED_PARTITION;

		short compactedCount = NUMBER_OF_RESERVED_PARTITIONS;

		for (short i = NUMBER_OF_RESERVED_PARTITIONS; i < nextFreePartition; i++) {
			short representative = i;
			short nextRep;

			while (representative != (nextRep = partitions[representative])) {
				representative = nextRep;
			}

			if (compacted[representative] == 0) {
				short newPartitionId = compactedCount++;
				compacted[representative] = newPartitionId;
				partitionBorderPositions[newPartitionId] = partitionBorderPositions[representative];
			}

			partitions[i] = representative;
			compacted[i] = compacted[representative];
		}
		partitions = compacted;
		neededPartitions = compactedCount;
	}

	private void increasePartitionArraySize() {
		short[] oldPartitions = partitions;
		partitions = new short[oldPartitions.length * INCREASE_FACTOR];
		System.arraycopy(oldPartitions, 0, partitions, 0, oldPartitions.length);

		ShortPoint2D[] oldBorderPositions = partitionBorderPositions;
		partitionBorderPositions = new ShortPoint2D[oldBorderPositions.length * INCREASE_FACTOR];
		System.arraycopy(oldBorderPositions, 0, partitionBorderPositions, 0, oldBorderPositions.length);
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
