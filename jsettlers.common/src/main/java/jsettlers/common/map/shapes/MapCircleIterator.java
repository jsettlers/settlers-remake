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
package jsettlers.common.map.shapes;

import jsettlers.common.position.ShortPoint2D;

import java.util.Iterator;

public class MapCircleIterator implements Iterator<ShortPoint2D> {

	private final MapCircle circle;
	protected final short centerX;
	protected final short centerY;
	protected final float radius;

	/**
	 * x from vertical center line of circle.
	 */
	protected float currentX;
	protected int currentY;

	protected float currentLineHalfWidth;


	public MapCircleIterator(MapCircle circle) {
		this.circle = circle;
		radius = circle.getRadius();
		currentY = -(int) (radius / MapCircle.Y_SCALE);
		currentLineHalfWidth = circle.getHalfLineWidth(currentY);
		currentX = -currentLineHalfWidth;

		centerX = circle.getCenterX();
		centerY = circle.getCenterY();
	}

	@Override
	public final boolean hasNext() {
		return currentY < radius / MapCircle.Y_SCALE;
	}

	/**
	 * NOTE: nextX() MUST BE CALLED after this call to progress to the next position.
	 * 
	 * @return gives the x of the current iterator position
	 */
	public final int nextY() {
		return currentY + centerY;
	}

	/**
	 * NOTE: nextY() MUST BE CALLED before this method is called!
	 * 
	 * @return gives the x of the current iterator position
	 */
	public final int nextX() {
		return computeNextXAndProgress();
	}

	@Override
	public ShortPoint2D next() {
		int y = currentY + centerY;
		int x = computeNextXAndProgress();

		return new ShortPoint2D(x, y);
	}

	private final int computeNextXAndProgress() {
		int x = (int) Math.ceil(.5f * currentY + currentX) + centerX;

		currentX++;
		if (currentX > currentLineHalfWidth) {
			// next line
			currentY++;
			currentLineHalfWidth = circle.getHalfLineWidth(currentY);
			currentX = -currentLineHalfWidth;
		}
		return x;
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException("Cannot remove from a circle.");
	}
}