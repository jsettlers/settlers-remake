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
package jsettlers.network.server;

import jsettlers.network.common.packets.ChatMessagePacket;
import jsettlers.network.common.packets.OpenNewMatchPacket;
import jsettlers.network.common.packets.TimeSyncPacket;
import jsettlers.network.server.match.Player;

public interface IServerManager {

	boolean acceptNewPlayer(Player player);

	void leaveMatch(Player player);

	/**
	 * Creates a new match with the given name and the given map for the given {@link Player} and joins the player to the match.
	 * 
	 * @param matchInfo
	 *            An {@link OpenNewMatchPacket} containing the data to be used for creating the new match.
	 * @param player
	 *            The player that want's to create the match. This player will directly be joined into the match.
	 */
	void createNewMatch(OpenNewMatchPacket matchInfo, Player player);

	void channelClosed(Player player);

	/**
	 * Starts the match of the given player.
	 * 
	 * @param player
	 */
	void startMatch(Player player);

	void forwardChatMessage(Player player, ChatMessagePacket packet);

	/**
	 * Sends the given {@link TimeSyncPacket} to the other players in the {@link Player}s match.
	 * 
	 * @param player
	 *            The player that sent the {@link TimeSyncPacket}.
	 * @param packet
	 */
	void distributeTimeSync(Player player, TimeSyncPacket packet);

	void joinMatch(String matchId, Player player);

	void setReadyStateForPlayer(Player player, boolean ready);

	void sendMatchesToPlayer(Player player);

	void setStartFinished(Player player, boolean startFinished);
}
