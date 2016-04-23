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
package jsettlers.algorithms.path.astar.queues.bucket;

import jsettlers.common.utils.collections.list.DoubleLinkedIntListItem;
import jsettlers.common.utils.collections.list.DoubleLinkedList;

/**
 * This class implements a minimum bucket priority queue with double liked lists.
 * <p />
 * {@link #insert(int, float)}, {@link #increasedPriority(int, float, float)}, {@link #size()} and {@link #isEmpty()} are in O(1)<br>
 * {@link #clear()} and {@link #deleteMin()} are in O({@link #NUMBER_OF_BUCKETS}) = O({@value #NUMBER_OF_BUCKETS}) that's actually a constant value.
 * 
 * @author Andreas Eberle
 * 
 */
public final class ListMinBucketQueue extends AbstractBucketQueue {
	/**
	 * NOTE: The number of buckets MUST BE a power of 2!!
	 */
	public static final int NUMBER_OF_BUCKETS = 4;
	private static final int MODULO_MASK = NUMBER_OF_BUCKETS - 1;

	private final DoubleLinkedList<DoubleLinkedIntListItem>[] buckets;
	private final DoubleLinkedIntListItem[] handles;

	private int minIdx = 0;
	private int size = 0;

	public ListMinBucketQueue(int maxNumberOfIds) {
		this.buckets = DoubleLinkedList.getArray(NUMBER_OF_BUCKETS);

		this.handles = new DoubleLinkedIntListItem[maxNumberOfIds];
		for (int i = 0; i < maxNumberOfIds; i++) {
			handles[i] = new DoubleLinkedIntListItem(i);
		}
	}

	@Override
	public void insert(int elementId, float rank) {
		buckets[getRankIdx(rank)].pushFront(handles[elementId]);
		size++;
	}

	private static final int getRankIdx(float rank) {
		return ((int) rank) & MODULO_MASK;
	}

	@Override
	public int size() {
		return size;
	}

	private final void remove(int elementId, int rankIdx) {
		buckets[rankIdx].remove(handles[elementId]);
		size--;
	}

	@Override
	public void clear() {
		for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
			this.buckets[i].clear();
		}

		size = 0;
	}

	@Override
	public int deleteMin() {
		while (buckets[minIdx].isEmpty()) {
			minIdx = (minIdx + 1) & MODULO_MASK;
		}
		size--;

		final int elementId = buckets[minIdx].popFront().value;

		return elementId;
	}

	@Override
	public void increasedPriority(int elementId, float oldRank, float newRank) {
		remove(elementId, getRankIdx(oldRank));
		insert(elementId, newRank);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

}
