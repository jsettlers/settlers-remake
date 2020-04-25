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
package jsettlers.logic.buildings.trading;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java8.util.Optional;
import java8.util.stream.Collectors;
import jsettlers.common.action.SetTradingWaypointAction.EWaypointType;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiMaterialRequestSettings;
import jsettlers.logic.buildings.stack.multi.MultiRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiRequestStackSharedData;
import jsettlers.logic.movable.strategies.trading.ITradeBuilding;
import jsettlers.logic.player.Player;

import static java8.util.stream.StreamSupport.stream;

public abstract class TradingBuilding extends Building implements IBuilding.ITrading, ITradeBuilding {
	private static final short WAYPOINT_SEARCH_RADIUS = (short) 20;

	private static final EPriority[] SUPPORTED_PRIORITIES = new EPriority[]{
		EPriority.LOW,
		EPriority.HIGH,
		EPriority.STOPPED};

	/**
	 * How many materials were requested by the user. Integer#MAX_VALUE for infinity.
	 */
	private final MultiMaterialRequestSettings requestedMaterials = new MultiMaterialRequestSettings();
	private final ShortPoint2D[]               waypoints          = new ShortPoint2D[EWaypointType.VALUES.length];

	TradingBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
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
		if (waypointType != EWaypointType.DESTINATION && !isTargetSet()) {
			waypointType = EWaypointType.DESTINATION;
		}

		if (isSelected()) {
			drawWaypointLine(false);
		}
		if (waypointType == EWaypointType.DESTINATION) {
			Arrays.fill(waypoints, null);
		}

		ShortPoint2D closestReachableLocation = findClosestReachablePosition(waypointType, position);
		if (closestReachableLocation == null || !isWaypointFulfillingPreconditions(waypointType, closestReachableLocation)) {
			return;
		}

		waypoints[waypointType.ordinal()] = closestReachableLocation;

		if (isSelected()) {
			drawWaypointLine(true);
		}
	}

	protected boolean isWaypointFulfillingPreconditions(EWaypointType waypointType, ShortPoint2D position) {
		return true;
	}

	private ShortPoint2D findClosestReachablePosition(EWaypointType waypointType, ShortPoint2D targetPosition) {
		ShortPoint2D waypointBefore = getWaypointsStartPosition();

		for (int index = waypointType.ordinal() - 1; index >= 0; index--) {
			if (waypoints[index] != null) {
				waypointBefore = waypoints[index];
				break;
			}
		}

		return grid.getClosestReachablePosition(waypointBefore, targetPosition, false, this.isSeaTrading(), null, WAYPOINT_SEARCH_RADIUS);
	}

	boolean isTargetSet() {
		return waypoints[waypoints.length - 1] != null;
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

	protected void drawWaypointLine(boolean draw) {
		ShortPoint2D waypointStart = getWaypointsStartPosition();
		if (waypointStart != null) {
			super.grid.drawTradingPathLine(waypointStart, waypoints, draw);
		}
	}

	protected abstract ShortPoint2D getWaypointsStartPosition();

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


	@Override
	public boolean needsTrader() {
		return isTargetSet() && getPriority() != EPriority.STOPPED && super.getStackWithMaterial() != null;
	}

	@Override
	public Optional<MaterialTypeWithCount> tryToTakeMaterial(int maxAmount) {
		if (!isTargetSet() || getPriority() == EPriority.STOPPED) { // if no target is set, or work is stopped don't give materials
			return Optional.empty();
		}

		List<? extends IRequestStack> potentialStacks = stream(getStacks()).filter(IRequestStack::hasMaterial).collect(Collectors.toList());

		if (potentialStacks.isEmpty()) {
			return Optional.empty();
		}

		EMaterialType resultMaterialType = potentialStacks.get(0).getMaterialType();
		int resultCount = 0;

		for (IRequestStack stack : potentialStacks) {
			if (stack.getMaterialType() != resultMaterialType) {
				continue;
			}

			while (resultCount < maxAmount && stack.pop()) {
				resultCount++;
			}

			if (resultCount >= maxAmount) {
				break;
			}
		}

		return Optional.of(new MaterialTypeWithCount(resultMaterialType, resultCount));
	}


	public Iterator<ShortPoint2D> getWaypointsIterator() {
		return new WaypointsIterator(waypoints);
	}

	private static class WaypointsIterator implements Iterator<ShortPoint2D>, Serializable {
		private static final long serialVersionUID = 5229610228646171358L;

		private final ShortPoint2D[] waypoints;
		private       int            i = 0;

		WaypointsIterator(ShortPoint2D[] waypoints) {
			this.waypoints = waypoints;
			hasNext();
		}

		@Override
		public boolean hasNext() {
			for (; i < waypoints.length && waypoints[i] == null; i++) { ; }
			return i < waypoints.length;
		}

		@Override
		public ShortPoint2D next() {
			return hasNext() ? waypoints[i++] : null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
