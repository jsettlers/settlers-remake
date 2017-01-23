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
import jsettlers.common.utils.collections.ISerializablePredicate;
import jsettlers.common.utils.collections.IteratorFilter;
import jsettlers.common.utils.collections.IteratorFilter.FilteredIterator;
import jsettlers.common.utils.coordinates.CoordinateStream;

/**
 * This extension of {@link IteratorFilter} is specialized for the usage with {@link IMapArea}s. It allows to use the contains method on the filtered
 * area.
 * 
 * @author Andreas Eberle
 * 
 */
public class FilteredMapArea implements IMapArea {
	private static final long serialVersionUID = -5136044315417473251L;
	private final IMapArea iterable;
	private final ISerializablePredicate<ShortPoint2D> predicate; // FIXME replace with ICoordinatePredicate

	public FilteredMapArea(IMapArea iterable, ISerializablePredicate<ShortPoint2D> predicate) {
		this.iterable = iterable;
		this.predicate = predicate;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new FilteredIterator<>(iterable.iterator(), predicate);
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return predicate.evaluate(position) && iterable.contains(position);
	}

	@Override
	public CoordinateStream stream() {
		return iterable.stream().filter((x, y) -> predicate.evaluate(new ShortPoint2D(x, y)));
	}
}
