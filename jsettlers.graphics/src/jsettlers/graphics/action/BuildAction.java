package jsettlers.graphics.action;

import jsettlers.common.buildings.EBuildingType;

/**
 * This is a build action.
 * @author michael
 *
 */
public class BuildAction extends Action {

	private final EBuildingType building;

	/**
	 * Creates a new build action.
	 * 
	 * @param building
	 *            The building to be built.
	 */
	public BuildAction(EBuildingType building) {
		super(EActionType.BUILD);
		this.building = building;
	}

	/**
	 * gets the building that corresponds with this action, if the action is an
	 * build action.
	 * 
	 * @return The building, <code>null</code> if it is not a build action.
	 */
	public EBuildingType getBuilding() {
		return this.building;
	}

}
