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
public final class IteratorFilter<T> implements Iterable<T> {

	private final Iterable<T> iterable;
	private final IPredicate<T> predicate;

	/**
	 * Creates a new {@link IteratorFilter} that filters the given list by the given predicate. (Keeps the element if the predicate is true).
	 * 
	 * @param iterable
	 *            List to be filtered.
	 * @param predicate
	 *            {@link IPredicate} used to determine if an element should be kept (predicate evaluates to true) or dropped.
	 */
	public IteratorFilter(Iterable<T> iterable, IPredicate<T> predicate) {
		this.iterable = iterable;
		this.predicate = predicate;
	}

	@Override
	public Iterator<T> iterator() {
		return new FilteredIterator<T>(iterable.iterator(), predicate);
	}

	/**
	 * 
	 * @return A list containing all elements of this iterator.
	 */
	public ArrayList<T> toList() {
		ArrayList<T> resultList = new ArrayList<T>();
		for (T curr : this) {
			resultList.add(curr);
		}
		return resultList;
	}

	public static class FilteredIterator<T> implements Iterator<T> {
		private final IPredicate<T> predicate;
		private final Iterator<T> iter;
		private T next;

		public FilteredIterator(Iterator<T> iter, IPredicate<T> predicate) {
			this.iter = iter;
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
			while (iter.hasNext()) {
				T curr = iter.next();
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
			iter.remove();
		}
	}
}
