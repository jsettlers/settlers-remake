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

/**
 * This is a line on a map.
 * 
 * @author Michael Zangl
 */
public class MapLine implements IMapArea {
	private static final long serialVersionUID = -5934808006015795383L;

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
	public Iterator<ShortPoint2D> iterator() {
		return new LineIterator();
	}
}
