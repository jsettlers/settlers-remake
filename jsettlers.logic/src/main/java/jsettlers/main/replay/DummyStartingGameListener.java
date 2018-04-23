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
package jsettlers.main.replay;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;

class DummyStartingGameListener implements IStartingGameListener {
	private final Object waitMutex = new Object();
	private IStartedGame startedGame = null;

	@Override
	public void startProgressChanged(EProgressState state, float progress) {
	}

	@Override
	public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
		startedGame = game;
		return new DummyMapInterfaceConnector();
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {
		System.err.println("ERROR: Start failed due to: " + errorType);
		exception.printStackTrace();
		System.exit(1);
	}

	public IStartedGame waitForGameStartup() {
		synchronized (waitMutex) {
			while (startedGame == null) {
				try {
					waitMutex.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return startedGame;
	}

	@Override
	public void startFinished() {
		synchronized (waitMutex) {
			waitMutex.notifyAll();
		}
	}

	@Override
	public void startingLoadingGame() {
	}

	@Override
	public void waitForPreloading() {
	}
}
