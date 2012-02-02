package jsettlers.main.network;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
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
	private NetworkPlayer[] playerInfos;
	private MapLoader mapLoader;
	private byte myPlayerNumber;

	public NetworkScreenAdapter(final ClientThread clientThread,
	        MatchDescription description) {
		this.clientThread = clientThread;
		this.description = description;
		clientThread.setListener(new ScreenAdapterClientThreadListener());

		new Thread("setReadyThread") {
			@Override
			public void run() {
				try {
					clientThread.requestPlayerInfos();
				} catch (IOException e) {
				}
			}
		}.start();
	}

	public MatchDescription getMatchDescription() {
		return description;
	}

	public MapLoader getMapLoader() {
		return mapLoader;
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
	public void setReady(final boolean ready) {
		new Thread("setReadyThread") {
			@Override
			public void run() {
				try {
					clientThread.setPlayerReady(ready);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void sendChatMessage(final String message) {
		new Thread("sendChatMessageThread") {
			@Override
			public void run() {
				try {
					clientThread.proxyObjectToOthers(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void startNetworkMatch() {
		new Thread("startGameThread") {
			@Override
			public void run() {
				try {
					clientThread.tryToStartMatch();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
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

	private void calculateMyPlayerNumebr(String myID) {
		for (byte i = 0; i < playerInfos.length; i++) {
			if (playerInfos[i] != null
			        && playerInfos[i].getUniqueID().equals(myID)) {
				myPlayerNumber = i;
				return;
			}
		}
		System.err
		        .println("ERROR: couldn't calculate the players playernumber!!!");
	}

	public void setEndListener(INetworkStartScreenEndListener endListener) {
		this.endListener = endListener;
	}

	public void cancel() {
		if (endListener != null) {
			endListener.leftMatch(this);
		}
	}

	public byte getMyPlayerNumber() {
		return myPlayerNumber;
	}

	private final class ScreenAdapterClientThreadListener implements
	        IClientThreadListener {

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
			// TODO Auto-generated method stub
		}

		@Override
		public void joinedMatchEvent(MatchDescription match) {
			System.err
			        .println("should not get here: NetworkScreenAdapter.ScreenAdapterClientThreadListener.joinedMatchEvent()");
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {
			// ignore
		}

		@Override
		public void receivedObject(String sender, Serializable proxiedObject) {
			if (proxiedObject instanceof String) {
				networkScreenListener.addChatMessage(sender + ": "
				        + proxiedObject);
			}
		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mapReceivedEvent(File mapFile) {
			mapLoader = new MapLoader(mapFile);
		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void receivedPlayerInfos(MatchDescription matchDescription,
		        MatchPlayer[] playerInfos) {
			System.err.println("RECEIVED PLAYER INFOS: "
			        + Arrays.toString(playerInfos));

			NetworkScreenAdapter.this.description = matchDescription;
			NetworkPlayer[] newInfos = new NetworkPlayer[playerInfos.length];
			for (int i = 0; i < description.getMaxPlayers(); i++) {
				if (playerInfos[i] != null)
					newInfos[i] = new NetworkPlayer(playerInfos[i]);
			}

			NetworkScreenAdapter.this.playerInfos = newInfos;

			notifyPlayerlistChanged();
		}

		@Override
		public void startingMatch(String myID) {
			System.out.println("NETWORK MATCH STARTED!!!");

			calculateMyPlayerNumebr(myID);

			notifyMatchStarted();
		}

	}

	public String[] getPlayerIDs() {
		String[] ids = new String[playerInfos.length];
		for (int i = 0; i < ids.length; i++) {
			if (playerInfos[i] != null) {
				ids[i] = playerInfos[i].getUniqueID();
			}
		}

		return ids;
	}

	@Override
	public void leaveGame() {
		new Thread("setReadyThread") {
			@Override
			public void run() {
				try {
					clientThread.leaveMatch();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		endListener.leftMatch(this);
	}

}
