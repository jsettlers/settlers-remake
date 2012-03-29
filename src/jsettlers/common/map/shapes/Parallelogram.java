package jsettlers.common.map.shapes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a parallelogram on the map.
 * <p>
 * That means an area which is just constrained by the x and y coordinate.
 * 
 * @author michael
 */
public class Parallelogram implements IMapArea {
	private static final long serialVersionUID = -8093699931739836499L;

	private final short minx;
	private final short miny;
	private final short maxx;
	private final short maxy;

	/**
	 * Creates a new shape form (minx, miny) to (maxx, maxy) including.
	 * 
	 * @param minx
	 *            The minimal x pixel
	 * @param miny
	 *            The minimal y coordiante a pixel has.
	 * @param maxx
	 *            The max x
	 * @param maxy
	 *            The max y
	 */
	public Parallelogram(short minx, short miny, short maxx, short maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	/**
	 * Creates a shape that contains only one pixel.
	 * 
	 * @param minx
	 * @param miny
	 */
	public Parallelogram(short minx, short miny) {
		this.minx = minx;
		this.maxx = minx;
		this.miny = miny;
		this.maxy = miny;

	}

	@Override
	public boolean contains(ShortPoint2D position) {
		int x = position.getX();
		int y = position.getY();
		return x >= minx && x <= maxx && y >= miny && y <= maxy;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new ParallelogramIterator();
	}

	class ParallelogramIterator implements Iterator<ShortPoint2D> {
		int x = minx;
		int y = miny;

		@Override
		public boolean hasNext() {
			return y <= maxy && x <= maxx; // maxx check for empty.
		}

		@Override
		public ShortPoint2D next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			ShortPoint2D position = new ShortPoint2D(x, y);
			x++;
			if (x > maxx) {
				x = minx;
				y++;
			}
			return position;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
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
}
