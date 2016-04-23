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
package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

public class MapCircleIterator implements Iterator<ShortPoint2D> {
	protected int currenty;

	protected float currentLineHalfWidth;
	// x from vertical center line of circle.
	protected float currentx;

	protected final float radius;

	protected final short centerx;

	protected final short centery;

	private final MapCircle circle;

	public MapCircleIterator(MapCircle circle) {
		this.circle = circle;
		radius = circle.getRadius();
		currenty = -(int) (radius / MapCircle.Y_SCALE);
		currentLineHalfWidth = circle.getHalfLineWidth(currenty);
		currentx = -currentLineHalfWidth;

		centerx = circle.getCenterX();
		centery = circle.getCenterY();
	}

	@Override
	public final boolean hasNext() {
		return currenty < radius / MapCircle.Y_SCALE;
	}

	/**
	 * NOTE: nextX() MUST BE CALLED after this call to progress to the next position.
	 * 
	 * @return gives the x of the current iterator position
	 */
	public final int nextY() {
		return currenty + centery;
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
		int y = currenty + centery;
		int x = computeNextXAndProgress();

		return new ShortPoint2D(x, y);
	}

	private final int computeNextXAndProgress() {
		int x = (int) Math.ceil(.5f * currenty + currentx) + centerx;

		currentx++;
		if (currentx > currentLineHalfWidth) {
			// next line
			currenty++;
			currentLineHalfWidth = circle.getHalfLineWidth(currenty);
			currentx = -currentLineHalfWidth;
		}
		return x;
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException("Cannot remove from a circle.");
	}
}