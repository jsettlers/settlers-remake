/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.algorithms.landmarks;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.ITraversingVisitor;
import jsettlers.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * Thread to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of
 * the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public final class EnclosedBlockedAreaFinderAlgorithm {

	public static final void checkLandmark(IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D startPos) {
		final short startX = startPos.x;
		final short startY = startPos.y;

		if (grid.isBlocked(startX, startY))
			return;

		short startPartition = grid.getPartitionAt(startPos.x, startPos.y);

		for (EDirection currDir : EDirection.VALUES) {
			ShortPoint2D currPos = currDir.getNextHexPoint(startX, startY);

			if (grid.isBlocked(currPos.x, currPos.y)) {
				if (needsRelabel(grid, containingProvider, currPos, startPartition)) {
					// System.out.println("relabel needed at " + currX + "|" + currY + " with startPartition: " + startPartition);
					relabel(grid, containingProvider, currPos, startPartition);
				}
			}
		}
	}

	private static void relabel(final IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D blockedStartPos,
			final short newPartition) {
		ITraversingVisitor visitor = new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				grid.setPartitionAt(x, y, newPartition);
				return true;
			}
		};
		AreaTraversingAlgorithm.traverseArea(containingProvider, visitor, blockedStartPos, grid.getWidth(), grid.getHeight());
	}

	/**
	 * Checks if the blocked partition given by the coordinates blockedX and blockedY is surrounded by the given partition.
	 * 
	 * @param containingProvider2
	 * @param grid2
	 * 
	 * @param blockedX
	 * @param blockedY
	 * @param partition
	 * @param blockedStartPos
	 * @return
	 */
	private static boolean needsRelabel(final IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider,
			ShortPoint2D blockedStartPos,
			final short partition) {
		return BorderTraversingAlgorithm.traverseBorder(containingProvider, blockedStartPos, new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return grid.getPartitionAt((short) x, (short) y) == partition;
			}
		}, true);
	}
}
