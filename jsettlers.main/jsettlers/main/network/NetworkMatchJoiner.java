package jsettlers.main.network;

import java.io.File;
import java.io.Serializable;

import jsettlers.common.network.IMatch;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.network.NetworkMatchOpener.INetworkStartListener;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.restapi.MatchDescription;
import jsettlers.network.server.restapi.MatchPlayer;

public class NetworkMatchJoiner implements INetworkConnectTask {
	private final String serverAddress;
	private final IMatch match;
	private final INetworkStartListener listener;

	public NetworkMatchJoiner(String serverAddress, IMatch match, INetworkStartListener listener) {
		this.serverAddress = serverAddress;
		this.match = match;
		this.listener = listener;
	}

	public void start() {
		new Thread(new JoinNetworkTask(), "network game joiner").start();
	}

	private class JoinNetworkTask implements Runnable, IClientThreadListener {
		private boolean started;
		private MatchDescription matchDescription;
		private Object waitForStartMutex = new Object();

		@Override
		public void run() {
			try {
				ClientThread clientThread = new ClientThread(serverAddress, this);
				clientThread.start();
				clientThread.joinMatch(match.getMatchID());

				synchronized (waitForStartMutex) {
					while (!started) {
						waitForStartMutex.wait();
					}
				}
				notifyJoinSucceeded(clientThread, matchDescription);
			} catch (Throwable t) {
				t.printStackTrace();
				notifyJoindFailed();
			}
		}

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
			// TODO Auto-generated method stub

		}

		@Override
		public void joinedMatchEvent(MatchDescription matchDescription) {
			started = true;
			this.matchDescription = matchDescription;
			synchronized (waitForStartMutex) {
				waitForStartMutex.notifyAll();
			}
		}

		@Override
		public void receivedObject(String sender, Serializable proxiedObject) {
		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void receivedPlayerInfos(MatchDescription matchDescription, MatchPlayer[] playerInfos) {
		}

		@Override
		public void startingMatch(String myID) {
		}

		@Override
		public void mapReceivedEvent(File mapFile) {
		}
	}

	protected void notifyJoindFailed() {
		listener.networkMatchJoinFailed(this);
	}

	protected void notifyJoinSucceeded(ClientThread thread, MatchDescription match) {
		listener.networkMatchJoined(this, thread, match);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}
}
