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
package jsettlers.common.utils.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

/**
 * This class implements a double linked list of {@link DoubleLinkedListItem}s.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DoubleLinkedList<T extends DoubleLinkedListItem<T>> implements Serializable, Iterable<T> {
	private static final long serialVersionUID = -8229566677756169997L;

	transient T head;
	private transient int size = 0;

	public DoubleLinkedList() {
		initHead();
	}

	@SuppressWarnings("unchecked")
	private void initHead() {
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

	public void pushEnd(T newItem) {
		newItem.next = head;
		newItem.prev = head.prev;
		newItem.prev.next = newItem;
		head.prev = newItem;

		size++;
	}

	/**
	 * Pops the first element from this list.
	 * <p />
	 * NOTE: NEVER EVER call popFront on an empty queue! There will be no internal checks!
	 * 
	 * @return
	 */
	public T popFront() {
		final T item = head.next;

		item.next.prev = head;
		head.next = item.next;
		item.next = null;
		item.prev = null;

		size--;

		return item;
	}

	/**
	 * gets the first item in the list without removing it from the list.
	 * @return
	 */
	public T getFront() {
		return head.next;
	}

	public void remove(DoubleLinkedListItem<T> item) {
		item.prev.next = item.next;
		item.next.prev = item.prev;

		size--;

		item.next = null;
		item.prev = null;
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

	/**
	 * Generates a new array of {@link DoubleLinkedList}s of the given length. The array will be filled with new {@link DoubleLinkedList} objects.
	 * 
	 * @param length
	 *            Length of the resulting array.
	 * @return
	 */
	public static <T extends DoubleLinkedListItem<T>> DoubleLinkedList<T>[] getArray(final int length) {
		@SuppressWarnings("unchecked")
		DoubleLinkedList<T>[] array = new DoubleLinkedList[length];
		for (int i = 0; i < length; i++) {
			array[i] = new DoubleLinkedList<>();
		}

		return array;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(size);

		T curr = head.next;
		for (int i = 0; i < size; i++) {
			oos.writeObject(curr);
			curr = curr.next;
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		initHead();

		int size = ois.readInt();

		for (int i = 0; i < size; i++) {
			pushEnd((T) ois.readObject());
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new DoubleLinkedListIterator<>(this);
	}

	/**
	 * Adds all elements of this list to the given {@link DoubleLinkedList}. After this operation this list will not contain any elements.
	 * 
	 * @param newList
	 *            The list to append all the elements of this list.
	 */
	public void mergeInto(DoubleLinkedList<T> newList) {
		newList.head.prev.next = this.head.next;
		this.head.next.prev = newList.head.prev;
		this.head.prev.next = newList.head;
		newList.head.prev = this.head.prev;
		newList.size += size;

		clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		T curr = head.next;
		while (curr != head) {
			result = prime * result + curr.hashCode();
			curr = curr.next;
		}
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		DoubleLinkedList<?> other = (DoubleLinkedList<?>) obj;
		if (head == null) {
			if (other.head != null)
				return false;
		} else {
			DoubleLinkedListItem<?> currThis = head.next;
			DoubleLinkedListItem<?> currOther = other.head.next;
			while (currThis != head && currOther != other.head) {
				if (!currThis.equals(currOther)) {
					return false;
				}
				currThis = currThis.next;
				currOther = currOther.next;
			}

			if (currThis != this.head || currOther != other.head) {
				return false;
			}
		}
		return size == other.size;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DoubleLinkedList [size=");
		builder.append(size);
		for (T e : this) {
			builder.append(", ");
			builder.append(e.toString());
		}
		builder.append("]");
		return builder.toString();
	}

}
