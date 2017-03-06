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
package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.player.Player;

/**
 * This class represents a big temple house that spawns new mages.
 * 
 * @author codingberlin
 * 
 */
public final class BigTemple extends SpawnBuilding {
	private static final long serialVersionUID = -6442369297239688436L;

	private static final byte PRODUCE_LIMIT = 100;
	private static final int PRODUCE_PERIOD_TICK = 30000;
	private static final int PRODUCE_PERIOD = 600000;
	private int nextProduceMillis = 0;

	public BigTemple(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.BIG_TEMPLE, player, position, buildingsGrid);
	}

	@Override
	protected final int subTimerEvent() {
		nextProduceMillis -= PRODUCE_PERIOD_TICK;
		if (nextProduceMillis <= 0) {
			int nextEventMillis = super.subTimerEvent();
			if (nextEventMillis == PRODUCE_PERIOD_TICK) { // priest was not prevented from spawning
				nextProduceMillis = PRODUCE_PERIOD;
			}
			return nextEventMillis;
		}
		return getProducePeriod();
	}

	@Override
	protected int constructionFinishedEvent() {
		getPlayer().getMannaInformation().increaseMannaByBigTemple();
		return super.constructionFinishedEvent();
	}

	@Override
	protected void killedEvent() {
		if (super.isConstructionFinished()) {
			// prevent the player from building temples and immediately destroy them to get back the material for more temples to get fast Level3
			getPlayer().getMannaInformation().stopFutureManaIncreasingByBigTemple();
		}
	}

	@Override
	protected int getProducePeriod() {
		return PRODUCE_PERIOD_TICK;
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.MAGE;
	}

	@Override
	protected byte getProduceLimit() {
		return PRODUCE_LIMIT;
	}
}
