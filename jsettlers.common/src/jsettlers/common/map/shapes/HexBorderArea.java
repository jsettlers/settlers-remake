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

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class HexBorderArea implements IMapArea {
	private static final long serialVersionUID = -5609476544086214928L;

	private final short radius;
	private short centerX;
	private short centerY;

	public HexBorderArea(ShortPoint2D center, short radius) {
		this(center.x, center.y, radius);
	}

	public HexBorderArea(short centerX, short centerY, short radius) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new HexBorderIterator(centerX, centerY, radius);
	}

	public short getNumberOfElements() {
		return (short) (radius * 6);
	}

	public static final class HexBorderIterator implements Iterator<ShortPoint2D> {
		private final short r;
		private final short centerX;
		private final short centerY;

		private byte nextCorner = 1;
		private short x, y;

		public HexBorderIterator(short centerX, short centerY, short radius) {
			this.centerX = centerX;
			this.centerY = centerY;
			this.r = radius;

			x = centerX;
			y = (short) (centerY - radius);
		}

		@Override
		public boolean hasNext() {
			return nextCorner < 7;
		}

		@Override
		public ShortPoint2D next() {
			switch (nextCorner) {
			case 1:
				x++;
				y++;
				if (y == centerY) { // then x == centerX + r
					nextCorner++;
				}
				break;

			case 2:
				y++;
				if (y == centerY + r) {
					nextCorner++;
				}
				break;

			case 3:
				x--;
				if (x == centerX) {
					nextCorner++;
				}
				break;

			case 4:
				x--;
				y--;
				if (y == centerY) { // then x == centerX - r
					nextCorner++;
				}
				break;

			case 5:
				y--;
				if (y == centerY - r) {
					nextCorner++;
				}
				break;

			case 6:
				x++;
				if (x >= centerX) {
					nextCorner++;
				}
				break;
			}

			return new ShortPoint2D(x, y);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
