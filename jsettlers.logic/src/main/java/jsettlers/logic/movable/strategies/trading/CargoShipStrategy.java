/*******************************************************************************
 * Copyright (c) 2017 - 2018
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

import java8.util.J8Arrays;
import java8.util.stream.Stream;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.buildings.trading.HarborBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Movable;

/**
 *
 * @author Rudolf Polzer
 *
 */
public class CargoShipStrategy extends TradingStrategy {
	private static final int   CARGO_STACKS           = 3;
	private static final short WAYPOINT_SEARCH_RADIUS = 50;

	private final EMaterialType cargoType[]  = new EMaterialType[CARGO_STACKS];
	private final int           cargoCount[] = new int[CARGO_STACKS];

	public CargoShipStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected boolean loadUp(ITradeBuilding tradeBuilding) {
		for (int stackIndex = 0; stackIndex < CARGO_STACKS; stackIndex++) {
			if (getCargoCount(stackIndex) > 0) {
				continue;
			}

			final int finalStackIndex = stackIndex;

			tradeBuilding.tryToTakeMaterial(Constants.STACK_SIZE).ifPresent(materialTypeWithCount -> {
				setCargoType(materialTypeWithCount.materialType, finalStackIndex);
				setCargoCount(materialTypeWithCount.count, finalStackIndex);
			});
		}

		return J8Arrays.stream(cargoCount).sum() > 0;
	}

	protected void dropMaterialIfPossible() {
		int cargoCount;
		EMaterialType material;
		for (int stack = 0; stack < CARGO_STACKS; stack++) {
			cargoCount = getCargoCount(stack);
			material = movable.getCargoType(stack);
			while (cargoCount > 0) {
				super.getGrid().dropMaterial(movable.getPosition(), material, true, true);
				cargoCount--;
			}
			setCargoCount(0, stack);
		}
	}

	protected Stream<? extends ITradeBuilding> getTradersWithWork() {
		return HarborBuilding.getAllHarbors(movable.getPlayer());
	}

	private void setCargoCount(int count, int stack) {
		if (checkStackNumber(stack)) {
			this.cargoCount[stack] = count;
			if (this.cargoCount[stack] < 0) {
				this.cargoCount[stack] = 0;
			} else if (this.cargoCount[stack] > 8) {
				this.cargoCount[stack] = 8;
			}
		}
	}

	private void setCargoType(EMaterialType cargo, int stack) {
		this.cargoType[stack] = cargo;
	}

	private boolean checkStackNumber(int stack) {
		return stack >= 0 && stack < CARGO_STACKS;
	}

	@Override
	public EMaterialType getCargoType(int stack) {
		if (checkStackNumber(stack)) {
			return this.cargoType[stack];
		} else {
			return null;
		}
	}

	public int getCargoCount(int stack) {
		if (checkStackNumber(stack) && this.cargoType[stack] != null) {
			return this.cargoCount[stack];
		} else {
			return 0;
		}
	}

	public int getNumberOfCargoStacks() {
		return CARGO_STACKS;
	}

	@Override
	protected short getWaypointSearchRadius() {
		return WAYPOINT_SEARCH_RADIUS;
	}
}
