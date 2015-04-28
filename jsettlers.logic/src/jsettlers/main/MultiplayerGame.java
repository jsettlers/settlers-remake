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

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.ENetworkMessage;
import jsettlers.graphics.startscreen.interfaces.IChatMessageListener;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.datatypes.MultiplayerPlayer;
import jsettlers.network.NetworkConstants;
import jsettlers.network.client.interfaces.INetworkClient;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.ChatMessagePacket;
import jsettlers.network.common.packets.MapInfoPacket;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.common.packets.MatchStartPacket;
import jsettlers.network.common.packets.PlayerInfoPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.match.EPlayerState;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiplayerGame {

	private final AsyncNetworkClientConnector networkClientFactory;
	private final ChangingList<IMultiplayerPlayer> playersList = new ChangingList<IMultiplayerPlayer>();
	private INetworkClient networkClient;

	private IJoiningGameListener joiningGameListener;
	private IMultiplayerListener multiplayerListener;
	private IChatMessageListener chatMessageListener;

	public MultiplayerGame(AsyncNetworkClientConnector networkClientFactory) {
		this.networkClientFactory = networkClientFactory;
	}

	public IJoiningGame join(final String matchId) {
		new Thread("joinGameThread") {
			@Override
			public void run() {
				networkClient = networkClientFactory.getNetworkClient();
				networkClient.joinMatch(matchId, generateMatchStartedListener(), generateMatchInfoUpdatedListener(), generateChatMessageReceiver());
			}
		}.start();
		return generateJoiningGame();
	}

	public IJoiningGame openNewGame(final IOpenMultiplayerGameInfo gameInfo) {
		new Thread("openNewGameThread") {
			@Override
			public void run() {
				networkClient = networkClientFactory.getNetworkClient();

				IMapDefinition mapDefintion = gameInfo.getMapDefinition();
				MapInfoPacket mapInfo = new MapInfoPacket(mapDefintion.getId(), mapDefintion.getName(), "", "", mapDefintion.getMaxPlayers());

				networkClient.openNewMatch(gameInfo.getMatchName(), gameInfo.getMaxPlayers(), mapInfo, 4711L, generateMatchStartedListener(),
						generateMatchInfoUpdatedListener(), generateChatMessageReceiver());
			}
		}.start();
		return generateJoiningGame();
	}

	private IJoiningGame generateJoiningGame() {
		return new IJoiningGame() {
			@Override
			public void setListener(IJoiningGameListener joiningGameListener) {
				MultiplayerGame.this.joiningGameListener = joiningGameListener;
				if (joiningGameListener != null && networkClient != null && networkClient.getState() == EPlayerState.IN_MATCH) {
					joiningGameListener.gameJoined(generateJoinPhaseGameConnector());
				}
			}

			@Override
			public void abort() {
				networkClient.leaveMatch();
			}
		};
	}

	private IPacketReceiver<ChatMessagePacket> generateChatMessageReceiver() {
		return new IPacketReceiver<ChatMessagePacket>() {
			@Override
			public void receivePacket(ChatMessagePacket packet) {
				if (chatMessageListener != null) {
					chatMessageListener.chatMessageReceived(packet.getAuthorId(), packet.getMessage());
				}
			}
		};
	}

	private IPacketReceiver<MatchStartPacket> generateMatchStartedListener() {
		return new IPacketReceiver<MatchStartPacket>() {
			@Override
			public void receivePacket(MatchStartPacket packet) {
				updatePlayersList(packet.getMatchInfo().getPlayers());

				MapLoader mapLoader = MapList.getDefaultList().getMapById(packet.getMatchInfo().getMapInfo().getId());
				long randomSeed = packet.getRandomSeed();
				boolean[] availablePlayers = new boolean[mapLoader.getMaxPlayers()];
				byte ownPlayerId = calculatePlayerInfos(availablePlayers);

				JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, networkClient.getNetworkConnector(), ownPlayerId, availablePlayers);

				multiplayerListener.gameIsStarting(game.start());
			}

		};
	}

	byte calculatePlayerInfos(boolean[] availablePlayers) {
		String myId = networkClient.getPlayerInfo().getId();
		byte i = 0;
		byte ownPlayerId = -1;
		for (IMultiplayerPlayer currPlayer : playersList.getItems()) {
			availablePlayers[i] = true;
			if (currPlayer.getId().equals(myId)) {
				ownPlayerId = i;
			}
			i++;
		}

		if (ownPlayerId < 0) {
			throw new RuntimeException("Wasn't able to find my id!");
		} else {
			return ownPlayerId;
		}
	}

	private IPacketReceiver<MatchInfoUpdatePacket> generateMatchInfoUpdatedListener() {
		return new IPacketReceiver<MatchInfoUpdatePacket>() {
			@Override
			public void receivePacket(MatchInfoUpdatePacket packet) {
				if (joiningGameListener != null) {
					joiningGameListener.gameJoined(generateJoinPhaseGameConnector());
					joiningGameListener = null;
				}

				updatePlayersList(packet.getMatchInfo().getPlayers());
				receiveSystemMessage(new MultiplayerPlayer(packet.getUpdatedPlayer()), getNetworkMessageById(packet.getUpdateReason()));
			}

		};
	}

	void updatePlayersList(PlayerInfoPacket[] playerInfoPackets) {
		List<IMultiplayerPlayer> players = new LinkedList<IMultiplayerPlayer>();
		for (PlayerInfoPacket playerInfoPacket : playerInfoPackets) {
			players.add(new MultiplayerPlayer(playerInfoPacket));
		}
		playersList.setList(players);
	}

	private ENetworkMessage getNetworkMessageById(NetworkConstants.ENetworkMessage errorMessageId) {
		switch (errorMessageId) {
		case INVALID_STATE_ERROR:
			return ENetworkMessage.INVALID_STATE_ERROR;
		case NO_LISTENER_FOUND:
			return ENetworkMessage.UNKNOWN_ERROR;
		case NOT_ALL_PLAYERS_READY:
			return ENetworkMessage.NOT_ALL_PLAYERS_READY;
		case PLAYER_JOINED:
			return ENetworkMessage.PLAYER_JOINED;
		case PLAYER_LEFT:
			return ENetworkMessage.PLAYER_LEFT;
		case UNAUTHORIZED:
			return ENetworkMessage.UNAUTHORIZED;
		case READY_STATE_CHANGED:
			return ENetworkMessage.READY_STATE_CHANGED;
		case UNKNOWN_ERROR:
		default:
			return ENetworkMessage.UNKNOWN_ERROR;
		}
	}

	void receiveSystemMessage(IMultiplayerPlayer author, ENetworkMessage networkMessage) {
		if (chatMessageListener != null) {
			chatMessageListener.systemMessageReceived(author, networkMessage);
		}
	}

	private IJoinPhaseMultiplayerGameConnector generateJoinPhaseGameConnector() {
		networkClient.registerRejectReceiver(new IPacketReceiver<RejectPacket>() {
			@Override
			public void receivePacket(RejectPacket packet) {
				receiveSystemMessage(null, getNetworkMessageById(packet.getErrorMessageId()));
				System.out.println("Received reject packet: rejectedKey: " + packet.getRejectedKey() + " messageid: " + packet.getErrorMessageId());
			}
		});

		return new IJoinPhaseMultiplayerGameConnector() {
			@Override
			public void startGame() {
				networkClient.startMatch();
			}

			@Override
			public void setReady(boolean ready) {
				networkClient.setReadyState(ready);
			}

			@Override
			public void setMultiplayerListener(IMultiplayerListener multiplayerListener) {
				MultiplayerGame.this.multiplayerListener = multiplayerListener;
			}

			@Override
			public ChangingList<IMultiplayerPlayer> getPlayers() {
				return playersList;
			}

			@Override
			public void abort() {
				networkClient.leaveMatch();
			}

			@Override
			public void setChatListener(IChatMessageListener chatMessageListener) {
				MultiplayerGame.this.chatMessageListener = chatMessageListener;
			}

			@Override
			public void sendChatMessage(String chatMessage) {
				networkClient.sendChatMessage(chatMessage);
			}
		};
	}

}
