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
package jsettlers.algorithms.heap;

import java.util.ArrayList;

/**
 * This is a unsynchronized min heap implementation.
 * <p>
 * It can heap everything that implements {@link MinHeapable}, but every object that is added to this heap may not be added to an other heap.
 * 
 * @author andreas
 * @param <T>
 */
public class MinHeap<T extends MinHeapable> {
	private final int MIN_CAPACITY;
	private final ArrayList<T> heap = new ArrayList<>();
	private int size = 0;

	/**
	 * Creates a new min heap.
	 * 
	 * @param capacity
	 *            The minimal and initial capacity the heap should have. May not be null.
	 */
	public MinHeap(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("too smal initial capacity");
		}
		MIN_CAPACITY = capacity;
		heap.ensureCapacity(MIN_CAPACITY);
	}

	/**
	 * Adds a new element to the heap.
	 * 
	 * @param e
	 *            The element to add.
	 */
	public void insert(T e) {
		if (heap.size() > size) {
			heap.set(size, e);
		} else {
			heap.add(e);
		}
		siftUp(e, size);
		size++;
	}

	/**
	 * sifts up an element to reestablish the heap array consistency.
	 * 
	 * @param e
	 *            The element to sift.
	 */
	public void siftUp(T e) {
		siftUp(e, e.getHeapIdx());
	}

	/**
	 * Sifts up an element recursively until the heap consistency is reestablished.
	 * 
	 * @param e
	 *            The element.
	 * @param eID
	 *            The index of the element in the heap.
	 * @return returns if the element was sifted up at least one time
	 */
	private boolean siftUp(T e, int eID) {
		e.setHeapIdx(eID);
		int parentID = getParent(eID);

		T parentElement = heap.get(parentID);
		if (parentElement.getRank() > e.getRank()) {
			heap.set(parentID, e);
			heap.set(eID, parentElement);
			parentElement.setHeapIdx(eID);
			siftUp(e, parentID);
			return true;
		}

		return false;

	}

	/**
	 * Checks whether the heap is empty.
	 * 
	 * @return true if and only if the heap is empty.
	 */
	public final boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Deletes the element of the heap that has the minimal value.
	 * <p>
	 * If the heap is empty, no action is preformed.
	 * 
	 * @return The deleted element, or null.
	 */
	public T deleteMin() {
		if (size <= 0) {
			return null;
		}
		size--;
		T result = heap.get(0);
		T last = heap.get(size);

		heap.set(0, last);

		siftDown(last, 0);

		return result;
	}

	private void siftDown(T e, int pos) {
		e.setHeapIdx(pos);
		int leftChildID = getLeftChildID(pos);

		if (leftChildID < size) {
			T leftChild = heap.get(leftChildID);
			int rightChildID = getRightChildID(pos);
			T smaller;
			int smallerID;

			if (rightChildID < size) {
				T rightChild = heap.get(rightChildID);

				if (leftChild.getRank() < rightChild.getRank()) {
					smaller = leftChild;
					smallerID = getLeftChildID(pos);
				} else {
					smaller = rightChild;
					smallerID = getRightChildID(pos);
				}
			} else {
				smaller = leftChild;
				smallerID = getLeftChildID(pos);
			}

			if (smaller.getRank() < e.getRank()) {
				heap.set(pos, smaller);
				smaller.setHeapIdx(pos);
				heap.set(smallerID, e);
				siftDown(e, smallerID);
			}
		}
	}

	/**
	 * Gets the position of the second child in the array.
	 * 
	 * @param pos
	 *            The position of the parent.
	 * @return The position of the child.
	 */
	private final int getRightChildID(int pos) {
		return 2 * pos + 2;
	}

	/**
	 * Gets the position of the first child in the array.
	 * 
	 * @param pos
	 *            The position of the parent.
	 * @return The position of the child.
	 */
	private final int getLeftChildID(int pos) {
		return 2 * pos + 1;
	}

	/**
	 * Gets the position of the parent in the array.
	 * 
	 * @param pos
	 *            The position of the child.
	 * @return The position of the parent.
	 */
	private final int getParent(int pos) {
		return (pos - 1) / 2;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t");
		for (int i = 0; i < size; i++) {
			buffer.append(heap.get(i).getRank() + "\t");
		}
		return buffer.toString();
	}

	/**
	 * Clears the heap, that means that all elements are removed.
	 */
	public final void clear() {
		size = 0;
	}

	/**
	 * Gets the number of elements that are in the heap.
	 * 
	 * @return The size of the heap.
	 */
	public final int size() {
		return size;
	}

	/**
	 * Removes a given element from the heap.
	 * 
	 * @param e
	 *            The element to remove
	 * @throws java.lang.AssertionError
	 */
	public void remove(T e) throws java.lang.AssertionError {
		// int idx = getIndex(e, 0);
		int idx = e.getHeapIdx();

		// if (idx == -1)
		// return;
		assert idx != -1 : "remove wrong element";
		size--;
		T last = heap.get(size);
		heap.set(idx, last);
		if (!siftUp(last, idx)) {
			siftDown(last, idx);
		}
	}

	/**
	 * Checks the heap for consistency.
	 * 
	 * @return True if all children "know" their position.
	 */
	public boolean doFullHeapCheck() {
		for (int i = 0; i < size; i++) {
			if (heap.get(i).getHeapIdx() != i) {
				return false;
			}
		}

		return checkHeap(0);
	}

	/**
	 * Checks the heap from a given position downwards.
	 * 
	 * @param pos
	 *            The position to start checking.
	 * @return true if and only if the heap is ordered the right way.
	 */
	private boolean checkHeap(int pos) {
		T curr = heap.get(pos);
		int left = getLeftChildID(pos);
		int right = getRightChildID(pos);
		if (left < size) {
			if (heap.get(left).getRank() < curr.getRank()) {
				return false;
			} else {
				return checkHeap(left);
			}
		}
		if (right < size) {
			if (heap.get(right).getRank() < curr.getRank()) {
				return false;
			} else {
				return checkHeap(right);
			}
		}
		return true;
	}

}
