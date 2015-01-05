package jsettlers.graphics.map.controls.original.panel.content;

import java.util.Arrays;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EPriority;

/**
 * This class saves the state parts of the building that is displayed by the gui, to detect changes
 * 
 * @author michael
 */
public class BuildingState {

	private final EPriority priority;
	private final EPriority[] supportedPriorities;

	/**
	 * Saves the current state of the building
	 * 
	 * @param building
	 *            the building
	 */
	public BuildingState(IBuilding building) {
		priority = building.getPriority();
		supportedPriorities = building.getSupportedPriorities();
		if (building instanceof IBuilding.IOccupyed) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			// TODO: use this to store how many people are occupying the
			// building.
		}
	}

	public boolean isStillInState(IBuilding building) {
		return building.getPriority() == priority
				&& Arrays.equals(supportedPriorities,
						building.getSupportedPriorities());
	}

}
