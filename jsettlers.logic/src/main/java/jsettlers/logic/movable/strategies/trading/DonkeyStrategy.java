/*******************************************************************************
 * Copyright (c) 2016 - 2018
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
package jsettlers.logic.movable.strategies.trading;

import java8.util.Optional;
import java8.util.stream.Stream;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.strategies.trading.ITradeBuilding.MaterialTypeWithCount;

/**
 *
 * @author Andreas Eberle
 *
 */
public class DonkeyStrategy extends TradingStrategy {
	private static final short WAYPOINT_SEARCH_RADIUS = 20;

	private EMaterialType materialType1;
	private EMaterialType materialType2;

	public DonkeyStrategy(Movable movable) {
		super(movable);
	}

	protected boolean loadUp(ITradeBuilding tradeBuilding) {
		Optional<EMaterialType> materialType1 = tradeBuilding.tryToTakeMaterial(1).map(MaterialTypeWithCount::getMaterialType);
		Optional<EMaterialType> materialType2 = tradeBuilding.tryToTakeMaterial(1).map(MaterialTypeWithCount::getMaterialType);

		if (!materialType1.isPresent() && !materialType2.isPresent()) {
			reset();
			return false;

		} else {
			super.setMaterial(EMaterialType.BASKET);

			this.materialType1 = materialType1.orElse(null);
			this.materialType2 = materialType2.orElse(null);

			return true;
		}
	}


	protected void dropMaterialIfPossible() {
		if (movable.getMaterial() != EMaterialType.NO_MATERIAL) {
			if (materialType1 != null) {
				super.getGrid().dropMaterial(movable.getPosition(), materialType1, true, true);
				materialType1 = null;
			}
			if (materialType2 != null) {
				super.getGrid().dropMaterial(movable.getPosition(), materialType2, true, true);
				materialType2 = null;
			}
			super.setMaterial(EMaterialType.NO_MATERIAL);
		}
	}

	protected Stream<MarketBuilding> getTradersWithWork() {
		return MarketBuilding.getAllMarkets(movable.getPlayer());
	}

	@Override
	protected boolean isAttackable() {
		return getState() == ETraderState.GOING_TO_TARGET;
	}

	@Override
	protected boolean receiveHit() {
		if (getState() == ETraderState.GOING_TO_TARGET) {
			reset();
			super.abortPath();
		}
		return false;
	}

	@Override
	protected short getWaypointSearchRadius() {
		return WAYPOINT_SEARCH_RADIUS;
	}
}
