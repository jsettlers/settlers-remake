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
package jsettlers.mapcreator.tools.buffers;

import java.util.Iterator;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class represents a two dimensional array, used as helper class for editing tools
 * 
 * @author Andreas Butti
 */
public class ByteMapArea implements IMapArea {
	private static final long serialVersionUID = 1L;
	private final byte[][] status;

	/**
	 * Constructor
	 * 
	 * @param status
	 *            Byte map
	 */
	public ByteMapArea(byte[][] status) {
		this.status = status;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		short y = position.y;
		short x = position.x;
		return x >= 0 && y >= 0 && x < status.length && y < status[x].length && status[x][y] > Byte.MAX_VALUE / 2;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new It();
	}

	private class It implements Iterator<ShortPoint2D> {
		private int x = 0;
		private int y = 0;

		private It() {
			searchNext();
		}

		private void searchNext() {
			do {
				x++;
				if (x >= status.length) {
					x = 0;
					y++;
				}
			} while (y < status[x].length && status[x][y] <= Byte.MAX_VALUE / 2);
		}

		@Override
		public boolean hasNext() {
			return y < status[x].length;
		}

		@Override
		public ShortPoint2D next() {
			if (!hasNext()) {
				throw new IllegalStateException();
			}
			ShortPoint2D point2d = new ShortPoint2D(x, y);
			searchNext();
			return point2d;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
