/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.map.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;

import java.util.Iterator;

/**
 * This is a line on a map.
 *
 * @author Michael Zangl
 */
public class MapLine implements IMapArea {
	private static final long serialVersionUID = -5934808006015795383L;

	private final ShortPoint2D start;
	private final ShortPoint2D end;

	public MapLine(ShortPoint2D start, ShortPoint2D end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		for (ShortPoint2D p : this) {
			if (p.equals(position)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(int x, int y) {
		return stream().contains(x, y);
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new LineIterator();
	}

	@Override
	public CoordinateStream stream() {
		return stream(start, end);
	}

	public static CoordinateStream stream(ShortPoint2D start, ShortPoint2D end) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				if (!function.apply(start.x, start.y)) {
					return false;
				}

				int currX = start.x;
				int currY = start.y;

				while (!end.equals(currX, currY)) {
					EDirection dir = EDirection.getApproxDirection(currX, currY, end.x, end.y);
					currX += dir.gridDeltaX;
					currY += dir.gridDeltaY;

					if (!function.apply(currX, currY)) {
						return false;
					}
				}

				return true;
			}
		};
	}

	private class LineIterator implements Iterator<ShortPoint2D> {
		private ShortPoint2D next = start;

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public ShortPoint2D next() {
			ShortPoint2D next = this.next;
			if (next.equals(end)) {
				this.next = null;
			} else {
				EDirection dir = EDirection.getApproxDirection(next, end);
				this.next = dir.getNextHexPoint(next);
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
