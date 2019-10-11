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

package jsettlers.main.android.gameplay.gamemenu;

import android.arch.lifecycle.MutableLiveData;

import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.network.client.interfaces.IGameClock;

public class GameSpeedLiveData extends MutableLiveData<Float> {

	private final IGameClock gameClock;
	private final DrawControls drawControls;

	public GameSpeedLiveData(IGameClock gameClock, DrawControls drawControls) {
		this.gameClock = gameClock;
		this.drawControls = drawControls;
	}

	@Override
	protected void onActive() {
		super.onActive();
		drawControls.addInfrequentDrawListener(drawListener);
		postValue(gameClock.getGameSpeed());
	}

	@Override
	protected void onInactive() {
		super.onInactive();
		drawControls.removeInfrequentDrawListener(drawListener);
	}

	private DrawListener drawListener = new DrawListener() {
		@Override
		public void draw() {
			postValue(gameClock.getGameSpeed());
		}
	};
}
