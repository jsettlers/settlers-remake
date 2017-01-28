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
import jsettlers.common.utils.coordinates.ICoordinateConsumer;

import java.util.Iterator;

public class MapNeighboursArea implements IMapArea {
	private static final long serialVersionUID = -6205409596340280969L;

	private final int x;
	private final int y;

	public MapNeighboursArea(ShortPoint2D center) {
		this.x = center.x;
		this.y = center.y;
	}

	public MapNeighboursArea(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return contains(position.x, position.y);
	}

	@Override
	public boolean contains(int x, int y) {
		return EDirection.getDirection(x - this.x, y - this.y) != null;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new NeighbourIterator();
	}

	private class NeighbourIterator implements Iterator<ShortPoint2D> {
		int directionIndex = 0;

		@Override
		public boolean hasNext() {
			return directionIndex < EDirection.VALUES.length;
		}

		@Override
		public ShortPoint2D next() {
			return EDirection.VALUES[directionIndex++].getNextHexPoint(x, y);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static void iterate(int x, int y, ICoordinateConsumer consumer) {
		for (EDirection direction : EDirection.VALUES) {
			consumer.accept(x + direction.gridDeltaX, y + direction.gridDeltaY);
		}
	}

	@Override
	public CoordinateStream stream() {
		return stream(x, y);
	}

	private static CoordinateStream stream(int centerX, int centerY) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				for (EDirection direction : EDirection.values()) {
					int x = direction.gridDeltaX + centerX;
					int y = direction.gridDeltaY + centerY;

					if (!function.apply(x, y)) {
						return false;
					}
				}
				return true;
			}
		};
	}
}
