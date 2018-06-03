/*******************************************************************************
 * Copyright (c) 2015 - 2018
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

import jsettlers.common.action.Action;
import jsettlers.common.action.EActionType;
import jsettlers.common.action.SetTradingWaypointAction.EWaypointType;

/**
 * This is an action combined with a {@link EWaypointType} that asks the user to set the trading waypoint.
 * 
 * @author Michael Zangl
 */
public class AskSetTradingWaypointAction extends Action {

	private final EWaypointType waypoint;

	/**
	 * Create a new {@link AskSetTradingWaypointAction}.
	 * 
	 * @param waypoint
	 *            The waypoint
	 */
	public AskSetTradingWaypointAction(EWaypointType waypoint) {
		super(EActionType.ASK_SET_TRADING_WAYPOINT);
		this.waypoint = waypoint;
	}

	/**
	 * Gets the waypoint.
	 * 
	 * @return The waypoint
	 */
	public EWaypointType getWaypoint() {
		return waypoint;
	}

}
