package jsettlers.logic.algorithms.construction;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.RelativePoint;

/**
 * Interface offering the methods needed by {@link ConstructionMarksThread}.
 * 
 * @author Andreas Eberle
 */
public interface IConstructionMarkableMap {

	/**
	 * Sets or removes a construction mark
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param set
	 *            If true, the construction mark shall be set, otherwise, it shall be removed.
	 * @param flattenPositions
	 *            The positions that need to be flattened to position this building. This value might be null whenever set is false.
	 */
	void setConstructMarking(int x, int y, boolean set, RelativePoint[] flattenPositions);

	/**
	 * @return width of map.
	 */
	short getWidth();

	/**
	 * @return height of map
	 */
	short getHeight();

	/**
	 * Checks if the given position is valid to build a building of given player that can stand on the given {@link ELandscapeType}s. Bounds checks
	 * will be done by this method.
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
	boolean canUsePositionForConstruction(int x, int y, ELandscapeType[] landscapeTypes, byte player);

}
