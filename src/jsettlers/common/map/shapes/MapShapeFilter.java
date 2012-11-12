package jsettlers.common.map.shapes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsettlers.common.position.ShortPoint2D;

/**
 * This filter creates the union of an other shape with the map.
 * 
 * @author michael
 */
public class MapShapeFilter implements IMapArea {
	private static final long serialVersionUID = 3531866238303135719L;

	private final IMapArea base;
	private final int width;
	private final int height;

	/**
	 * Creates a new filtered shape
	 * 
	 * @param base
	 *            The base shape
	 * @param width
	 *            The width of the map
	 * @param height
	 *            The height of the map
	 */
	public MapShapeFilter(IMapArea base, int width, int height) {
		this.base = base;
		this.width = width;
		this.height = height;
	}

	/**
	 * This method checks if the point is contained by the map and by the shape.
	 */
	@Override
	public boolean contains(ShortPoint2D position) {
		if (inMap(position)) {
			return base.contains(position);
		} else {
			return false;
		}
	}

	private boolean inMap(ShortPoint2D position) {
		int x = position.x;
		int y = position.y;
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new FilteredIterator();
	}

	private class FilteredIterator implements Iterator<ShortPoint2D> {
		private ShortPoint2D next;
		private Iterator<ShortPoint2D> iterator;

		public FilteredIterator() {
			iterator = base.iterator();
			searchNext();
		}

		private void searchNext() {
			do {
				if (iterator.hasNext()) {
					next = iterator.next();
				} else {
					next = null;
				}
			} while (next != null && !inMap(next));
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public ShortPoint2D next() {
			if (next == null) {
				throw new NoSuchElementException();
			}
			ShortPoint2D result = next;
			searchNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
