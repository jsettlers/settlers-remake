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
package jsettlers.logic.map.grid.partition;

import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableInt;
import jsettlers.logic.map.grid.partition.PartitionsListingBorderVisitor.BorderPartitionInfo;

/**
 * This class implements an algorithm used by the {@link PartitionsGrid} to check if two positions of a partition are divided.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsDividedTester {

	/**
	 * Private constructor to prevent the creation of objects.
	 */
	private PartitionsDividedTester() {
	}

	/**
	 * Tests if the given positions (that need to lie on the border of the given partition) are connected by the given partition.
	 * 
	 * @param partitionObjects
	 * @param partitions
	 * @param width
	 * @param partition1
	 * @param partition1Size
	 * @param partition2
	 * @param partition2Size
	 * @return true if both positions are connected by the given partition.<br>
	 *         false if the positions are not connected.
	 */
	public static boolean isPartitionDivided(Partition[] partitionObjects, short[] partitions, short width, BorderPartitionInfo partition1,
			MutableInt partition1Size, BorderPartitionInfo partition2, MutableInt partition2Size) {

		assert partition1.partitionId == partition2.partitionId;

		return posNotOnBorder(partitionObjects, partitions, width, partition1.positionOfPartition,
				partition1.insideNeighborPosition, partition2.positionOfPartition, partition1.partitionId, partition1Size)
				&& posNotOnBorder(partitionObjects, partitions, width, partition2.positionOfPartition,
						partition2.insideNeighborPosition, partition1.positionOfPartition, partition1.partitionId, partition2Size);
	}

	/**
	 * NOTE: The call to this method is different if the given positions are swapped!
	 *
	 * @param partitionObjects
	 * @param partitions
	 * @param width
	 * @param insideStartPosition
	 * @param checkPosition
	 * @param partitionSize
	 * @return
	 */
	private static boolean posNotOnBorder(final Partition[] partitionObjects, final short[] partitions, final short width,
			final ShortPoint2D insideStartPosition, final ShortPoint2D outsideStartPosition, final ShortPoint2D checkPosition,
			final short partitionId, MutableInt partitionSize) {

		final short checkPositionX = checkPosition.x;
		final short checkPositionY = checkPosition.y;

		return BorderTraversingAlgorithm.traverseBorder(
				(x, y) -> partitionObjects[partitions[x + y * width]].partitionId == partitionId, insideStartPosition, outsideStartPosition,
				(insideX, insideY, outsideX, outsideY) -> checkPositionX != insideX || checkPositionY != insideY, false, partitionSize);
	}
}