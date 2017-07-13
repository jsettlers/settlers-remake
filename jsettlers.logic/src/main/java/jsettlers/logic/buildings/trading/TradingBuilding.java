/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.SetTradingWaypointAction.EWaypointType;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiMaterialRequestSettings;
import jsettlers.logic.buildings.stack.multi.MultiRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiRequestStackSharedData;
import jsettlers.logic.player.Player;

public class TradingBuilding extends Building implements IBuilding.ITrading {
	private static final short WAYPOINT_SEARCH_RADIUS = (short) 20;

	private static final long serialVersionUID = -1760409147232184087L;

	private static final EPriority[] SUPPORTED_PRIORITIES = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };

	private final boolean isSeaTrading;
	private int[] dockPosition = null; // x, y, dx, dy
	private ShortPoint2D shipWayStart = null;

	/**
	 * How many materials were requested by the user. Integer#MAX_VALUE for infinity.
	 */
	private final MultiMaterialRequestSettings requestedMaterials = new MultiMaterialRequestSettings();
	private final ShortPoint2D[] waypoints = new ShortPoint2D[EWaypointType.VALUES.length];

	public TradingBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid, boolean isSeaTrading) {
		super(type, player, position, buildingsGrid);
		this.isSeaTrading = isSeaTrading;
	}

	@Override
	public boolean isOccupied() {
		return false;
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
	public int getRequestedTradingFor(EMaterialType materialType) {
		return requestedMaterials.getRequestedAmount(materialType);
	}

	@Override
	public boolean isSeaTrading() {
		return isSeaTrading;
	}

	public void changeRequestedMaterial(EMaterialType materialType, int amount, boolean relative) {
		long newValue = amount;
		if (relative) {
			int old = requestedMaterials.getRequestedAmount(materialType);
			if (old == Integer.MAX_VALUE) {
				// infinity stays infinity.
				return;
			}
			newValue += old;
		}

		requestedMaterials.setRequestedAmount(materialType, (short) Math.max(0, Math.min(Short.MAX_VALUE, newValue)));
	}

	public void setWaypoint(EWaypointType waypointType, ShortPoint2D position) {
		if (this.isSeaTrading && dockPosition == null) {
			return;
		}
		if (waypointType != EWaypointType.DESTINATION && !isTargetSet()) {
			waypointType = EWaypointType.DESTINATION;
		}
		ShortPoint2D closeReachableLocation = findClosestRechablePosition(waypointType, position);
		if (this.isSeaTrading && waypointType == EWaypointType.DESTINATION) {
			if (!coastIsReachable(position)) {
				return;
			}
		}
		if (isSelected()) {
			drawWaypointLine(false);
		}
		if (waypointType == EWaypointType.DESTINATION) {
			Arrays.fill(waypoints, null);
		}

		ShortPoint2D closeReachableLocation = findClosestReachablePosition(waypointType, position);

		waypoints[waypointType.ordinal()] = closeReachableLocation;

		if (isSelected()) {
			drawWaypointLine(true);
		}
	}

	private boolean coastIsReachable(ShortPoint2D position) {
		boolean waterFound = false;
		boolean landFound = false;
		short x = position.x;
		short y = position.y;
		final byte[] xDeltaArray = EDirection.getXDeltaArray();
		final byte[] yDeltaArray = EDirection.getYDeltaArray();
		int distance = 5;
		for (int direction = 0; direction < EDirection.NUMBER_OF_DIRECTIONS; direction++) {
			int dx = x + xDeltaArray[direction] * distance;
			int dy = y + yDeltaArray[direction] * distance;
			if (grid.getMovableGrid().isInBounds(dx, dy)) {
				if (grid.getMovableGrid().isWaterSafe(dx, dy)) {
					waterFound = true;
				} else {
					landFound = true;
				}
			}
		}
		return waterFound && landFound;
	}

	private ShortPoint2D findClosestRechablePosition(EWaypointType waypointType, ShortPoint2D targetPosition) {
		ShortPoint2D waypointBefore = this.pos;
		if (this.isSeaTrading) {
			waypointBefore = this.shipWayStart;
		}
		for (int index = waypointType.ordinal() - 1; index >= 0; index--) {
			if (waypoints[index] != null) {
				waypointBefore = waypoints[index];
				break;
			}
		}

		return grid.getClosestReachablePosition(waypointBefore, targetPosition,
				false, this.isSeaTrading, null, WAYPOINT_SEARCH_RADIUS);
	}

	boolean isTargetSet() {
		return waypoints[waypoints.length - 1] != null;
	}

	ShortPoint2D[] getWaypoints() {
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
		if (dockPosition != null) {
			removeDock();
		}
		super.kill();
	}

	public void drawWaypointLine(boolean draw) {
		if (this.isSeaTrading) {
			if (this.dockPosition == null) {
				return;
			}
			super.grid.drawTradingPathLine(this.shipWayStart, waypoints, draw);
		} else {
			super.grid.drawTradingPathLine(super.pos, waypoints, draw);
		}
	}

	@Override
	protected List<IRequestStack> createWorkStacks() {
		List<IRequestStack> newStacks = new LinkedList<>();

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

	public boolean setDock(int[] position) {
		if (this.type != EBuildingType.HARBOR) {
			return false;
		}
		if (this.dockPosition != null) { // replace dock
			this.grid.setDock(this.dockPosition, false, this.getPlayer().getPlayerId());
		}
		this.dockPosition = position;
		this.grid.setDock(position, true, this.getPlayer().getPlayerId());
		this.shipWayStart = new ShortPoint2D
				((short) (this.dockPosition[0] + 5 * this.dockPosition[2]),
				(short) (this.dockPosition[1] + 5 * this.dockPosition[3]));
		return true;
	}

	public int[] getDock() {
		return this.dockPosition;
	}

	public void removeDock() {
		if (this.dockPosition == null) {
			return;
		}
		this.grid.setDock(this.dockPosition, false, this.getPlayer().getPlayerId());
		this.dockPosition = null;
		this.shipWayStart = null;
	}

	public ShortPoint2D whereIsMaterialAvailable(EMaterialType material) {
		for (IRequestStack stack : getStacks()) {
			if (stack.getMaterialType() == material) {
				if (stack.hasMaterial()) {
					return stack.getPos();
				}
			}
		}
		return null;
	}

	public ShortPoint2D getShipWayStart() {
		return this.shipWayStart;
	}
}
