package jsettlers.common.buildings;

import jsettlers.common.movable.EMovableType;

/**
 * This interface allows the graphics to get the occupyer in a building.
 * @author michael
 *
 */
public interface IBuildingOccupyer {
	/**
	 * gets the type of the movable
	 * @return The type.
	 */
	public EMovableType getMovableType();
	
	/**
	 * The place the occupyer was placed
	 * @return The place, as given by the building type.
	 */
	public OccupyerPlace getPlace();
}
