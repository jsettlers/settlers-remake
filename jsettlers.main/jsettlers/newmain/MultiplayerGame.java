package jsettlers.newmain;

import java.util.LinkedList;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.EMultiplayerConnectorState;
import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IChatMessageListener;
import jsettlers.graphics.startscreen.interfaces.IJoinPhaseMultiplayerGameConnector;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGameListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerPlayer;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.newmain.datatypes.ChangingList;
import jsettlers.newmain.datatypes.MultiplayerPlayer;
import jsettlers.newmain.datatypes.ObjectContainer;
import networklib.client.interfaces.INetworkClient;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.PlayerInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiplayerGame {

	private final INetworkClient networkClient;
	private final ChangingList<IMultiplayerPlayer> playersList = new ChangingList<IMultiplayerPlayer>();
	private final ObjectContainer<EMultiplayerConnectorState> state;

	private IJoiningGameListener joiningGameListener;
	private IMultiplayerListener multiplayerListener;
	private IChatMessageListener chatMessageListener;

	public MultiplayerGame(INetworkClient networkClient, ObjectContainer<EMultiplayerConnectorState> state) {
		this.networkClient = networkClient;
		this.state = state;
	}

	public IJoiningGame join(String matchId) {
		state.setValue(EMultiplayerConnectorState.JOINING_GAME);
		networkClient.joinMatch(matchId, generateMatchStartedListener(), generateMatchInfoUpdatedListener(), generateChatMessageReceiver());
		return generateJoiningGame();
	}

	public IJoiningGame openNewGame(IOpenMultiplayerGameInfo gameInfo) {
		state.setValue(EMultiplayerConnectorState.OPENING_NEW_GAME);
		IStartableMapDefinition mapDefintion = gameInfo.getMapDefinition();
		MapInfoPacket mapInfo = new MapInfoPacket(mapDefintion.getId(), mapDefintion.getName(), null, null, mapDefintion.getMaxPlayers());

		networkClient.openNewMatch(gameInfo.getMatchName(), gameInfo.getMaxPlayers(), mapInfo, 4711L, generateMatchStartedListener(),
				generateMatchInfoUpdatedListener(), generateChatMessageReceiver());
		return generateJoiningGame();
	}

	private IJoiningGame generateJoiningGame() {
		return new IJoiningGame() {
			@Override
			public void setListener(IJoiningGameListener joiningGameListener) {
				MultiplayerGame.this.joiningGameListener = joiningGameListener;
				if (joiningGameListener != null && state.getValue() == EMultiplayerConnectorState.JOINED_GAME) {
					joiningGameListener.gameJoined(generateJoinPhaseGameConnector());
				}
			}

			@Override
			public void abort() {
				networkClient.leaveMatch();
				state.setValue(EMultiplayerConnectorState.CONNECTED_TO_SERVER);
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
				state.setValue(EMultiplayerConnectorState.IN_RUNNING_GAME);

				updatePlayersList(packet.getMatchInfo().getPlayers());

				MapLoader mapLoader = MapList.getDefaultList().getMapById(packet.getMatchInfo().getMapInfo().getId());
				long randomSeed = packet.getRandomSeed();
				byte myPlayerNumber = getMyPlayerNumber();

				JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, myPlayerNumber);

				multiplayerListener.gameIsStarting(game.start());
			}
		};
	}

	byte getMyPlayerNumber() {
		String myId = networkClient.getPlayerInfo().getId();
		byte i = 0;
		for (IMultiplayerPlayer currPlayer : playersList.getItems()) {
			if (currPlayer.getId().equals(myId)) {
				return i;
			}
			i++;
		}
		throw new RuntimeException("Wasn't able to find my id!");
	}

	private IPacketReceiver<MatchInfoUpdatePacket> generateMatchInfoUpdatedListener() {
		return new IPacketReceiver<MatchInfoUpdatePacket>() {
			@Override
			public void receivePacket(MatchInfoUpdatePacket packet) {
				state.setValue(EMultiplayerConnectorState.JOINED_GAME);
				if (joiningGameListener != null) {
					joiningGameListener.gameJoined(generateJoinPhaseGameConnector());
				}

				updatePlayersList(packet.getMatchInfo().getPlayers());
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

	private IJoinPhaseMultiplayerGameConnector generateJoinPhaseGameConnector() {
		return new IJoinPhaseMultiplayerGameConnector() {

			@Override
			public void startGame() {
				state.setValue(EMultiplayerConnectorState.STARTING_GAME);
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
			public IChangingList<IMultiplayerPlayer> getPlayers() {
				return playersList;
			}

			@Override
			public void abort() {
				networkClient.leaveMatch();
				state.setValue(EMultiplayerConnectorState.CONNECTED_TO_SERVER);
			}

			@Override
			public void setChatListener(IChatMessageListener chatMessageListener) {
				MultiplayerGame.this.chatMessageListener = chatMessageListener;
			}
		};
	}

}
