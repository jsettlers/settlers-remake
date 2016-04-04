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
package jsettlers.common.menu;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.statistics.IGameTimeProvider;

/**
 * This interface represents a started game offering the methods the UI needs.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IStartedGame {
	/**
	 * Gets the grid that should be displayed to the user.
	 * 
	 * @return
	 */
	IGraphicsGrid getMap();

	/**
	 * Gets an {@link IGameTimeProvider} implementation used to supply the UI with game time information.
	 * 
	 * @return
	 */
	IGameTimeProvider getGameTimeProvider();

	IInGamePlayer getInGamePlayer();

	void setGameExitListener(IGameExitListener exitListener);

	boolean isShutdownFinished();
}