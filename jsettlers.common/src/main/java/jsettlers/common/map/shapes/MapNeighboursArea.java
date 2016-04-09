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

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

public class MapNeighboursArea implements IMapArea {
	private static final long serialVersionUID = -6205409596340280969L;

	private final short x;
	private final short y;

	public MapNeighboursArea(ShortPoint2D center) {
		this.x = center.x;
		this.y = center.y;
	}

	public MapNeighboursArea(final short x, final short y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		for (ShortPoint2D pos : this) {
			if (pos.equals(position)) {
				return true;
			}
		}
		return false;
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
}
