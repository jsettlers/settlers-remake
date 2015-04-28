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
package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.utils.collections.ChangingList;

/**
 * This is the screen that is displayed when a multiplayer game is in the join phase.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IJoinPhaseMultiplayerGameConnector {
	/**
	 * Sets the multiplayer listener that listens to game state changes. If the game has already started,
	 * {@link IMultiplayerListener#gameIsStarting(IStartingGame)} is called immediately by this method.
	 * 
	 * @param listener
	 */
	public void setMultiplayerListener(IMultiplayerListener listener);

	public void setChatListener(IChatMessageListener chatMessageListener);

	public void sendChatMessage(String chatMessage);

	/* TODO: Add a method to access information about the base map */

	/**
	 * The list of players that join this game.
	 * 
	 * @return
	 */
	public ChangingList<IMultiplayerPlayer> getPlayers();

	/**
	 * Sets the ready state of the current user.
	 * 
	 * @param ready
	 */
	void setReady(boolean ready);

	/**
	 * Starts the game. Calls the game listener as soon as it is starting.
	 */
	void startGame();

	/**
	 * Called when the user exits the screen, to abort the multiplayer game. This method does not need to call the abort() method of the
	 * {@link IMultiplayerListener}.
	 */
	public void abort();

}
