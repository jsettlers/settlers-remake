package jsettlers.main;

import java.io.File;

import jsettlers.common.network.IMatch;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.NetworkStarter.INetworkStartListener;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.INetworkableObject;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.response.MatchesInfoList;

public class NetworkJoiner implements NetworkConnectTask {
	private final String serverAddress;
	private final IMatch match;
	private final INetworkStartListener listener;

	public NetworkJoiner(String serverAddress, IMatch match,
	        INetworkStartListener listener) {
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
				ClientThread clientThread =
				        new ClientThread(serverAddress, this);
				clientThread.start();
				clientThread.joinMatch(match.getMatchID());

				synchronized (waitForStartMutex) {
					while (!started) {
						waitForStartMutex.wait();
					}
				}
				notifyStartSucceeded(clientThread, matchDescription);
			} catch (Throwable t) {
				t.printStackTrace();
				notifyStartFailed();
			}
		}

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
			// TODO Auto-generated method stub

		}

		@Override
		public void joinedMatchEvent(
		        MatchDescription matchDescription) {
			started = true;
			this.matchDescription = matchDescription;
			synchronized (waitForStartMutex) {
				waitForStartMutex.notifyAll();
			}
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {
			// TODO Auto-generated method stub

		}

		@Override
		public void receivedProxiedObjectEvent(String sender,
		        INetworkableObject proxiedObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mapReceivedEvent() {
			// TODO Auto-generated method stub

		}

		@Override
		public File getMapFolder() {
			return MapList.getDefaultFolder();
		}

		@Override
		public void receivedMatchAttendants(String[] matchAttendants) {
			// TODO Auto-generated method stub

		}
	}

	protected void notifyStartFailed() {
		listener.networkGameStartFailed(this);
	}

	protected void notifyStartSucceeded(ClientThread thread,
	        MatchDescription match) {
		listener.networkGameStarted(this, thread, match);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}
}
