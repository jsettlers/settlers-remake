package jsettlers.input.tasks;

/**
 * Actions of the gui used to send them over the network.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EGuiAction {
	BUILD,
	SET_WORK_AREA,
	MOVE_TO,
	QUICK_SAVE,
	DESTROY_MOVABLES,
	DESTROY_BUILDING,
	STOP_WORKING,
	START_WORKING,
	CONVERT,
	SET_BUILDING_PRIORITY,
	SET_MATERIAL_DISTRIBUTION_SETTINGS,

	/**
	 * The user wants to change the order in which materials are served by bearers.
	 * 
	 * @see SetMaterialPrioritiesGuiTask
	 */
	SET_MATERIAL_PRIORITIES;

	public static final EGuiAction[] values = values();
}
