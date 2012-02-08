package jsettlers.logic.algorithms.path.astar.heap;

import java.util.ArrayList;

/**
 * This is an unsynchronized min heap implementation.
 * 
 * @author Andreas Eberle
 */
public final class MinHeap {
	private final ArrayList<Integer> heap = new ArrayList<Integer>();
	private int size = 0;
	private final IHeapRankSupplier rankSupplier;

	/**
	 * Creates a new min heap.
	 * 
	 * @param rankSupplier
	 *            user of the heap
	 * @param startCapacity
	 *            The minimal and initial capacity the heap should have. May not be null.
	 */
	public MinHeap(IHeapRankSupplier rankSupplier, int startCapacity) {
		this.rankSupplier = rankSupplier;
		if (startCapacity <= 0) {
			throw new IllegalArgumentException("too smal initial capacity");
		}
		heap.ensureCapacity(startCapacity);
	}

	/**
	 * Adds a new element to the heap.
	 * 
	 * @param elementID
	 *            The element to add.
	 */
	public void insert(int elementID) {
		if (heap.size() > size) {
			heap.set(size, elementID);
		} else {
			heap.add(elementID);
		}
		siftUp(elementID, size);
		size++;
	}

	/**
	 * sifts up an element to reestablish the heap array consistency.
	 * 
	 * @param elementID
	 *            The element to sift.
	 */
	public void siftUp(int elementID) {
		siftUp(elementID, rankSupplier.getHeapIdx(elementID));
	}

	/**
	 * Sifts up an element recursively until the heap consistency is reestablished.
	 * 
	 * @param elementID
	 *            The element.
	 * @param idx
	 *            The index of the element in the heap.
	 * @return returns true if the element was sifted up at least one time
	 */
	private boolean siftUp(int elementID, int idx) {
		rankSupplier.setHeapIdx(elementID, idx);
		int parentIdx = getParentIdx(idx);

		int parentElementID = heap.get(parentIdx);
		if (rankSupplier.getHeapRank(parentElementID) > rankSupplier.getHeapRank(elementID)) {
			heap.set(parentIdx, elementID);
			heap.set(idx, parentElementID);
			rankSupplier.setHeapIdx(parentElementID, idx);
			siftUp(elementID, parentIdx);
			return true;
		}

		return false;

	}

	/**
	 * Checks whether the heap is empty.
	 * 
	 * @return true if the heap is empty.
	 */
	public final boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Deletes the element of the heap that has the minimal value.
	 * <p>
	 * If the heap is empty, no action is performed.
	 * 
	 * @return The deleted element, or -1 if the heap was empty.
	 */
	public int deleteMin() {
		if (size <= 0) {
			return -1;
		}
		size--;
		int resultID = heap.get(0);
		int lastID = heap.get(size);

		heap.set(0, lastID);

		siftDown(lastID, 0);

		return resultID;
	}

	private void siftDown(int elementID, int idx) {
		rankSupplier.setHeapIdx(elementID, idx);
		int leftChildIdx = getLeftChildIdx(idx);

		if (leftChildIdx < size) {
			int leftChildID = heap.get(leftChildIdx);
			int rightChildIdx = getRightChildIdx(idx);
			int smaller;
			int smallerIdx;

			if (rightChildIdx < size) {
				int rightChildID = heap.get(rightChildIdx);

				if (rankSupplier.getHeapRank(leftChildID) < rankSupplier.getHeapRank(rightChildID)) {
					smaller = leftChildID;
					smallerIdx = getLeftChildIdx(idx);
				} else {
					smaller = rightChildID;
					smallerIdx = getRightChildIdx(idx);
				}
			} else {
				smaller = leftChildID;
				smallerIdx = getLeftChildIdx(idx);
			}

			if (rankSupplier.getHeapRank(smaller) < rankSupplier.getHeapRank(elementID)) {
				heap.set(idx, smaller);
				rankSupplier.setHeapIdx(smaller, idx);
				heap.set(smallerIdx, elementID);
				siftDown(elementID, smallerIdx);
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
	private final int getRightChildIdx(int pos) {
		return 2 * pos + 2;
	}

	/**
	 * Gets the position of the first child in the array.
	 * 
	 * @param pos
	 *            The position of the parent.
	 * @return The position of the child.
	 */
	private final int getLeftChildIdx(int pos) {
		return 2 * pos + 1;
	}

	/**
	 * Gets the position of the parent in the array.
	 * 
	 * @param pos
	 *            The position of the child.
	 * @return The position of the parent.
	 */
	private final int getParentIdx(int pos) {
		return (pos - 1) / 2;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t");
		for (int i = 0; i < size; i++) {
			buffer.append(rankSupplier.getHeapRank(heap.get(i)) + "\t");
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
	 * @param element
	 *            The element to remove
	 */
	public void remove(int element) {
		Integer idx = rankSupplier.getHeapIdx(element);

		size--;
		int last = heap.get(size);
		heap.set(idx, last);
		if (!siftUp(last, idx)) {
			siftDown(last, idx);
		}
	}

	/**
	 * Checks the heap for consistency.
	 * 
	 * @return true if all children "know" their position and the children always have a higher rank than their parents
	 */
	public boolean doFullHeapCheck() {
		for (int i = 0; i < size; i++) {
			if (rankSupplier.getHeapIdx(heap.get(i)) != i) {
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
		int curr = heap.get(pos);
		int left = getLeftChildIdx(pos);
		int right = getRightChildIdx(pos);
		if (left < size) {
			if (rankSupplier.getHeapRank(heap.get(left)) < rankSupplier.getHeapRank(curr)) {
				return false;
			} else {
				return checkHeap(left);
			}
		}
		if (right < size) {
			if (rankSupplier.getHeapRank(heap.get(right)) < rankSupplier.getHeapRank(curr)) {
				return false;
			} else {
				return checkHeap(right);
			}
		}
		return true;
	}

}
