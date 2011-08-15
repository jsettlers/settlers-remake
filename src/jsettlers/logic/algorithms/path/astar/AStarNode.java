package jsettlers.logic.algorithms.path.astar;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.heap.MinHeapable;

/**
 * A single node in the search graph
 */
public class AStarNode implements MinHeapable, ISPosition2D {
	/** The x coordinate of the node */
	short x;
	/** The y coordinate of the node */
	short y;

	float cost = 0;

	AStarNode parent = null;

	float heuristic;
	/** The search depth of this node */
	int depth = 0;

	int inList = 0;

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
	public AStarNode(short x, short y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the parent of this node
	 * 
	 * @param parent
	 *            The parent node which lead us to this node
	 * @return The depth we have no reached in searching
	 */
	public final int setParent(AStarNode parent) {
		assert !parent.equals(this) : "parent to itself";

		depth = parent.depth + 1;
		this.parent = parent;

		return depth;
	}

	@Override
	public float getRank() {
		return heuristic + cost;
	}

	public boolean equals(AStarNode m) {
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

	@Override
	public short getX() {
		return x;
	}

	@Override
	public short getY() {
		return y;
	}

	@Override
	public boolean equals(ISPosition2D other) {
		return other.getX() == x && other.getY() == y;
	}
	
	@Override
	public int hashCode() {
	    return ShortPoint2D.hashCode(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}
}
