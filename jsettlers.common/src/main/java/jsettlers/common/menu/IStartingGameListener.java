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
package jsettlers.common.menu;

/**
 * @author michael
 * @author Andreas Eberle
 */
public interface IStartingGameListener {
	/**
	 * Notifies this listener of the current progress of the start. May only be called before {@link #preLoadFinished(IStartedGame)} is called.
	 *
	 * @param state
	 * @param progress
	 */
	void startProgressChanged(EProgressState state, float progress);

	/**
	 * Notifies the listener that a game was started and gives it access to the game data.
	 *
	 * @param game
	 * 		The game that was just started.
	 * @retrun A {@link IMapInterfaceConnector} that can be used to access the game afterwards.
	 */
	IMapInterfaceConnector preLoadFinished(IStartedGame game);

	void startFailed(EGameError errorType, Exception exception);

	void startFinished();

	void startingLoadingGame();

	void waitForPreloading();
}
