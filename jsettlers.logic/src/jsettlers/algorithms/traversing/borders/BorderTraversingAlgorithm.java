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
package jsettlers.algorithms.traversing.borders;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.ITraversingVisitor;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MutableInt;

/**
 * 
 * @author Andreas Eberle
 */
public final class BorderTraversingAlgorithm {

	/**
	 * No instances of this class shall be created.
	 */
	private BorderTraversingAlgorithm() {
	}

	/**
	 * Traverses the border of an area defined by the given {@link IContainingProvider} starting at {@link startPos}. The given visitor is called for
	 * every position on the outside of the area.<br>
	 * If the {@link startPos} is not surrounded by any position that is not in the area (meaning startPos is not on the border), the traversing can't
	 * be started and the visitor is never called.
	 * 
	 * @param containingProvider
	 *            {@link IContainingProvider} defining the position that are in and the ones that are outside the area.
	 * @param startPos
	 *            The start position for the traversing. This position must be in the area but at the border!
	 * @param visitor
	 *            The visitor that will be called for every border position (a border position is a position outside the border!).
	 * @param visitOutside
	 *            If true the positions on the outside will be visited.<br>
	 *            If false the inside positions will be visited.
	 * @param traversedPositions
	 *            This object will contain the number of traversed positions after the call.
	 * @return true if the whole border has been traversed.<br>
	 *         false if the traversing has been canceled by the {@link ITraversingVisitor}'s visit() method.
	 */
	public static boolean traverseBorder(final IContainingProvider containingProvider, final ShortPoint2D startPos, final ITraversingVisitor visitor,
			boolean visitOutside, MutableInt traversedPositions) {
		final int startInsideX = startPos.x;
		final int startInsideY = startPos.y;

		int insideX = startInsideX;
		int insideY = startInsideY;

		int outsideX = -1;
		int outsideY = -1;

		boolean foundOutsidePos = false;

		// determine first outside position
		for (EDirection dir : EDirection.VALUES) {
			outsideX = insideX + dir.gridDeltaX;
			outsideY = insideY + dir.gridDeltaY;

			if (!containingProvider.contains(outsideX, outsideY)) {
				foundOutsidePos = true;
				break;
			}
		}

		if (!foundOutsidePos) { // no neighbor of the start position is on the outside.
			return false;
		}

		final int startOutsideX = outsideX;
		final int startOutsideY = outsideY;

		int traversedPositionsCounter = 1;

		if (!visitor.visit(startOutsideX, startOutsideY)) {
			traversedPositions.value = traversedPositionsCounter;
			return false;
		}

		do {
			traversedPositionsCounter++;

			EDirection outInDir = EDirection.getDirection(insideX - outsideX, insideY - outsideY);
			EDirection neighborDir = outInDir.getNeighbor(-1);

			int neighborX = neighborDir.gridDeltaX + outsideX;
			int neighborY = neighborDir.gridDeltaY + outsideY;

			if (containingProvider.contains(neighborX, neighborY)) {
				insideX = neighborX;
				insideY = neighborY;

				if (!visitOutside && !visitor.visit(insideX, insideY)) {
					traversedPositions.value = traversedPositionsCounter;
					return false;
				}
			} else {
				outsideX = neighborX;
				outsideY = neighborY;

				if (visitOutside && !visitor.visit(outsideX, outsideY)) {
					traversedPositions.value = traversedPositionsCounter;
					return false;
				}
			}
		} while (insideX != startInsideX || insideY != startInsideY || outsideX != startOutsideX || outsideY != startOutsideY);

		traversedPositions.value = traversedPositionsCounter;
		return true;
	}

	public static boolean traverseBorder(final IContainingProvider containingProvider, final ShortPoint2D startPos, final ITraversingVisitor visitor,
			boolean visitOutside) {
		return traverseBorder(containingProvider, startPos, visitor, visitOutside, new MutableInt());
	}
}
