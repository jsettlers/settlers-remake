package jsettlers.logic.algorithms.path.astar.lists;

/**
 * This class implements a double linked list of integer values.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DoubleLinkedIntList {

	private DoubleLinkedIntListItem head = new DoubleLinkedIntListItem();
	private int size = 0;

	public DoubleLinkedIntListItem pushFront(int value) {
		DoubleLinkedIntListItem newItem = new DoubleLinkedIntListItem(value);

		newItem.next = head.next;
		newItem.prev = head;
		newItem.next.prev = newItem;
		head.next = newItem;

		size++;

		return newItem;
	}

	public int popFront() {
		final DoubleLinkedIntListItem item = head.next;

		item.next.prev = head;
		head.next = item.next;

		size--;

		return item.value;
	}

	public void remove(DoubleLinkedIntListItem item) {
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

		DoubleLinkedIntListItem curr = head.next;
		for (int i = 0; i < size; i++) {
			assert curr != head && (i == 0 || curr.prev != head);
			curr = curr.next;
		}
	}

	public DoubleLinkedIntList deepCopy() {
		DoubleLinkedIntList copy = new DoubleLinkedIntList();

		DoubleLinkedIntListItem origCurr = head.next;
		DoubleLinkedIntListItem copyLast = copy.head;

		for (int i = 0; i < size; i++) {
			DoubleLinkedIntListItem newItem = new DoubleLinkedIntListItem(origCurr.value);
			newItem.next = head;
			newItem.prev = copyLast;
			copyLast.next = newItem;
			copy.head.prev = newItem;

			copyLast = newItem;
			origCurr = origCurr.next;
		}
		copy.size = size;

		return copy;
	}
}
