/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.action;

/**
 * This enum defines the type of the action.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public enum EActionType {
	/**
	 * Builds a building, the building has to be supplied by the action.
	 * 
	 * @see BuildAction
	 */
	BUILD,

	/**
	 * Show or hide the construction marks.
	 * 
	 * @see ShowConstructionMarksAction
	 */
	SHOW_CONSTRUCTION_MARK,

	/**
	 * Destroys the selected items.
	 */
	DESTROY,

	/**
	 * Select a point on the map.
	 * 
	 * @see PointAction
	 */
	SELECT_POINT,

	/**
	 * Select all units of the type on that point that are around that point.
	 * 
	 * @see PointAction
	 */
	SELECT_POINT_TYPE,

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
	 * 
	 * @see SelectAction
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
	 * Deselect everything.
	 */
	DESELECT,

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
	 * 
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
	 * used to convert any movable to another.<br>
	 * 
	 * @see ConvertAction
	 */
	CONVERT,

	/**
	 * Start menu constants, never used in game
	 */
	SHOW_START_NEW,
	SHOW_LOAD,
	SHOW_START_NETWORK,
	SHOW_JOIN_NETWORK,
	SHOW_RECOVER_NETWORK,
	START_NEW_GAME,
	LOAD_GAME,

	/**
	 * GUI internal. Implements ExecutableAction
	 */
	EXECUTABLE,

	/**
	 * Exit the game. Does not ask any more.
	 */
	EXIT,

	/**
	 * Sets the speed to be not paused.
	 */
	SPEED_UNSET_PAUSE,

	/**
	 * Sets the speed to be paused.
	 */
	SPEED_SET_PAUSE,

	/**
	 * Unspecified action.
	 */
	UNSPECIFIED,

	/**
	 * Asks the user to set a work area.
	 */
	ASK_SET_WORK_AREA,

	/**
	 * Aborts the current user action, attempts to reset the GUI state to the default state.
	 */
	ABORT,
	/**
	 * Asks the user if he wants to destroy the building.
	 */
	ASK_DESTROY,

	/**
	 * Gets called when a network game should be started.
	 */
	START_NETWORK,
	JOIN_NETWORK,
	TOGGLE_ALLOW_START,

	/**
	 * Toggles if original graphics should be used by the gui.
	 */
	TOGGLE_ORIGINAL_GRAPHICS,

	/**
	 * The user wants to go back.
	 */
	BACK,

	/**
	 * The user wants to set the priority of the building.
	 * 
	 * @see SetBuildingPriorityAction
	 */
	SET_BUILDING_PRIORITY,

	/**
	 * The user wants to change the settings for the distribution of a material to the receiving buildings.
	 * 
	 * @see SetMaterialDistributionSettingsAction
	 */
	SET_MATERIAL_DISTRIBUTION_SETTINGS,

	/**
	 * The user wants to change the order in which materials are served by bearers.
	 * 
	 * @see SetMaterialPrioritiesAction
	 */
	SET_MATERIAL_PRIORITIES,

	/**
	 * Focus a UI Input
	 */
	FOCUS,

	NEXT_OF_TYPE,
	UPGRADE_SOLDIERS,
	SET_MATERIAL_PRODUCTION,

	/**
	 * Sets if the material should be placed in stock or not.
	 * 
	 * @see SetMaterialShouldUseStockAction
	 */
	SET_MATERIAL_STOCK_ACCEPTED,

	/**
	 * Add as many soldiers to this building as possible.
	 */
	SOLDIERS_ALL,
	/**
	 * Only put one soldier in this building.
	 */
	SOLDIERS_ONE,
	/**
	 * Add one more soldier of this type to the building
	 * 
	 * @see SoldierAction
	 */
	SOLDIERS_MORE,
	/**
	 * Add one more soldier of this type to the building
	 * 
	 * @see SoldierAction
	 */
	SOLDIERS_LESS,

	/**
	 * Ask the user to select a trading waypoint.
	 * 
	 * @see AskSetTradingWaypointAction
	 */
	ASK_SET_TRADING_WAYPOINT,

	/**
	 * Set the trading waypoint for the current selection.
	 * 
	 * @see SetTradingWaypointAction
	 */
	SET_TRADING_WAYPOINT,

	/**
	 * Ask the user to set the dock position.
	 */
	ASK_SET_DOCK,

	/**
	 * Set the dock position.
	 * 
	 * @see PointAction
	 */
	SET_DOCK,

	/**
	 * Changes the amount of materials that should be traded from the current trading building.
	 * 
	 * @see ChangeTradingRequestAction
	 */
	CHANGE_TRADING_REQUEST

}
