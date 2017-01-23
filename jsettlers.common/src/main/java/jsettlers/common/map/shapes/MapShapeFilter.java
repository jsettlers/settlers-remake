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
import java.util.NoSuchElementException;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;

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

	@Override
	public CoordinateStream stream() {
		return base.stream().filterBounds(width, height);
	}
}
