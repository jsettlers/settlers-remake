/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.logic.movable.strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 *
 */
public class DonkeyStrategy extends MovableStrategy {
	private static final long serialVersionUID = 1L;

	private EDonkeyState state = EDonkeyState.JOBLESS;

	private IDonkeyMarket market;
	private Iterator<ShortPoint2D> waypoints;

	private EMaterialType materialType1;
	private EMaterialType materialType2;

	public DonkeyStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			this.market = findNextMarketNeedingDonkey();

			if (this.market == null) { // no market found
				break;
			}

		case INIT_GOING_TO_MARKET:
			if (market.needsDonkey() && super.goToPos(market.getDoor())) {
				state = EDonkeyState.GOING_TO_MARKET;
			} else {
				reset();
			}
			break;

		case GOING_TO_MARKET:
			EMaterialType materialType1 = market.tryToTakeDonkeyMaterial();
			EMaterialType materialType2 = market.tryToTakeDonkeyMaterial();

			if (materialType1 != null || materialType2 != null) {
				super.setMaterial(EMaterialType.BASKET);

				this.materialType1 = materialType1;
				this.materialType2 = materialType2;

				this.waypoints = market.getWaypointsIterator();
				if (super.goToPos(this.waypoints.next())) {
					state = EDonkeyState.GOING_TO_TARGET;
					break;
				}
			}
			reset();
			break;

		case GOING_TO_TARGET:
			if (waypoints.hasNext()) {
				super.goToPos(waypoints.next());
			} else {
				dropMaterialIfPossible();
				waypoints = null;
				state = EDonkeyState.INIT_GOING_TO_MARKET;
			}
			break;

		default:
			break;
		}
	}

	private void reset() {
		dropMaterialIfPossible();
		market = null;
		waypoints = null;
		state = EDonkeyState.JOBLESS;
	}

	private void dropMaterialIfPossible() {
		if (super.getMaterial() != EMaterialType.NO_MATERIAL) {
			if (materialType1 != null) {
				super.getStrategyGrid().dropMaterial(super.getPos(), materialType1, true, true);
				materialType1 = null;
			}
			if (materialType2 != null) {
				super.getStrategyGrid().dropMaterial(super.getPos(), materialType2, true, true);
				materialType2 = null;
			}
			super.setMaterial(EMaterialType.NO_MATERIAL);
		}
	}

	private IDonkeyMarket findNextMarketNeedingDonkey() {
		if (this.market != null && this.market.needsDonkey()) {
			return this.market;
		}

		Iterable<? extends IDonkeyMarket> markets = MarketBuilding.getAllMarkets(super.getPlayer());
		List<IDonkeyMarket> marketsNeedingDonkeys = new ArrayList<IDonkeyMarket>();

		for (IDonkeyMarket currMarket : markets) {
			if (currMarket.needsDonkey()) {
				marketsNeedingDonkeys.add(currMarket);
			}
		}

		if (!marketsNeedingDonkeys.isEmpty()) {
			// randomly distribute the donkeys onto the markets needing them
			return marketsNeedingDonkeys.get(MatchConstants.random().nextInt(marketsNeedingDonkeys.size()));
		} else {
			return null;
		}
	}

	@Override
	protected boolean isAttackable() {
		return state == EDonkeyState.GOING_TO_TARGET;
	}

	@Override
	protected boolean receiveHit() {
		if (state == EDonkeyState.GOING_TO_TARGET) {
			reset();
			super.abortPath();
		}
		return false;
	}

	public static interface IDonkeyMarket extends ILocatable {
		boolean needsDonkey();

		ShortPoint2D getDoor();

		EMaterialType tryToTakeDonkeyMaterial();

		Iterator<ShortPoint2D> getWaypointsIterator();
	}

	private static enum EDonkeyState {
		JOBLESS,
		INIT_GOING_TO_MARKET,
		GOING_TO_MARKET,
		GOING_TO_TARGET,
		DEAD
	}
}
