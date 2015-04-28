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
package jsettlers.logic.statistics;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.network.client.interfaces.IGameClock;

/**
 * This class supplies the UI with statistics of the game.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameStatistics implements IStatisticable {

	private IGameClock gameClock;

	public GameStatistics(IGameClock gameTimer) {
		this.gameClock = gameTimer;
	}

	@Override
	public int getGameTime() {
		return gameClock.getTime();
	}

	@Override
	public int getNumberOf(EMaterialType materialType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOf(EMovableType movableType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getJoblessBearers() {
		// TODO Auto-generated method stub
		return 0;
	}

}
