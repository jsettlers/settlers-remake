package jsettlers.common.utils.collections;

import java.util.Iterator;

/**
 * Filters specific elements from a list's iterator.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public final class FilterIterator<T> implements Iterator<T>, Iterable<T> {

	private final IPredicate<T> predicate;
	private final Iterator<T> iter;
	private T next;

	/**
	 * Creates a new {@link FilterIterator} that filters the given list by the given predicate. (Keeps the element if the predicate is true).
	 * 
	 * @param collection
	 *            List to be filtered.
	 * @param predicate
	 *            {@link IPredicate} used to determine if an element should be kept (predicate evaluates to true) or dropped.
	 */
	public FilterIterator(Iterable<T> collection, IPredicate<T> predicate) {
		this.iter = collection.iterator();
		this.predicate = predicate;

		findNext();
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
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (next == null) {
			findNext();
		}
		return next != null;
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
