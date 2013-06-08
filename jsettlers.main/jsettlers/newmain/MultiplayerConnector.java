package jsettlers.newmain;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.EMultiplayerConnectorState;
import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.newmain.datatypes.ChangingList;
import jsettlers.newmain.datatypes.JoinableGame;
import jsettlers.newmain.datatypes.ObjectContainer;
import networklib.client.NetworkClient;
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

	private INetworkClient networkClient;
	private final ChangingList<IJoinableGame> joinableGames = new ChangingList<IJoinableGame>();
	private ObjectContainer<EMultiplayerConnectorState> state = new ObjectContainer<EMultiplayerConnectorState>(
			EMultiplayerConnectorState.CONNECTING_TO_SERVER);

	public MultiplayerConnector(final String serverAddress, final String userId, final String userName) {
		new Thread() {
			@Override
			public void run() {
				try {
					networkClient = new NetworkClient(serverAddress, null);
					networkClient.logIn(userId, userName, generateMatchesReceiver());

					state.setValue(EMultiplayerConnectorState.CONNECTED_TO_SERVER);
				} catch (IllegalStateException e) {
					e.printStackTrace(); // this can never happen
					state.setValue(EMultiplayerConnectorState.FAILED_CONNECTING);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					state.setValue(EMultiplayerConnectorState.FAILED_SERVER_NOT_FOUND);
				} catch (IOException e) {
					e.printStackTrace();
					state.setValue(EMultiplayerConnectorState.FAILED_CONNECTING);
				}
			}
		}.start();
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
	public IChangingList<? extends IJoinableGame> getJoinableMultiplayerGames() {
		return joinableGames;
	}

	@Override
	public IJoiningGame joinMultiplayerGame(IJoinableGame game) throws IllegalStateException {
		if (state.getValue() != EMultiplayerConnectorState.CONNECTED_TO_SERVER) {
			throw new IllegalStateException("Connector is not connected to the server yet!");
		}

		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClient, state);
		return multiplayerGame.join(game.getId());
	}

	@Override
	public IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo) {
		if (state.getValue() != EMultiplayerConnectorState.CONNECTED_TO_SERVER) {
			throw new IllegalStateException("Connector is not connected to the server yet!");
		}

		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClient, state);
		return multiplayerGame.openNewGame(gameInfo);
	}

	@Override
	public EMultiplayerConnectorState getState() {
		return state.getValue();
	}

	@Override
	public void shutdown() {
		networkClient.close();
	}

	@Override
	public int getRoundTripTimeInMs() {
		return networkClient.getRoundTripTimeInMs();
	}
}
