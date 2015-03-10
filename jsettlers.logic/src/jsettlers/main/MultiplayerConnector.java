package jsettlers.main;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.main.datatypes.JoinableGame;
import networklib.client.interfaces.INetworkClient;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.MatchInfoPacket;

/**
 * This class implements the {@link IMultiplayerConnector} interface and supports the UI with the list of available multiplayer games and allows to
 * start or create them.
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiplayerConnector implements IMultiplayerConnector {

	private final AsyncNetworkClientConnector networkClientFactory;
	private final ChangingList<IJoinableGame> joinableGames = new ChangingList<IJoinableGame>();

	public MultiplayerConnector(final String serverAddress, final String userId, final String userName) {
		networkClientFactory = new AsyncNetworkClientConnector(serverAddress, userId, userName, generateMatchesReceiver());
	}

	private IPacketReceiver<ArrayOfMatchInfosPacket> generateMatchesReceiver() {
		return new IPacketReceiver<ArrayOfMatchInfosPacket>() {
			@Override
			public void receivePacket(ArrayOfMatchInfosPacket packet) {
				List<IJoinableGame> openGames = new LinkedList<IJoinableGame>();
				for (MatchInfoPacket matchInfo : packet.getMatches()) {
					openGames.add(new JoinableGame(matchInfo));
				}
				joinableGames.setList(openGames);
			}
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
