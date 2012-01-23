package jsettlers.main;

import java.io.File;

import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.INetworkableObject;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.response.MatchesInfoList;

/**
 * A helper class for jsettlers to start the network.
 * <p>
 * it is capable to start a new server.
 * 
 * @author michael
 */
public class NetworkStarter implements NetworkConnectTask {

	private final INetworkStartListener notify;
	private final IMatchSettings gameSettings;
	private ProgressConnector progress;
	private final String server;

	public NetworkStarter(String server, IMatchSettings gameSettings,
	        INetworkStartListener notify) {
		this.server = server;
		this.gameSettings = gameSettings;
		this.notify = notify;
	}

	/**
	 * Starts the game asynchroniously.
	 * 
	 * @param progress
	 */
	public void start(ProgressConnector progress) {
		this.progress = progress;
		new Thread(new StartNetworkTask(), "network game starter").start();
	}

	public void cancel() {
		// TODO: allow to cancel connection
	}

	private final class StartNetworkTask implements Runnable,
	        IClientThreadListener {
		private final Object waitForStartMutex = new Object();
		private boolean started = false;
		private MatchDescription match;

		@Override
		public void run() {
			try {
				ClientThread clientThread = new ClientThread(server, this);
				clientThread.start();
				MatchDescription matchDescription =
				        new MatchDescription(gameSettings);
				clientThread.startNewMatch(matchDescription, gameSettings
				        .getMap().getFile());

				synchronized (waitForStartMutex) {
					while (!started) {
						waitForStartMutex.wait();
					}
				}
				notifyStartSucceeded(clientThread, match);

			} catch (Throwable t) {
				notifyStartFailed();
			}
		}

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
		}

		@Override
		public void joinedMatchEvent(MatchDescription match) {
			started = true;
			this.match = match;
			synchronized (waitForStartMutex) {
				waitForStartMutex.notifyAll();
			}
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {

		}

		@Override
		public void receivedProxiedObjectEvent(String sender,
		        INetworkableObject proxiedObject) {
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
	}

	protected void notifyStartFailed() {
		notify.networkGameStartFailed(this);
	}

	protected void notifyStartSucceeded(ClientThread thread, MatchDescription match) {
		notify.networkGameStarted(this, thread, match);
	}

	public interface INetworkStartListener {
		void networkGameStarted(NetworkConnectTask starter,
		        ClientThread clientThread, MatchDescription description);

		void networkGameStartFailed(NetworkConnectTask networkJoiner);
	}
}
