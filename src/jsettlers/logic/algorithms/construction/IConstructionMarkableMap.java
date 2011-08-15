package jsettlers.logic.algorithms.construction;

import jsettlers.common.position.ISPosition2D;

/**
 * Interface offering the methods needed by {@link ConstructMarksCalculator}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IConstructionMarkableMap {

	/**
	 * Sets the given value to the given position.
	 * 
	 * @param pos
	 *            position the value will be set to
	 * @param value
	 *            value to be set as construction mark value.
	 */
	void setConstructMarking(ISPosition2D pos, byte value);

	/**
	 * Checks if a building can be positioned at the given position
	 * 
	 * @param position
	 *            position to be checked.
	 * @param player
	 *            player
	 * @return true if a building can be positioned at the given position<br>
	 *         false otherwise.
	 */
	boolean isBuildingPlaceable(ISPosition2D position, byte player);

	/**
	 * 
	 * @return width of map.
	 */
	short getWidth();

	/**
	 * 
	 * @return height of map
	 */
	short getHeight();

	/**
	 * gets the height of the given tile position.
	 * 
	 * @param pos
	 *            position to be looked for
	 * @return height of the tile at the fiven position
	 */
	byte getHeightAt(ISPosition2D pos);
}
