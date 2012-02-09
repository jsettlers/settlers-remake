package jsettlers.algorithms.path.astar.heap;

import jsettlers.logic.algorithms.path.astar.heap.IHeapRankSupplier;
import jsettlers.logic.algorithms.path.astar.heap.MinHeap;

public class HeapTester {

	public static void main(String args[]) {
		MinHeap heap = new MinHeap(new TestRankSupplier(), 20);

		heap.insert(0);
		heap.insert(1);
		heap.insert(2);
		heap.insert(3);
		heap.insert(4);
		heap.insert(5);
		heap.insert(6);
		heap.insert(7);
		heap.insert(8);
		heap.insert(9);
		heap.insert(10);

		checkConsitency(heap);
		System.out.println(heap.toString());

		heap.remove(4);
		heap.deleteMin();
		heap.insert(11);
		heap.insert(12);
		heap.deleteMin();
		heap.insert(13);

		checkConsitency(heap);
		System.out.println(heap.toString());
	}

	private static void checkConsitency(MinHeap heap) {
		if (heap.doFullHeapCheck()) {
			System.out.println("heap consistent");
		} else {
			System.err.println("heap inconsistent!!!");
		}
	}

	private static class TestRankSupplier implements IHeapRankSupplier {

		float[] ranks = { 3, 4, 5, 6, 3.5f, 8, 1, 2, 3, 4, 5, 6, 7, 8, 98 };
		int[] heapIdx = new int[ranks.length];

		@Override
		public float getHeapRank(int identifier) {
			return ranks[identifier];
		}

		@Override
		public int getHeapIdx(int identifier) {
			return heapIdx[identifier];
		}

		@Override
		public void setHeapIdx(int identifier, int idx) {
			heapIdx[identifier] = idx;
		}

	}
}
