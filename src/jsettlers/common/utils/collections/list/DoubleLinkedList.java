package jsettlers.common.utils.collections.list;

/**
 * This class implements a double linked list of {@link DoubleLinkedListItem}s.
 * <p />
 * NOTE: Type safety is not preserved when doing a {@link #popFront()} on an empty queue.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DoubleLinkedList<T extends DoubleLinkedListItem<T>> {

	private final T head;
	private int size = 0;

	@SuppressWarnings("unchecked")
	public DoubleLinkedList() {
		head = (T) new DoubleLinkedListItem<T>();
		head.next = head;
		head.prev = head;
	}

	public void pushFront(T newItem) {
		newItem.next = head.next;
		newItem.prev = head;
		newItem.next.prev = newItem;
		head.next = newItem;

		size++;
	}

	public T popFront() {
		final T item = head.next;

		item.next.prev = head;
		head.next = item.next;
		item.next = null;
		item.prev = null;

		size--;

		return item;
	}

	public void remove(DoubleLinkedListItem<T> item) {
		item.prev.next = item.next;
		item.next.prev = item.prev;

		size--;
	}

	public int size() {
		return size;
	}

	public void clear() {
		head.next = head;
		head.prev = head;
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void assertCorrect() {
		assert size == 0 || (head.next != head && head.prev != head);

		DoubleLinkedListItem<T> curr = head.next;
		for (int i = 0; i < size; i++) {
			assert curr != head && (i == 0 || curr.prev != head);
			curr = curr.next;
		}
	}
}
