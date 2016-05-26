/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.algorithms.traversing.borders.IBorderVisitor;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MutableInt;

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
	 * @param debugColorSetable
	 * 
	 * @param partitionObjects
	 * @param partitions
	 * @param width
	 * @param pos1
	 *            The first position.
	 * @param pos2
	 *            The second position.
	 * @param partition
	 *            The partition of both positions.
	 * @return true if both positions are connected by the given partition.<br>
	 *         false if the positions are not connected.
	 */
	public static boolean isPartitionDivided(Partition[] partitionObjects, short[] partitions, short width, ShortPoint2D pos1,
			MutableInt partition1Size, ShortPoint2D pos2, MutableInt partition2Size, short partition) {

		return posNotOnBorder(partitionObjects, partitions, width, pos1, pos2, partition, partition1Size)
				&& posNotOnBorder(partitionObjects, partitions, width, pos2, pos1, partition, partition2Size);
	}

	/**
	 * NOTE: The call to this method is different if the given positions are swapped!
	 * 
	 * @param debugColorSetable
	 * @param partitionObjects
	 * @param partitions
	 * @param width
	 * @param startPosition
	 * @param checkPosition
	 * @param partition
	 * @return
	 */
	private static boolean posNotOnBorder(final Partition[] partitionObjects, final short[] partitions, final short width,
			final ShortPoint2D startPosition, final ShortPoint2D checkPosition, final short partition,
			MutableInt partitionSize) {
		final short checkPositionX = checkPosition.x;
		final short checkPositionY = checkPosition.y;

		boolean pos2NotOnBorder = BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return partitionObjects[partitions[x + y * width]].partitionId == partition;
			}
		}, startPosition, new IBorderVisitor() {
			@Override
			public boolean visit(int insideX, int insideY, int outsideX, int outsideY) {
				return checkPositionX != insideX || checkPositionY != insideY;
			}
		}, false, partitionSize);
		return pos2NotOnBorder;
	}
}
