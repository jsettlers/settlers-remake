package jsettlers.main.network;

import java.io.File;
import java.io.Serializable;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.ManagedJSettlers;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.match.MatchPlayer;
import jsettlers.network.server.match.MatchesInfoList;

/**
 * This adapter provides everything the gui needs to display a network screen.
 * 
 * @author michael
 * @see ManagedJSettlers
 */
public class NetworkScreenAdapter implements INetworkScreenAdapter {

	private final ClientThread clientThread;
	private MatchDescription description;
	private INetworkStartScreenEndListener endListener;
	private INetworkScreenListener networkScreenListener;
	private INetworkPlayer[] playerInfos;

	public NetworkScreenAdapter(ClientThread clientThread, MatchDescription description) {
		this.clientThread = clientThread;
		this.description = description;
		// TODO: @andreas: Register a listener on clientThread, that calls notifyMatchStarted and notifyPlayerlistChanged
		clientThread.setListener(new ScreenAdapterClientThreadListener());
	}

	@Override
	public void setListener(INetworkScreenListener networkScreenListener) {
		this.networkScreenListener = networkScreenListener;
	}

	@Override
	public INetworkPlayer[] getPlayers() {
		return playerInfos;
	}

	@Override
	public void setReady(boolean ready) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendChatMessage(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets notified by the client thread that the match is started.
	 */
	private void notifyMatchStarted() {
		if (endListener != null) {
			endListener.networkMatchStarting(this, this.clientThread);
		}
	}

	/**
	 * Notifies the gui that the player list changed.
	 */
	private void notifyPlayerlistChanged() {
		if (networkScreenListener != null) {
			networkScreenListener.playerListChanged();
		}
	}

	public void setEndListener(INetworkStartScreenEndListener endListener) {
		this.endListener = endListener;
	}

	public void cancel() {
		if (endListener != null) {
			endListener.leftMatch(this);
		}
	}

	private class ScreenAdapterClientThreadListener implements IClientThreadListener {

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
			// TODO Auto-generated method stub
		}

		@Override
		public void joinedMatchEvent(MatchDescription match) {
			System.err.println("should not get here: NetworkScreenAdapter.ScreenAdapterClientThreadListener.joinedMatchEvent()");
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {
			// ignore
		}

		@Override
		public void receivedProxiedObjectEvent(String sender, Serializable proxiedObject) {
			if (proxiedObject instanceof String) {
				networkScreenListener.addChatMessage(sender + ": " + proxiedObject);
			}
		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mapReceivedEvent() {
			// ignore
		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void receivedPlayerInfos(MatchDescription matchDescription, MatchPlayer[] playerInfos) {
			NetworkScreenAdapter.this.description = matchDescription;
			INetworkPlayer[] newInfos = new NetworkPlayer[playerInfos.length];
			for (int i = 0; i < description.getMaxPlayers(); i++) {
				newInfos[i] = new NetworkPlayer(playerInfos[i]);
			}

			NetworkScreenAdapter.this.playerInfos = newInfos;

			if (networkScreenListener != null)
				networkScreenListener.playerListChanged();
		}

	}

	private static final class NetworkPlayer implements INetworkPlayer {
		private final MatchPlayer matchPlayer;

		public NetworkPlayer(MatchPlayer matchPlayer) {
			this.matchPlayer = matchPlayer;
		}

		@Override
		public String getPlayerName() {
			return matchPlayer.getName();
		}

		@Override
		public boolean isReady() {
			return matchPlayer.isReady();
		}
	}
}
