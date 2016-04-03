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
package jsettlers.logic.buildings.trading;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.SetTradingWaypointAction.WaypointType;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiRequestStackSharedData;
import jsettlers.logic.player.Player;

public class TradingBuilding extends Building implements IBuilding.ITrading {
	private static final short WAYPOINT_SEARCH_RADIUS = (short) 20;

	private static final long serialVersionUID = -1760409147232184087L;

	private static final EPriority[] SUPPORTED_PRIORITIES = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };

	private final boolean isSeaTrading;

	/**
	 * How many materials were requested by the user. Integer#MAX_VALUE for infinity.
	 */
	private final short[] requestedMaterials = new short[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	private final ShortPoint2D[] waypoints = new ShortPoint2D[WaypointType.values.length];

	public TradingBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid, boolean isSeaTrading) {
		super(type, player, position, buildingsGrid);
		this.isSeaTrading = isSeaTrading;
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	protected int subTimerEvent() {
		return -1;
	}

	@Override
	protected int constructionFinishedEvent() {
		return -1;
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public int getRequestedTradingFor(EMaterialType material) {
		return requestedMaterials[material.ordinal];
	}

	@Override
	public boolean isSeaTrading() {
		return isSeaTrading;
	}

	public void changeRequestedMaterial(EMaterialType material, int amount, boolean relative) {
		long newValue = amount;
		if (relative) {
			int old = requestedMaterials[material.ordinal];
			if (old == Integer.MAX_VALUE) {
				// infinity stays infinity.
				return;
			}
			newValue += old;
		}

		requestedMaterials[material.ordinal] = (short) Math.max(0, Math.min(Short.MAX_VALUE, newValue));
	}

	public void setWaypoint(WaypointType waypointType, ShortPoint2D position) {
		if (isSelected()) {
			drawWaypointLine(false);
		}

		if (waypointType != WaypointType.DESTINATION && !isTargetSet()) {
			waypointType = WaypointType.DESTINATION;
		}
		if (waypointType == WaypointType.DESTINATION) {
			Arrays.fill(waypoints, null);
		}

		ShortPoint2D closeReachableLocation = findClosestRechablePosition(waypointType, position);

		waypoints[waypointType.ordinal()] = closeReachableLocation;

		if (isSelected()) {
			drawWaypointLine(true);
		}
	}

	private ShortPoint2D findClosestRechablePosition(WaypointType waypointType, ShortPoint2D targetPosition) {
		ShortPoint2D waypointBefore = this.pos;
		for (int index = waypointType.ordinal() - 1; index >= 0; index--) {
			if (waypoints[index] != null) {
				waypointBefore = waypoints[index];
				break;
			}
		}

		ShortPoint2D closestReachableLocation = grid.getClosestReachablePosition(waypointBefore, targetPosition, false, (byte) 0,
				WAYPOINT_SEARCH_RADIUS);
		return closestReachableLocation;
	}

	protected boolean isTargetSet() {
		return waypoints[waypoints.length - 1] != null;
	}

	protected ShortPoint2D[] getWaypoints() {
		return waypoints;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		drawWaypointLine(selected);
	}

	@Override
	public void kill() {
		if (isSelected()) {
			drawWaypointLine(false);
		}
		super.kill();
	}

	private void drawWaypointLine(boolean draw) {
		super.grid.drawTradingPathLine(super.pos, waypoints, draw);
	}

	@Override
	protected List<IRequestStack> createWorkStacks() {
		List<IRequestStack> newStacks = new LinkedList<IRequestStack>();

		MultiRequestStackSharedData sharedData = new MultiRequestStackSharedData(requestedMaterials);

		for (RelativeStack stack : type.getRequestStacks()) {
			newStacks.add(new MultiRequestStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), type, super.getPriority(), sharedData));
		}

		return newStacks;
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		return SUPPORTED_PRIORITIES;
	}
}
