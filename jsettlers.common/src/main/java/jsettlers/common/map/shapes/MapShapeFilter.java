/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.IteratorFilter;
import jsettlers.common.utils.coordinates.CoordinateStream;

import java.util.Iterator;

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
		return inMap(position) && base.contains(position);
	}

	@Override
	public boolean contains(int x, int y) {
		return 0 <= x && x < width && 0 <= y && y < height && base.contains(x, y);
	}

	boolean inMap(ShortPoint2D position) {
		int x = position.x;
		int y = position.y;
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new IteratorFilter.FilteredIterator<>(base.iterator(), this::inMap);
	}

	@Override
	public CoordinateStream stream() {
		return base.stream().filterBounds(width, height);
	}
}
