package jsettlers.common.buildings;

import jsettlers.common.movable.IMovable;

/**
 * This interface allows the graphics to get the occupyer in a building.
 * 
 * @author michael
 * 
 */
public interface IBuildingOccupyer {
	/**
	 * gets the movable
	 * 
	 * @return The type.
	 */
	public IMovable getMovable();

	/**
	 * The place the occupyer was placed
	 * 
	 * @return The place, as given by the building type.
	 */
	public OccupyerPlace getPlace();
}
