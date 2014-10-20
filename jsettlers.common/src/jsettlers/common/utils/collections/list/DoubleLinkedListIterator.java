package jsettlers.common.utils.collections.list;

import java.util.Iterator;

/**
 * {@link Iterator} implementation for the {@link DoubleLinkedList} class.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
class DoubleLinkedListIterator<T extends DoubleLinkedListItem<T>> implements Iterator<T> {

	private final DoubleLinkedList<T> list;
	private T next;

	public DoubleLinkedListIterator(DoubleLinkedList<T> list) {
		this.list = list;
		this.next = list.head.next;
	}

	@Override
	public boolean hasNext() {
		return next != list.head;
	}

	@Override
	public T next() {
		T currNext = next;
		next = next.next;
		return currNext;
	}

	@Override
	public void remove() {
		list.remove(next.prev);
	}
}
