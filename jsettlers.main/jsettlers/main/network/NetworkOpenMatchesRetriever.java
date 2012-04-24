package jsettlers.main.network;

import jsettlers.common.network.IMatch;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.network.client.rest.IRestRequester;
import jsettlers.network.client.rest.ServerRestApiInterface;
import jsettlers.network.server.ServerThread;
import jsettlers.network.server.restapi.MatchDescription;
import jsettlers.network.server.restapi.MatchesInfoList;

/**
 * This class retrieves the list of matches for the start screen.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkOpenMatchesRetriever extends Thread implements INetworkConnector {
	private INetworkListener listener;
	private String server = null;
	private IMatch[] matches = new IMatch[0];

	private boolean started = false;
	private boolean disconnected = false;

	public NetworkOpenMatchesRetriever() {
		super("network open matches retriever");
		super.setDaemon(true);
	}

	@Override
	public synchronized void start() {
		started = true;
		super.start();
	}

	@Override
	public synchronized void setServerAddress(String server) {
		this.server = server;

		if (!started) {
			this.start();
		} else {
			assert server != null : "you can't set the server to null on running match retriever!";
			this.interrupt(); // wake up the thread, so that it does an immediate request
		}
	}

	private void notifyMatchListChanged() {
		if (listener != null) {
			listener.matchListChanged(this);
		}
	}

	@Override
	public synchronized String getServerAddress() {
		return server;
	}

	@Override
	public synchronized void setListener(INetworkListener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized IMatch[] getMatches() {
		return matches;
	}

	protected synchronized void setMatchList(IMatch[] matches) {
		this.matches = matches;
		notifyMatchListChanged();
	}

	@Override
	public void disconnect() {
		disconnected = true;
	}

	@Override
	public void run() {
		while (!disconnected && server == null) {
			server = ServerThread.retrieveLanServerAddress(5); // try to find LAN server

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

		while (!disconnected && server != null) {
			System.out.println("Server: " + server);
			ServerRestApiInterface.request(server, new IRestRequester<MatchesInfoList>() {
				@Override
				public void receive(MatchesInfoList openMatches) {
					IMatch[] matches = new IMatch[openMatches.getOpenMatches().length];
					int i = 0;
					for (MatchDescription m : openMatches.getOpenMatches()) {
						matches[i] = new NetworkMatch(m.getMatchId(), m.getMatchName(), m.getMapId(), m.getMaxPlayers());
						i++;
					}

					setMatchList(matches);
					System.out.println("retrieved network matches: " + matches.length);
				}
			}, "/matches/");

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
}
