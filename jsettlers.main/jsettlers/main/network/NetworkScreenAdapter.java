package jsettlers.main.network;

import java.util.ArrayList;
import java.util.List;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.main.ManagedJSettlers;
import jsettlers.network.client.ClientThread;
import jsettlers.network.server.match.MatchDescription;

/**
 * This adapter provides everything the gui needs to display a network screen.
 * 
 * @author michael
 * @see ManagedJSettlers
 */
public class NetworkScreenAdapter implements INetworkScreenAdapter {

	private final ClientThread clientThread;
	private final MatchDescription description;

	public NetworkScreenAdapter(ClientThread clientThread, MatchDescription description) {
		this.clientThread = clientThread;
		this.description = description;
	}

	@Override
	public void setListener(INetworkScreenListener networkScreen) {
		INetworkScreenListener listener = networkScreen;
	}

	@Override
	public List<INetworkPlayer> getPlayerList() {
		ArrayList<INetworkPlayer> list = new ArrayList<INetworkPlayer>();
		for (int i = 0; i < description.getPlayers(); i++) {
			list.add(new NetworkPlayer(i));
		}
		return list;
	}

	private class NetworkPlayer implements INetworkPlayer {
		private final String name;

		public NetworkPlayer(int i) {
			name = "player " + i;
		}

		@Override
		public String getPlayerName() {
			return name;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public void setReady(boolean ready) {
	}

	@Override
	public void sendChatMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub

	}

}
