package jsettlers.main.network;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.response.MatchesInfoList;

/**
 * This is the list of matches for the start screen.
 * 
 * @author michael
 * 
 */
public class NetworkConnector implements INetworkConnector {

	private static final List<IMatch> NULL_LIST = Arrays.asList(new IMatch[] {});
	private INetworkListener listener;
	private String address = "";
	private List<IMatch> matches = NULL_LIST;

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
	public synchronized List<IMatch> getMatches() {
		return matches;
	}

	protected synchronized void setMatchList(ServerSender from, List<IMatch> list) {
		if (from == currentSender) {
			this.matches = list;
			notifyMatchListChanged();
		}
	}

	private class ServerSender implements IClientThreadListener, Runnable {
		private ClientThread clientThread;
		private final String address2;
		private boolean disconnected = false;

		public ServerSender(String address) {
			address2 = address;
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
			ArrayList<IMatch> matches = new ArrayList<IMatch>();
			for (Match m : matchesList.getMatches()) {
				matches.add(new NetworkMatch(m.getMatchId(), m.getDescription().getMatchName(), m.getDescription().getMapId(), m.getDescription()
						.getMaxPlayers()));
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
		public void mapReceivedEvent() {
		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void receivedMatchAttendants(String[] matchAttendants) {
		}

		@Override
		public void run() {
			try {
				clientThread = new ClientThread(address2, this);
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
	}

}
