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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class defines an area on the map that is a rectangle on the screen.
 *
 * @author michael
 */
public final class MapRectangle implements IMapArea {
	private static final long serialVersionUID = -5451513891892255692L;

	private final short minX;
	private final short minY;
	final short width;
	final short height;

	public MapRectangle(short minx, short miny, short width, short height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Shape Size is negative");
		}
		this.minX = minx;
		this.minY = miny;
		this.width = width;
		this.height = height;
	}

	public MapRectangle(int minx, int miny, int width, int height) {
		this((short) minx, (short) miny, (short) width, (short) height);
	}

	@Override
	public final boolean contains(ShortPoint2D position) {
		return contains(position.x, position.y);
	}

	public final boolean contains(int x, int y) {
		if (!containsLine(y)) {
			return false;
		}
		return !(x < getLineStartX(y - getMinY()) || x > getLineEndX(y - getMinY()));
	}

	public final boolean containsLine(int y) {
		return y >= getMinY() && y < getMinY() + height;
	}

	@Override
	public final Iterator<ShortPoint2D> iterator() {
		return new RectangleIterator();
	}

	private final static int getOffsetForLine(int line) {
		return line / 2;
	}

	/**
	 * Gets the first x coordinate contained by a line.
	 *
	 * @param line
	 *            The line relative to the first line of this rectangle.
	 */
	public final int getLineStartX(int line) {
		return minX + getOffsetForLine(line);
	}

	/**
	 * Gets the last x coordinate contained by a line.
	 *
	 * @param line
	 *            The line relative to the first line of this rectangle.
	 */
	public final int getLineEndX(int line) {
		return getLineStartX(line) + this.width - 1;
	}

	public final int getLineY(int line) {
		return minY + line;
	}

	public final short getLines() {
		return height;
	}

	public final short getLineLength() {
		return width;
	}

	public short getMinX() {
		return minX;
	}

	public short getMinY() {
		return minY;
	}

	public final short getWidth() {
		return width;
	}

	public final short getHeight() {
		return height;
	}

	@Override
	public CoordinateStream stream() {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				for (int relativeY = 0; relativeY < height; relativeY++) {
					int lineStartX = getLineStartX(relativeY);

					for (int relativeX = 0; relativeX < width; relativeX++) {
						int x = lineStartX + relativeX;
						int y = getLineY(relativeY);
						if (!function.apply(x, y)) {
							return false;
						}
					}
				}
				return true;
			}
		};
	}

	private class RectangleIterator implements Iterator<ShortPoint2D> {
		private int relativeX = 0;
		private int relativeY = 0;

		@Override
		public boolean hasNext() {
			return relativeY < height && width > 0;
		}

		@Override
		public ShortPoint2D next() {
			if (relativeY < height && width > 0) {
				int x = getLineStartX(relativeY) + relativeX;
				int y = getLineY(relativeY);
				ShortPoint2D pos = new ShortPoint2D(x, y);
				relativeX++;
				if (relativeX >= width) {
					relativeX = 0;
					relativeY++;
				}
				return pos;
			} else {
				throw new NoSuchElementException("There are no more elements in the shape");
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove tiles from a Shape");
		}
	}
}
