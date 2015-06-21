package jsettlers.graphics.action;

import jsettlers.common.buildings.EBuildingType;

/**
 * Instructs the logic to compute and display the constructions mark for the given building.
 * 
 * @author michael
 *
 */
public class ShowConstructionMarksAction extends Action {
	private EBuildingType buildingType;

	public ShowConstructionMarksAction(EBuildingType buildingType) {
		super(EActionType.SHOW_CONSTRUCTION_MARK);
		this.buildingType = buildingType;

	}

	/**
	 * Get the type of building to show the construction marks for.
	 * 
	 * @return The type or <code>null</code> if none should be displayed.
	 */
	public EBuildingType getBuildingType() {
		return buildingType;
	}
}
