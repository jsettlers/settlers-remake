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
package jsettlers.logic.buildings.others;

import java.util.Arrays;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.SetTradingWaypointAction.WaypointType;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.MaterialRequestCount;

public class TestTradingBuilding extends Building implements IBuilding.ITrading {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1760409147232184087L;
	private boolean isSeaTrading;

	private ShortPoint2D[] waypoints = new ShortPoint2D[WaypointType.values.length];
	MaterialRequestCount requestedMaterials = new MaterialRequestCount();

	public TestTradingBuilding(EBuildingType type, Player player, boolean isSeaTrading) {
		super(type, player);
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
		return requestedMaterials.getRequestedFor(material);
	}

	@Override
	public boolean isSeaTrading() {
		return isSeaTrading;
	}

	public void changeRequestedMaterial(EMaterialType material, int amount, boolean relative) {
		requestedMaterials.changeRequestedMaterial(material, amount, relative);
	}

	public void setWaypoint(WaypointType waypointType, ShortPoint2D position) {
		if (isSelected()) {
			drawWaypointLine(false);
		}

		if (waypointType != WaypointType.DESTINATION && waypoints[waypoints.length - 1] == null) {
			waypointType = WaypointType.DESTINATION;
		}
		if (waypointType == WaypointType.DESTINATION) {
			Arrays.fill(waypoints, null);
		}
		waypoints[waypointType.ordinal()] = position;

		if (isSelected()) {
			drawWaypointLine(true);
		}
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
		super.getGrid().drawTradingPathLine(super.getPos(), waypoints, draw);
	}
}
