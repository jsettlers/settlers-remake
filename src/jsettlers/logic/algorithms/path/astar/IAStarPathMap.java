package jsettlers.logic.algorithms.path.astar;

import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IAStarPathMap {

	float getHeuristicCost(short sx, short sy, short tx, short ty);

	boolean isBlocked(IPathCalculateable requester, short x, short y);

	float getCost(short sx, short sy, short tx, short ty);

	/**
	 * only for debugging
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	void markAsOpen(short x, short y);

	/**
	 * only for debugging
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	void markAsClosed(short x, short y);

}
