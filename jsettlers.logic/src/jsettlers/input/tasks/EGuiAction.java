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
	SET_MATERIAL_PRIORITIES,
	UPGRADE_SOLDIERS,
	CHANGE_TRADING,
	SET_TRADING_WAYPOINT,
	SET_STOCK_ACEPTS_MATERIAL;

	public static final EGuiAction[] values = values();
}
