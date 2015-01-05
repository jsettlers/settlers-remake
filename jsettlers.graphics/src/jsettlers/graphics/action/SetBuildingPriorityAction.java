package jsettlers.graphics.action;

import jsettlers.common.material.EPriority;

/**
 * This {@link Action} is used to set the priority of a building. When it is fired, the priority is set for the current selection.
 * 
 * @author Andreas Eberle
 */
public class SetBuildingPriorityAction extends Action {

	private final EPriority newPriority;

	public SetBuildingPriorityAction(EPriority newPriority) {
		super(EActionType.SET_BUILDING_PRIORITY);
		this.newPriority = newPriority;
	}

	/**
	 * @return Returns the new priority that shall be set to the selected building.
	 */
	public EPriority getNewPriority() {
		return newPriority;
	}

}
