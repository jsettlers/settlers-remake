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
package jsettlers.common.utils.collections;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Filters specific elements from a list's iterator.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class IteratorFilter<T> implements Iterable<T> {

	protected final Iterable<? extends T> iterable;
	protected final IPredicate<T> predicate;

	/**
	 * Creates a new {@link IteratorFilter} that filters the given list by the given predicate. (Keeps the element if the predicate is true).
	 * 
	 * @param iterable
	 *            List to be filtered.
	 * @param predicate
	 *            {@link IPredicate} used to determine if an element should be kept (predicate evaluates to true) or dropped.
	 */
	public IteratorFilter(Iterable<? extends T> iterable, IPredicate<T> predicate) {
		this.iterable = iterable;
		this.predicate = predicate;
	}

	@Override
	public Iterator<T> iterator() {
		return new FilteredIterator<>(iterable.iterator(), predicate);
	}

	/**
	 * 
	 * @return A list containing all elements of this iterator.
	 */
	public ArrayList<T> toList() {
		ArrayList<T> resultList = new ArrayList<>();
		for (T curr : this) {
			resultList.add(curr);
		}
		return resultList;
	}

	public static class FilteredIterator<T> implements Iterator<T> {
		private final Iterator<? extends T> iterator;
		private final IPredicate<T> predicate;
		private T next;

		public FilteredIterator(Iterator<? extends T> iterator, IPredicate<T> predicate) {
			this.iterator = iterator;
			this.predicate = predicate;

			findNext();
		}

		@Override
		public boolean hasNext() {
			if (next == null) {
				findNext();
			}
			return next != null;
		}

		private void findNext() {
			while (iterator.hasNext()) {
				T curr = iterator.next();
				if (predicate.evaluate(curr)) {
					this.next = curr;
					break;
				}
			}
		}

		@Override
		public T next() {
			T curr = this.next;
			this.next = null;
			return curr;
		}

		@Override
		public void remove() {
			assert this.next == null : "remove may only be called after next() and before hasNext()";
			iterator.remove();
		}
	}
}
