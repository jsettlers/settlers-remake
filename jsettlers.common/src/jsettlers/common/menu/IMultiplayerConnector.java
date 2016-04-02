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
package jsettlers.common.menu;

import jsettlers.common.utils.collections.ChangingList;

/**
 * This interface offers the methods that interact with a multiplayer server.
 * 
 * @author Andreas Eberle
 */
public interface IMultiplayerConnector {

	/**
	 * Gets a list of multiplayer games that can be joined.
	 * 
	 * @param onServer
	 *            The name of the server we should search on.
	 * @return
	 */
	ChangingList<IJoinableGame> getJoinableMultiplayerGames();

	/**
	 * Joins the given multiplayer game.
	 * 
	 * @param game
	 * @return
	 */
	IJoiningGame joinMultiplayerGame(IJoinableGame game)
			throws IllegalStateException;

	/**
	 * Creates a new multiplayer game on the server and joins this new game.
	 * 
	 * @param gameInfo
	 *            {@link IOpenMultiplayerGameInfo} object defining the parameters of the game.
	 * @return
	 */
	IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo);

	/**
	 * @return Returns the round trip time in milliseconds<br>
	 *         or {@link Integer#MAX_VALUE} if the client is not yet connected.
	 */
	int getRoundTripTimeInMs();

	/**
	 * Shuts down the connection to the server and stops the threads this multiplayer connector started.
	 */
	void shutdown();
}
