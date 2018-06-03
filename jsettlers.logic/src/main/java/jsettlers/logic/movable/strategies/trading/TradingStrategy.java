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

package jsettlers.logic.movable.strategies.trading;

import java.util.Iterator;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

public abstract class TradingStrategy extends MovableStrategy {

	private ETraderState state = ETraderState.JOBLESS;

	private ITradeBuilding         tradeBuilding;
	private Iterator<ShortPoint2D> waypoints;

	TradingStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
			case JOBLESS:
				if (tradeBuilding == null || !tradeBuilding.needsTrader()) {
					this.tradeBuilding = findTradeBuildingWithWork();
				}

				if (this.tradeBuilding == null) { // no tradeBuilding found
					break;
				}

			case INIT_GOING_TO_TRADING_BUILDING:
				if (tradeBuilding.needsTrader() && super.goToPos(tradeBuilding.getPickUpPosition())) {
					state = ETraderState.GOING_TO_TRADING_BUILDING;
				} else {
					reset();
				}
				break;

			case GOING_TO_TRADING_BUILDING:
				if (loadUp(tradeBuilding)) {
					this.waypoints = tradeBuilding.getWaypointsIterator();
					state = ETraderState.GOING_TO_TARGET;
				} else {
					state = ETraderState.JOBLESS;
					break;
				}

			case GOING_TO_TARGET:
				if (!goToNextWaypoint()) { // no waypoint left
					dropMaterialIfPossible();
					waypoints = null;
					state = ETraderState.INIT_GOING_TO_TRADING_BUILDING;
				}
				break;

			default:
				break;
		}
	}

	/**
	 *
	 * @return
	 * true if the tradeBuilding had material, the unit is loaded and should proceed to the target destination
	 */
	protected abstract boolean loadUp(ITradeBuilding tradeBuilding);

	private ITradeBuilding findTradeBuildingWithWork() {
		List<? extends ITradeBuilding> tradeBuilding = getTradersWithWork().filter(ITradeBuilding::needsTrader).collect(Collectors.toList());

		if (!tradeBuilding.isEmpty()) { // randomly distribute the donkeys onto the markets needing them
			return tradeBuilding.get(MatchConstants.random().nextInt(tradeBuilding.size()));
		} else {
			return null;
		}
	}

	protected abstract Stream<? extends ITradeBuilding> getTradersWithWork();

	private boolean goToNextWaypoint() {
		while (waypoints.hasNext()) {
			ShortPoint2D nextPosition = waypoints.next();
			if (super.preSearchPath(true, nextPosition.x, nextPosition.y, getWaypointSearchRadius(), ESearchType.VALID_FREE_POSITION)) {
				super.followPresearchedPath();
				return true;
			}
		}

		return false;
	}

	protected abstract short getWaypointSearchRadius();

	protected void reset() {
		dropMaterialIfPossible();
		tradeBuilding = null;
		waypoints = null;
		state = ETraderState.JOBLESS;
	}

	protected abstract void dropMaterialIfPossible();

	protected ETraderState getState() {
		return state;
	}

	protected enum ETraderState {
		JOBLESS,
		INIT_GOING_TO_TRADING_BUILDING,
		GOING_TO_TRADING_BUILDING,
		GOING_TO_TARGET,
		DEAD
	}
}
