package jsettlers.main.network;

import java.io.File;
import java.io.Serializable;

import jsettlers.common.network.IMatchSettings;
import jsettlers.common.resources.ResourceManager;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.ServerThread;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.match.MatchPlayer;
import jsettlers.network.server.match.MatchesInfoList;

/**
 * A helper class for jsettlers to open a network match.
 * <p>
 * it is capable to start a new server.
 * 
 * @author michael
 */
public class NetworkMatchOpener implements INetworkConnectTask {

	private final INetworkStartListener notify;
	private final IMatchSettings gameSettings;
	private final String server;

	public NetworkMatchOpener(IMatchSettings gameSettings, INetworkStartListener notify) {
		// this.server = gameSettings.getServerAddress(); //TODO @Michael implement a way to say if we are in lan or not
		this.server = null;
		this.gameSettings = gameSettings;
		this.notify = notify;
	}

	/**
	 * Starts the game asynchroniously.
	 */
	public void start() {
		new Thread(new StartNetworkTask(), "network game starter").start();
	}

	@Override
	public void cancel() {
		// TODO: allow to cancel connection
	}

	private final class StartNetworkTask implements Runnable, IClientThreadListener {
		private final Object waitForStartMutex = new Object();
		private boolean started = false;
		private MatchDescription match;

		@Override
		public void run() {
			try {
				String serverAddress;
				if (server == null || server.isEmpty()) {
					serverAddress = ServerThread.retrievLanServerAddress(5); // try to find lan server

					if (serverAddress == null) { // if no lan server has been found
						// start new server
						System.err.println("started new server!");
						ServerThread serverThread = new ServerThread(ResourceManager.getTempDirectory(), true);
						serverThread.start();

						serverAddress = "localhost";
					}
				} else {
					serverAddress = server;
				}

				ClientThread clientThread = new ClientThread(serverAddress, this);
				clientThread.start();
				MatchDescription matchDescription = new MatchDescription(gameSettings);
				clientThread.openNewMatch(matchDescription, gameSettings.getMap().getFile());

				synchronized (waitForStartMutex) {
					while (!started) {
						waitForStartMutex.wait();
					}
				}
				notifyStartSucceeded(clientThread, match);

			} catch (Throwable t) {
				t.printStackTrace();
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
		public void receivedPlayerInfos(MatchDescription matchDescription, MatchPlayer[] playerInfos) {
		}

		@Override
		public void startingMatch(String myID) {
		}
	}

	protected void notifyStartFailed() {
		notify.networkMatchJoinFailed(this);
	}

	protected void notifyStartSucceeded(ClientThread thread, MatchDescription match) {
		notify.networkMatchJoined(this, thread, match);
	}

	public interface INetworkStartListener {
		void networkMatchJoined(INetworkConnectTask starter, ClientThread clientThread, MatchDescription description);

		void networkMatchJoinFailed(INetworkConnectTask networkJoiner);
	}
}
