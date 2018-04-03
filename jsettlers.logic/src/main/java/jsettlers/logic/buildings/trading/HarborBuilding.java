/*******************************************************************************
 * Copyright (c) 2017
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import jsettlers.common.action.SetTradingWaypointAction;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.IteratorFilter;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.IDockBuilding;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.movable.strategies.trading.IShipHarbor;
import jsettlers.logic.player.Player;

/**
 *
 * @author Rudolf Polzer
 *
 */
public class HarborBuilding extends TradingBuilding implements IShipHarbor, IDockBuilding {
	private static final List<HarborBuilding> ALL_HARBORS = new ArrayList<>();

	public static Iterable<HarborBuilding> getAllHarbors(final Player player) {
		return new IteratorFilter<>(ALL_HARBORS, building -> building.getPlayer() == player);
	}

	public static void clearState() {
		ALL_HARBORS.clear();
	}

	private DockPosition dockPosition = null;

	public HarborBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
		ALL_HARBORS.add(this);
	}

	@Override
	public ShortPoint2D getWaypointsStartPosition() {
		return dockPosition != null ? dockPosition.getWaterPosition() : null;
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		ALL_HARBORS.add(this);
	}

	@Override
	public boolean isSeaTrading() {
		return true;
	}

	@Override
	protected void killedEvent() {
		super.killedEvent();
		ALL_HARBORS.remove(this);
		removeDock();
	}

	@Override
	public boolean needsShip() {
		return isTargetSet() && getPriority() != EPriority.STOPPED && super.getStackWithMaterial() != null;
	}

	@Override
	public int tryToTakeFurtherMaterial(EMaterialType materialType, int requestedNumber) {
		if (!isTargetSet() || getPriority() == EPriority.STOPPED) { // if no target is set, or work is stopped don't give materials
			return 0;
		}
		IRequestStack stack = super.getStackWithMaterial();
		int remainingRequest = requestedNumber;
		while (remainingRequest > 0 && stack != null && materialType == stack.getMaterialType()) {
			if (stack.pop()) {
				remainingRequest--;
			} else {
				break;
			}
		}
		return requestedNumber - remainingRequest;
	}

	@Override
	public EMaterialType tryToTakeShipMaterial() {
		if (!isTargetSet() || getPriority() == EPriority.STOPPED) { // if no target is set, or work is stopped don't give materials
			return null;
		}

		IRequestStack stack = super.getStackWithMaterial();

		if (stack != null) {
			EMaterialType materialType = stack.getMaterialType(); // get this before pop, as pop may reset the currentType of the stack
			if (stack.pop()) {
				return materialType;
			}
		}

		return null;
	}

	@Override
	protected boolean isWaypointFulfillingPreconditions(SetTradingWaypointAction.EWaypointType waypointType, ShortPoint2D position) {
		return waypointType != SetTradingWaypointAction.EWaypointType.DESTINATION || grid.isCoastReachable(position);
	}

	@Override
	public void setDock(ShortPoint2D requestedDockPosition) {
		DockPosition newDockPosition = findValidDockPosition(requestedDockPosition);
		if (newDockPosition == null) {
			return;
		}

		removeDock();
		if (isSelected()) {
			drawWaypointLine(false);
		}

		dockPosition = newDockPosition;
		grid.setDock(dockPosition, this.getPlayer());

		if (isSelected()) {
			drawWaypointLine(true);
		}
	}

	@Override
	public boolean canDockBePlaced(ShortPoint2D requestedDockPosition) {
		return findValidDockPosition(requestedDockPosition) != null;
	}

	private DockPosition findValidDockPosition(ShortPoint2D requestedDockPosition) {
		return grid.findValidDockPosition(requestedDockPosition, pos, IDockBuilding.MAXIMUM_DOCKYARD_DISTANCE);
	}

	public DockPosition getDock() {
		return this.dockPosition;
	}

	private void removeDock() {
		if (this.dockPosition == null) {
			return;
		}
		this.grid.removeDock(this.dockPosition);
		this.dockPosition = null;
	}
}
