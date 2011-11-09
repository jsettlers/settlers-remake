package jsettlers.logic.algorithms.construction;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ISPosition2D;

/**
 * Interface offering the methods needed by {@link ConstructMarksThread}.
 * 
 * @author Andreas Eberle
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
	 * Checks if the given building type can be build at the given position
	 * 
	 * @param x
	 *            x coordinate of the target position
	 * @param y
	 *            y coordinate of the target position
	 * @param type
	 *            type of building to be checked.
	 * @param player
	 *            player
	 * @return true if a building can be positioned at the given position<br>
	 *         false otherwise.
	 */
	boolean canConstructAt(short x, short y, EBuildingType type, byte player);

}
