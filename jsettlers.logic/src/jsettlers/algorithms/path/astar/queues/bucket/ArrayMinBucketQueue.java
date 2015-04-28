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

import jsettlers.algorithms.path.arrays.IntArrayStack;

public final class ArrayMinBucketQueue extends AbstractBucketQueue {
	/**
	 * NOTE: The number of buckets MUST BE a power of 2!!
	 */
	private static final int NUMBER_OF_BUCKETS = 4;
	private static final int MODULO_MASK = NUMBER_OF_BUCKETS - 1;
	private static final int BUCKET_START_SIZE = 10000;

	private final IntArrayStack[] buckets;
	private final int[] handles;

	private int minIdx = 0;
	private int size = 0;

	public ArrayMinBucketQueue(int maxNumberOfIds) {
		this.buckets = new IntArrayStack[NUMBER_OF_BUCKETS];
		for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
			this.buckets[i] = new IntArrayStack(BUCKET_START_SIZE);
		}

		this.handles = new int[maxNumberOfIds];
	}

	@Override
	public void insert(int elementId, float rank) {
		handles[elementId] = buckets[getRankIdx(rank)].pushFront(elementId);
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

		final int elementId = buckets[minIdx].popFront();

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
