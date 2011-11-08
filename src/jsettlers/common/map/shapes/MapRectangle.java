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
public class MapRectangle implements IMapArea {
	private static final long serialVersionUID = -5451513891892255692L;

	private final short minx;
	private final short miny;
	private final short width;
	private final short height;

	public MapRectangle(short minx, short miny, short width, short height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Shape Size is negative");
		}
		this.minx = minx;
		this.miny = miny;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean contains(ISPosition2D position) {
		return contains(position.getX(), position.getY());
	}

	public boolean contains(int x, int y) {
		if (!containsLine(y)) {
			return false;
		}
		if (x < getLineStartX(y - miny) || x > getLineEndX(y - miny)) {
			return false;
		}
		return true;
	}

	public boolean containsLine(int y) {
		return y >= miny && y < miny + height;
	}

	@Override
	public Iterator<ISPosition2D> iterator() {
		return new RectangleIterator();
	}

	private int getOffsetForLine(int line) {
		return line / 2;
	}

	/**
	 * Gets the first x coordinate contained by a line.
	 * 
	 * @param line
	 *            The line relative to the first line of this rectangle.
	 */
	public int getLineStartX(int line) {
		return minx + getOffsetForLine(line);
	}

	/**
	 * Gets the first x coordinate contained by a line.
	 * 
	 * @param line
	 *            The line relative to the first line of this rectangle.
	 */
	public int getLineEndX(int line) {
		return getLineStartX(line) + this.width - 1;
	}

	public int getLineY(int line) {
		return miny + line;
	}

	public short getLines() {
		return height;
	}

	public short getLineLength() {
		return width;
	}

	private class RectangleIterator implements Iterator<ISPosition2D> {
		private int relativex = 0;
		private int relativey = 0;

		@Override
		public boolean hasNext() {
			return relativey < height && width > 0;
		}

		@Override
		public ISPosition2D next() {
			if (relativey < height && width > 0) {
				int x = getLineStartX(relativey) + relativex;
				int y = getLineY(relativey);
				ShortPoint2D pos = new ShortPoint2D(x, y);
				relativex++;
				if (relativex >= width) {
					relativex = 0;
					relativey++;
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
