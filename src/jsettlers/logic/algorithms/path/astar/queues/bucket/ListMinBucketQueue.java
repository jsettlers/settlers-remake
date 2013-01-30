package jsettlers.logic.algorithms.path.astar.queues.bucket;

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
