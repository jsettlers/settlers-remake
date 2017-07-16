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
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a parallelogram on the map.
 * <p>
 * That means an area which is just constrained by the x and y coordinate.
 * 
 * @author michael
 */
public class Parallelogram implements IMapArea {
	private static final long serialVersionUID = -8093699931739836499L;

	private final short minX;
	private final short minY;
	private final short maxX;
	private final short maxY;

	/**
	 * Creates a new shape form (minX, minY) to (maxX, maxY) including.
	 * 
	 * @param minX
	 *            The minimal x pixel
	 * @param minY
	 *            The minimal y coordiante a pixel has.
	 * @param maxX
	 *            The max x
	 * @param maxY
	 *            The max y
	 */
	public Parallelogram(short minX, short minY, short maxX, short maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return contains(position.x, position.y);
	}

	@Override
	public boolean contains(int x, int y) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new ParallelogramIterator();
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("{");
		int trim = 0;
		for (ShortPoint2D point : this) {
			str.append(point + ", ");
			trim = 2;
		}
		return str.substring(0, str.length() - trim) + "}";
	}

	@Override
	public CoordinateStream stream() {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				for (int y = minY; y <= maxY; y++) {
					for (int x = minX; x <= maxX; x++) {
						if (!function.apply(x, y)) {
							return false;
						}
					}
				}
				return true;
			}
		};
	}

	private class ParallelogramIterator implements Iterator<ShortPoint2D> {
		int x = minX;
		int y = minY;

		@Override
		public boolean hasNext() {
			return y <= maxY && x <= maxX; // maxx check for empty.
		}

		@Override
		public ShortPoint2D next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			ShortPoint2D position = new ShortPoint2D(x, y);
			x++;
			if (x > maxX) {
				x = minX;
				y++;
			}
			return position;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
