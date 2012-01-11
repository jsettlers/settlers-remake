package jsettlers.common.map.shapes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

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

	@Override
	public final boolean contains(ISPosition2D position) {
		return contains(position.getX(), position.getY());
	}

	public final boolean contains(int x, int y) {
		if (!containsLine(y)) {
			return false;
		}
		if (x < getLineStartX(y - getMinY()) || x > getLineEndX(y - getMinY())) {
			return false;
		}
		return true;
	}

	public final boolean containsLine(int y) {
		return y >= getMinY() && y < getMinY() + height;
	}

	@Override
	public final Iterator<ISPosition2D> iterator() {
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
		return getMinX() + getOffsetForLine(line);
	}

	/**
	 * Gets the first x coordinate contained by a line.
	 * 
	 * @param line
	 *            The line relative to the first line of this rectangle.
	 */
	public final int getLineEndX(int line) {
		return getLineStartX(line) + this.width - 1;
	}

	public final int getLineY(int line) {
		return getMinY() + line;
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

	private class RectangleIterator implements Iterator<ISPosition2D> {
		private int relativeX = 0;
		private int relativeY = 0;

		@Override
		public boolean hasNext() {
			return relativeY < height && width > 0;
		}

		@Override
		public ISPosition2D next() {
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

	public final short getWidth() {
		return width;
	}

	public final short getHeight() {
		return height;
	}

}
