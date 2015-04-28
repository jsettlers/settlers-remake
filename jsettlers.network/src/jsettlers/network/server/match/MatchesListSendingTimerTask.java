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
package jsettlers.network.server.match;

import java.util.List;
import java.util.TimerTask;

import jsettlers.network.NetworkConstants;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.server.db.IDBFacade;

/**
 * This {@link TimerTask} implementation gets the logged in players and sends them the open matches on every call to {@link #run()}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchesListSendingTimerTask extends TimerTask {
	private final IDBFacade db;

	public MatchesListSendingTimerTask(IDBFacade db) {
		this.db = db;
	}

	@Override
	public void run() {
		List<Player> loggedInPlayers = db.getPlayers(EPlayerState.LOGGED_IN);
		ArrayOfMatchInfosPacket packet = getArrayOfMatchInfosPacket();

		for (Player currPlayer : loggedInPlayers) {
			sendMatchesPacketToPlayer(currPlayer, packet);
		}
	}

	private void sendMatchesPacketToPlayer(Player player, ArrayOfMatchInfosPacket arrayOfMatchesPacket) {
		player.sendPacket(NetworkConstants.ENetworkKey.ARRAY_OF_MATCHES, arrayOfMatchesPacket);
	}

	private ArrayOfMatchInfosPacket getArrayOfMatchInfosPacket() {
		List<Match> matches = db.getJoinableMatches();

		MatchInfoPacket[] matchInfoPackets = new MatchInfoPacket[matches.size()];
		int i = 0;
		for (Match curr : matches) {
			matchInfoPackets[i] = new MatchInfoPacket(curr);
			i++;
		}

		return new ArrayOfMatchInfosPacket(matchInfoPackets);
	}

	public void sendMatchesTo(Player player) {
		ArrayOfMatchInfosPacket arrayOfMatchesPacket = getArrayOfMatchInfosPacket();
		sendMatchesPacketToPlayer(player, arrayOfMatchesPacket);
	}
}
