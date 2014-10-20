package jsettlers.logic.algorithms.path.astar.queues.bucket;

import jsettlers.logic.algorithms.path.arrays.IntArrayStack;

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
