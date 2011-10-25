package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.logic.algorithms.heap.MinHeapable;

/**
 * A single node in the search graph
 */
public class DijkstraNode implements MinHeapable {
	/** The x coordinate of the node */
	short x;
	/** The y coordinate of the node */
	short y;

	float depth = 0;

	int inList = 0;

	DijkstraNode parent = null;

	/** index in open list */
	int heapIdx = -1;

	/**
	 * Create a new node
	 * 
	 * @param x
	 *            The x coordinate of the node
	 * @param y
	 *            The y coordinate of the node
	 */
	public DijkstraNode(short x, short y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public float getRank() {
		return depth;
	}

	public boolean equals(DijkstraNode m) {
		return m.x == x && m.y == y;
	}

	@Override
	public final int getHeapIdx() {
		return heapIdx;
	}

	@Override
	public final void setHeapIdx(int idx) {
		heapIdx = idx;
	}

	public boolean equals(short x, short y) {
		return this.x == x && this.y == y;
	}

}
