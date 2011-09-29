package jsettlers.logic.algorithms.path;

import jsettlers.common.map.shapes.MapNeighboursArea;

/**
 * interface to specify the methods needed by pathfinders to operate on a map
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPathMap {
	short getHeight();

	short getWidth();

	boolean isBlocked(IPathCalculateable requester, short x, short y);

	/**
	 * 
	 */
	/**
	 * Gets the neighbour tile positions, as an array with items: (x, y)
	 * 
	 * @param x
	 *            The center x
	 * @param y
	 *            The center y
	 * @param neighbors
	 *            The array to load with the neighbours. If it is null or does not match the reqirements, a new one is created.
	 * @return The resulting array
	 * @see MapNeighboursArea
	 */
	short[][] getNeighbors(short x, short y, short[][] neighbors);

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
