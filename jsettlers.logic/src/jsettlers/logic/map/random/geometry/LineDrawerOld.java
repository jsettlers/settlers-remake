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
package jsettlers.logic.map.random.geometry;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jsettlers.common.position.ShortPoint2D;

/**
 * A 2D line drawer.
 * <p>
 * TODO: convert it so that it can handle hex tile connections
 * 
 * @author michael
 * 
 */
public class LineDrawerOld implements Iterable<ShortPoint2D> {

	private final List<Point> points;

	public LineDrawerOld(List<Point> points) {
		if (points.size() < 2) {
			throw new IllegalArgumentException("There have to be at least 2 points in a line");
		}
		this.points = points;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new LineIterator();
	}

	/**
	 * A line drawing iterator {@link http ://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm}
	 * 
	 * @author michael
	 */
	private class LineIterator implements Iterator<ShortPoint2D> {
		Iterator<Point> pointIt;
		private int endx;
		private int endy;
		// next x,y to return. x and y are swapped if (steep) is true
		private int x;
		private int y;

		boolean finished = false;
		private int dx;
		private int dy;
		private int sx;
		private int sy;
		private int err;

		private LineIterator() {
			pointIt = points.iterator();
			Point endPoint = pointIt.next();
			endx = endPoint.x;
			endy = endPoint.y;
			startNextSegment();
		}

		private void startNextSegment() {
			x = endx;
			y = endy;
			Point endPoint = pointIt.next();
			endx = endPoint.x;
			endy = endPoint.y;

			dx = Math.abs(endx - x);
			dy = Math.abs(endy - y);

			if (x < endx) {
				sx = 1;
			} else {
				sx = -1;
			}
			if (y < endy) {
				sy = 1;
			} else {
				sy = -1;
			}

			err = dx - dy;

		}

		@Override
		public boolean hasNext() {
			return !finished;
		}

		@Override
		public ShortPoint2D next() {
			if (finished) {
				throw new NoSuchElementException();
			}
			ShortPoint2D next = new ShortPoint2D(x, y);
			if (x == endx && y == endy) {
				if (pointIt.hasNext()) {
					startNextSegment();
				} else {
					finished = true;
				}
			}

			int e2 = err * 2;
			if (e2 > -dy) {
				err -= dy;
				x += sx;
			}
			if (e2 < dx) {
				err += dx;
				y += sy;
			}

			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}

	}

}
