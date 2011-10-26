package jsettlers.logic.algorithms.path.astar;

import jsettlers.logic.algorithms.path.IPathCalculateable;

public interface IAStarPathMap {
	short getHeight();

	short getWidth();

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

	/**
	 * Checks if the coordinates are on the map.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return true if the given coordinates are on the map.
	 */
	boolean isInBounds(short x, short y);
}
