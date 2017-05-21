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
package jsettlers.main;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IOpenMultiplayerGameInfo;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.datatypes.JoinableGame;
import jsettlers.network.client.interfaces.INetworkClient;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.common.packets.MatchInfoPacket;

/**
 * This class implements the {@link IMultiplayerConnector} interface and supports the UI with the list of available multiplayer games and allows to
 * start or create them.
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiplayerConnector implements IMultiplayerConnector {

	private final AsyncNetworkClientConnector networkClientFactory;
	private final ChangingList<IJoinableGame> joinableGames = new ChangingList<>();

	public MultiplayerConnector(final String serverAddress, final String userId, final String userName) {
		networkClientFactory = new AsyncNetworkClientConnector(serverAddress, userId, userName, generateMatchesReceiver());
	}

	private IPacketReceiver<ArrayOfMatchInfosPacket> generateMatchesReceiver() {
		return packet -> {
			List<IJoinableGame> openGames = new LinkedList<>();
			for (MatchInfoPacket matchInfo : packet.getMatches()) {
				openGames.add(new JoinableGame(matchInfo));
			}
			joinableGames.setList(openGames);
		};
	}

	@Override
	public ChangingList<IJoinableGame> getJoinableMultiplayerGames() {
		return joinableGames;
	}

	@Override
	public IJoiningGame joinMultiplayerGame(IJoinableGame game) throws IllegalStateException {
		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClientFactory);
		return multiplayerGame.join(game.getId());
	}

	@Override
	public IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo) {
		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClientFactory);
		return multiplayerGame.openNewGame(gameInfo);
	}

	@Override
	public void shutdown() {
		networkClientFactory.close();
	}

	@Override
	public int getRoundTripTimeInMs() {
		INetworkClient networkClient = networkClientFactory.getNetworkClientAsync();
		if (networkClient != null) {
			return networkClient.getRoundTripTimeInMs();
		} else {
			return Integer.MAX_VALUE;
		}
	}
}
