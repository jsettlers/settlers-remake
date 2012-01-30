package jsettlers.main.network;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.match.MatchPlayer;
import jsettlers.network.server.match.MatchesInfoList;

/**
 * This class retrieves the list of matches for the start screen.
 * 
 * @author michael
 * 
 */
public class NetworkMatchRetriever implements INetworkConnector {
	private INetworkListener listener;
	private String address = "";
	private IMatch[] matches = new IMatch[0];

	private ServerSender currentSender;

	@Override
	public synchronized void setServerAddress(String address) {
		this.address = address;

		reconnect();
		notifyMatchListChanged();
	}

	private void notifyMatchListChanged() {
		if (listener != null) {
			listener.matchListChanged(this);
		}
	}

	private void reconnect() {
		if (currentSender != null) {
			currentSender.disconnect();
		}
		currentSender = new ServerSender(address);
		new Thread(currentSender, "network start sender").start();
	}

	@Override
	public synchronized String getServerAddress() {
		return address;
	}

	@Override
	public synchronized void setListener(INetworkListener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized IMatch[] getMatches() {
		return matches;
	}

	protected synchronized void setMatchList(ServerSender from, IMatch[] matches) {
		if (from == currentSender) {
			this.matches = matches;
			notifyMatchListChanged();
		}
	}

	private class ServerSender implements IClientThreadListener, Runnable {
		private ClientThread clientThread;
		private final String address;
		private boolean disconnected = false;

		public ServerSender(String address) {
			this.address = address;
		}

		public void disconnect() {
			clientThread.cancelConnection();
			disconnected = true;
		}

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
		}

		@Override
		public void joinedMatchEvent(MatchDescription match) {
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {
			IMatch[] matches = new IMatch[matchesList.getMatches().length];
			int i = 0;
			for (MatchDescription m : matchesList.getMatches()) {
				matches[i] = new NetworkMatch(m.getMatchId(), m.getMatchName(), m.getMapId(), m.getMaxPlayers());
				i++;
			}

			setMatchList(this, matches);
		}

		@Override
		public void receivedProxiedObjectEvent(String sender, Serializable proxiedObject) {
		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
		}

		@Override
		public void mapReceivedEvent(File mapFile) {
		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void run() {
			try {
				clientThread = new ClientThread(address, this);
				clientThread.start();
				while (!disconnected) {
					clientThread.requestMatchesList();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: sent connection error
			}
		}

		@Override
		public void receivedPlayerInfos(MatchDescription matchDescription, MatchPlayer[] playerInfos) {
		}

		@Override
		public void startingMatch() {
		}
	}

}
