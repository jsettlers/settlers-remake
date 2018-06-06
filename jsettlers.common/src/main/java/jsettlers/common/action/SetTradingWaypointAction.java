/*
 * Copyright (c) 2018
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
 */
package jsettlers.common.action;

import jsettlers.common.position.ShortPoint2D;

/**
 * Sets a traiding waypoint position for the selected building.
 * 
 * @author Michael Zangl
 *
 */
public class SetTradingWaypointAction extends PointAction {

	/**
	 * The waypoint types, ordered in the order they are visited by the donkeys/ships.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public enum EWaypointType {
		WAYPOINT_1,
		WAYPOINT_2,
		WAYPOINT_3,
		DESTINATION;

		public static final EWaypointType[] VALUES = values();
	}

	private final EWaypointType waypoint;

	/**
	 * Creates a new {@link SetTradingWaypointAction}.
	 * 
	 * @param waypoint
	 *            The waypoint to set.
	 * @param position
	 *            The position to set it at.
	 */
	public SetTradingWaypointAction(EWaypointType waypoint, ShortPoint2D position) {
		super(EActionType.SET_TRADING_WAYPOINT, position);
		this.waypoint = waypoint;
	}

	/**
	 * Gets the waypoint to set.
	 * 
	 * @return The waypoint.
	 */
	public EWaypointType getWaypointType() {
		return waypoint;
	}
}
