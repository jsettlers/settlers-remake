package jsettlers.main.network;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.restapi.MatchDescription;
import jsettlers.network.server.restapi.MatchPlayer;
import network.INetworkServerConnector;
import network.IProxiedObjectListener;

public class NetworkRunningGameConnector implements IClientThreadListener, INetworkServerConnector {

	private IProxiedObjectListener proxiedObjectListener = null;
	private final ProxyObjectsThread proxyThread;

	public NetworkRunningGameConnector(ClientThread clientThread) {
		proxyThread = new ProxyObjectsThread(clientThread);
		proxyThread.start();
	}

	@Override
	public void addProxiedObjectListener(IProxiedObjectListener listener) {
		proxiedObjectListener = listener;
	}

	private static class ProxyObjectsThread extends Thread {
		LinkedBlockingQueue<ProxyObject> queue = new LinkedBlockingQueue<ProxyObject>();
		private final ClientThread clientThread;

		ProxyObjectsThread(ClientThread clientThread) {
			super("proxyObjectsThread");
			this.clientThread = clientThread;
			super.setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				ProxyObject proxyObject = null;
				do {
					try {
						proxyObject = queue.take();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} while (proxyObject == null);

				try {
					if (proxyObject.receiver == null) {
						clientThread.proxyObjectToOthers(proxyObject.object);
					} else {
						clientThread.sendObjectTo(proxyObject.receiver, proxyObject.object);
					}
				} catch (IOException e) {
					e.printStackTrace(); // TODO error handling
				}
			}
		}
	}

	private static class ProxyObject {
		final Serializable object;
		final String receiver;

		ProxyObject(String receiver, Serializable object) {
			this.receiver = receiver;
			this.object = object;
		}

		ProxyObject(Serializable object) {
			this.receiver = null;
			this.object = object;
		}
	}

	@Override
	public void proxyObjectToOthers(final Serializable o) {
		try {
			proxyThread.queue.put(new ProxyObject(o));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendObjectTo(final String receiver, final Serializable object) {
		try {
			proxyThread.queue.put(new ProxyObject(receiver, object));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void requestFailedEvent(EClientRequest failedRequest) {
		// TODO handle the error.
	}

	@Override
	public void joinedMatchEvent(MatchDescription match) {
		// ignore
	}

	@Override
	public void receivedObject(String sender, Serializable object) {
		proxiedObjectListener.receivedObject(sender, object);
	}

	@Override
	public void playerLeftEvent(String leavingPlayer) {
		// TODO print message on GUI
	}

	@Override
	public void mapReceivedEvent(File mapFile) {
		// ignore
	}

	@Override
	public File getMapFolder() {
		// can't happen here
		return null;
	}

	@Override
	public void receivedPlayerInfos(MatchDescription matchDescription, MatchPlayer[] playerInfos) {
		// ignore
	}

	@Override
	public void startingMatch(String myID) {
		// can't happen here
	}

}
