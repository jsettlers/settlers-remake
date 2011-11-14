package jsettlers.graphics.action;


/**
 * This defines the type of the action.
 * 
 * @author michael
 */
public enum EActionType {
	/**
	 * Builds a building, the building has to be supplied by the action.
	 * 
	 * @see BuildAction
	 */
	BUILD,

	/**
	 * Destroys the selected items.
	 */
	DESTROY,

	/**
	 * Select a point on the map.
	 * 
	 * @see SelectAction
	 */
	SELECT_POINT,

	/**
	 * Skip the next minute of gameplay.
	 */
	FAST_FORWARD,

	// - - - - - - SPEED - - - - -
	SPEED_TOGGLE_PAUSE,
	SPEED_FASTER,
	SPEED_SLOWER,
	SPEED_SLOW,
	SPEED_NORMAL,
	SPEED_FAST,

	// - - - - - - SELECTION - - - - - - -
	/**
	 * Lets the currently selected settlers start working
	 */
	START_WORKING,

	/**
	 * Lets the currently selected settlers stop working
	 */
	STOP_WORKING,

	/**
	 * Request to set the working area of the building.
	 */
	SET_WORK_AREA,

	/**
	 * Lets the settler move to a given point.
	 * 
	 * @see MoveToAction
	 */
	MOVE_TO,

	/**
	 * Sets the screen to display the selection.
	 */
	SHOW_SELECTION,

	/**
	 * Selects an area of the screen.
	 * 
	 * @see SelectAreaAction
	 */
	SELECT_AREA,

	/**
	 * The screen changed.
	 * 
	 * @see ScreenChangeAction
	 */
	SCREEN_CHANGE,

	/**
	 * used for debugging (should be fired on pressing d)
	 */
	DEBUG_ACTION, 
	
	/**
	 * Changes the side panel content.
	 * @see ChangePanelAction
	 */
	CHANGE_PANEL, 
	
	/**
	 * Toggles debug tile display
	 */
	TOGGLE_DEBUG, 
	
	/**
	 * Pan to the given point.
	 * <p>
	 * The action must be an instance of {@link PanToAction}
	 */
	PAN_TO, 
	
	/**
	 * Toggles the build menu.
	 */
	TOGGLE_BUILD_MENU, 
	
	/**
	 * Toggles fog of war.
	 */
	TOGGLE_FOG_OF_WAR, 
	
	/**
	 * Zoom in
	 */
	ZOOM_IN,
	
	/**
	 * Zoom out
	 */
	ZOOM_OUT, 
	
	/**
	 * Save the game
	 */
	SAVE, 
	/**
	 * Converts bearer to pioneer of the selection.
	 */
	CONVERT_ONE_PIONEER,
	/**
	 * Converts all selected bearers to pioneers.
	 */
	CONVERT_ALL_PIONEER,
	/**
	 * Converts bearer to thieves of the selection.
	 */
	CONVERT_ONE_THIEF,
	/**
	 * Converts all selected thieves to pioneers.
	 */
	CONVERT_ALL_THIEF,
	/**
	 * Converts bearer to geologist of the selection.
	 */
	CONVERT_ONE_GEOLOGIST,
	/**
	 * Converts all selected geologist to pioneers.
	 */
	CONVERT_ALL_GEOLOGIST, 
	/**
	 * Converts selected Pioneers back to bearers.
	 */
	CONVERT_TO_BEARERS,
	
}
