package jsettlers.newmain;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;
import jsettlers.newmain.datatypes.ChangingList;
import jsettlers.newmain.datatypes.JoinableGame;
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

	private final INetworkClient networkClient;
	private final ChangingList<IJoinableGame> joinableGames = new ChangingList<IJoinableGame>();

	public MultiplayerConnector(String serverAddress, String userId, String userName) throws UnknownHostException, IOException {
		networkClient = new NetworkClient(serverAddress, null);
		try {
			networkClient.logIn(userId, userName, generateMatchesReceiver());
		} catch (IllegalStateException e) { // this can never happen
			e.printStackTrace();
		}
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
		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClient);
		return multiplayerGame.join(game.getId());
	}

	@Override
	public IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo) {
		MultiplayerGame multiplayerGame = new MultiplayerGame(networkClient);
		return multiplayerGame.openNewGame(gameInfo);
	}
}
