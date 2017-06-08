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
package jsettlers.algorithms.traversing.area;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.borders.IBorderVisitor;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * This algorithm offers a method to traverse a connected area with an {@link IBorderVisitor}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class AreaTraversingAlgorithm {

	/**
	 * No instances of this class shall be created.
	 */
	private AreaTraversingAlgorithm() {
	}

	/**
	 * Traverses a connected area and calls the given visitor for every position.
	 * 
	 * @param containingProvider
	 *            {@link IContainingProvider} defining what's part of the area and what isn't.
	 * @param visitor
	 *            The visitor that will be called on every position in the connected area reachable from the given start position.
	 * @param startPos
	 *            A start position somewhere in the area.
	 * @param width
	 *            The width of the area. So the maximum x value may be width - 1;
	 * @param height
	 *            The height of the area. So the maximum y value may be height - 1;
	 * 
	 * @return true if the traversing finished<br>
	 *         false if the visitor returned false at any position and therefore caused the traversing to be canceled.
	 */
	public static boolean traverseArea(IContainingProvider containingProvider, IAreaVisitor visitor, ShortPoint2D startPos, int width, int height) {
		LinkedList<ShortPoint2D> stack = new LinkedList<>();
		stack.push(startPos);
		BitSet touched = new BitSet(width * height);
		touched.set(startPos.x + startPos.y * width);

		while (!stack.isEmpty()) {
			ShortPoint2D currPos = stack.poll();
			if (!visitor.visit(currPos.x, currPos.y)) {
				return false;
			}

			for (EDirection dir : EDirection.VALUES) {
				int nextX = dir.gridDeltaX + currPos.x;
				int nextY = dir.gridDeltaY + currPos.y;

				if (0 <= nextX && nextX <= width && 0 <= nextY && nextY <= height) {
					int nextIdx = nextX + nextY * width;
					if (!touched.get(nextIdx) && containingProvider.contains(nextX, nextY)) {
						stack.push(new ShortPoint2D(nextX, nextY));
						touched.set(nextIdx);
					}
				}
			}
		}

		return true;
	}
}
