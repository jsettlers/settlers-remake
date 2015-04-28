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
package jsettlers.logic.map.random.voronoi;

/**
 * This is a tree that orders the items by y-coordinate.
 * 
 * @author michael
 */
public class BeachTree implements Beach {
	private BeachTreeItem root;

	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public void add(VoronioSite point, CircleEventManager mgr) {
		if (isEmpty()) {
			root = new BeachLinePart(point);
		} else {
			// double y = point.getY();
			// TODO
		}
	}

	@Override
	public BeachLinePart getBeachAt(double sweepx, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the part of the beach line below the given item
	 * 
	 * @param brokenArc
	 *            The current arc
	 * @return The arc below the current
	 */
	@Override
	public BeachLinePart getBottom(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the part of the beach line above the given item
	 * 
	 * @param brokenArc
	 *            The current arc
	 * @return The arc above the current
	 */
	@Override
	public BeachLinePart getTop(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the separation point above the given point
	 * 
	 * @param middle
	 *            The middle point
	 * @return The found separator, or <code>null</code> if it was the topmost line.
	 */
	public BeachSeparator findSeparatorAbove(BeachLinePart middle) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the separation point below the given point
	 * 
	 * @param middle
	 *            The middle point
	 * @return The found separator, or <code>null</code> if it was the topmost line.
	 */
	public BeachSeparator findSeparatorBelow(BeachLinePart middle) {
		// TODO Auto-generated method stub
		return null;
	}
}
