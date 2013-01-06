package jsettlers.logic.algorithms.path.astar.queues.bucket;

import jsettlers.logic.algorithms.path.astar.lists.DoubleLinkedIntList;
import jsettlers.logic.algorithms.path.astar.lists.DoubleLinkedIntListItem;
import jsettlers.logic.algorithms.path.astar.normal.AbstractMinPriorityQueue;
import jsettlers.logic.algorithms.path.astar.queues.IRankSupplier;

public final class MinBucketQueue extends AbstractMinPriorityQueue {
	/**
	 * NOTE: The number of buckets MUST BE a power of 2!!
	 */
	private static final int NUMBER_OF_BUCKETS = 4;
	private static final int MODULO_MASK = NUMBER_OF_BUCKETS - 1;

	private final IRankSupplier rankSupplier;
	private final DoubleLinkedIntList[] buckets;
	private final DoubleLinkedIntListItem[] handles;

	private int minIdx = 0;
	private int size = 0;

	public MinBucketQueue(IRankSupplier rankSupplier, int maxNumberOfIds) {
		this.rankSupplier = rankSupplier;

		this.buckets = new DoubleLinkedIntList[NUMBER_OF_BUCKETS];
		for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
			this.buckets[i] = new DoubleLinkedIntList();
		}

		this.handles = new DoubleLinkedIntListItem[maxNumberOfIds];
		for (int i = 0; i < maxNumberOfIds; i++) {
			handles[i] = new DoubleLinkedIntListItem(i);
		}
	}

	@Override
	public void insert(int elementId) {
		buckets[getRankIdx(elementId)].pushFront(handles[elementId]);
		size++;
	}

	private final int getRankIdx(int elementId) {
		float rank = rankSupplier.getRank(elementId);
		return getRankIdx(rank);
	}

	private final int getRankIdx(float rank) {
		return ((int) rank) & MODULO_MASK;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void remove(int elementId) {
		remove(elementId, getRankIdx(elementId));
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
	public void increasedPriority(int elementId, float oldRank) {
		remove(elementId, getRankIdx(oldRank));
		insert(elementId);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

}
