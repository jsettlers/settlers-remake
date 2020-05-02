/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.main;

import jsettlers.common.statistics.IGameTimeProvider;
import jsettlers.network.client.interfaces.IGameClock;

/**
 * This class provides game time information to the UI.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameTimeProvider implements IGameTimeProvider {

	private IGameClock gameClock;

	public GameTimeProvider(IGameClock gameTimer) {
		this.gameClock = gameTimer;
	}

	@Override
	public int getGameTime() {
		return gameClock.getTime();
	}

	@Override
	public boolean isGamePausing() {
		return gameClock.isPausing();
	}

	@Override
	public float getGameSpeed() {
		return gameClock.getGameSpeed();
	}
}
