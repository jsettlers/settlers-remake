package jsettlers.logic.algorithms.construction;

import jsettlers.common.landscape.ELandscapeType;

/**
 * Interface offering the methods needed by {@link ConstructMarksThread}.
 * 
 * @author Andreas Eberle
 */
public interface IConstructionMarkableMap {

	/**
	 * Sets the given value to the given position.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param value
	 *            value to be set as construction mark value.
	 */
	void setConstructMarking(short x, short y, byte value);

	/**
	 * @return width of map.
	 */
	short getWidth();

	/**
	 * @return height of map
	 */
	short getHeight();

	/**
	 * gets the height of the given tile position.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return height of the tile at the fiven position
	 */
	byte getHeightAt(short x, short y);

	/**
	 * Checks if the given position is valid to build a building of given player that can stand on the given {@link ELandscapeType}s
	 * 
	 * @param x
	 *            x coordinate of the target position
	 * @param y
	 *            y coordinate of the target position
	 * @param landscapeTypes
	 *            allowed landscape types
	 * @param player
	 *            player
	 * @return true if a building can be positioned at the given position<br>
	 *         false otherwise.
	 */
	boolean canUsePositionForConstruction(short x, short y, ELandscapeType[] landscapeTypes, byte player);

	boolean isInBounds(short x, short y);
}
