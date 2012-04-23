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
import jsettlers.network.server.ServerThread;
import jsettlers.network.server.restapi.MatchDescription;
import jsettlers.network.server.restapi.MatchPlayer;
import jsettlers.network.server.restapi.MatchesInfoList;

/**
 * This class retrieves the list of matches for the start screen.
 * 
 * @author michael
 * 
 */
public class NetworkMatchRetriever implements INetworkConnector {
	private INetworkListener listener;
	private String address = null;
	private IMatch[] matches = new IMatch[0];

	private ServerSender currentSender;

	@Override
	public synchronized void setServerAddress(String address) {
		this.address = null;

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
		currentSender = new ServerSender();
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
		private boolean disconnected = false;

		public void disconnect() {
			if (clientThread != null)
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
			IMatch[] matches = new IMatch[matchesList.getOpenMatches().length];
			int i = 0;
			for (MatchDescription m : matchesList.getOpenMatches()) {
				matches[i] = new NetworkMatch(m.getMatchId(), m.getMatchName(), m.getMapId(), m.getMaxPlayers());
				i++;
			}

			setMatchList(this, matches);
			System.out.println("retrieved network matches: " + matches.length);
		}

		@Override
		public void receivedObject(String sender, Serializable proxiedObject) {
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
			if (address == null) {
				address = ServerThread.retrievLanServerAddress(5); // try to find lan server
			}

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
		public void startingMatch(String myID) {
		}
	}

}
