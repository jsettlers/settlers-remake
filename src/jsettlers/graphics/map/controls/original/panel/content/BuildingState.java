package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.buildings.IBuilding;

/**
 * This class saves the state parts of the building that is displayed by the
 * gui, to detect changes
 * 
 * @author michael
 */
public class BuildingState {

	private final boolean working;

	/**
	 * Saves the current state of the building
	 * 
	 * @param building
	 *            the building
	 */
	public BuildingState(IBuilding building) {
		working = building.isWorking();
		if (building instanceof IBuilding.IOccupyed) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			// TODO: use this to store how many people are occupying the
			// building.
		}
	}

	public boolean isStillInState(IBuilding building) {
		return building.isWorking() == working;
	}

}
