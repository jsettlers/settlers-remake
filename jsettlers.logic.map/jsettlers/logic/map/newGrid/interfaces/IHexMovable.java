package jsettlers.logic.map.newGrid.interfaces;

import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;

/**
 * Defines a movable on the hex grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IHexMovable extends IMovable {

	/**
	 * sets this object to be selected or not
	 * 
	 * @param selected
	 *            true if the movable has to be selected
	 */
	@Override
	void setSelected(boolean selected);

	/**
	 * push this movable<br>
	 * (request it to leave this position)
	 * 
	 * @param from
	 *            pushed from this movable
	 */
	void push(IHexMovable from);

	/**
	 * Used to reduce the health of a movable by another in a fight.
	 * 
	 * @param strength
	 *            this is the offensive strength of the hitting movable
	 */
	void hit(float strength);

	void initGoingToNextTile();

	IHexMovable getPushedFrom();

	ISPosition2D getNextTile();

	/**
	 * 
	 * @param building
	 *            the occupyable building that requests this movable
	 * @return true if this movable was a soldier<br>
	 *         false if it is no soldier
	 */
	boolean setOccupyableBuilding(IOccupyableBuilding building);

}
