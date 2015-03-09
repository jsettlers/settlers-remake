package jsettlers.main;

import java.io.IOException;
import java.net.UnknownHostException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.client.NetworkClient;
import networklib.client.interfaces.INetworkClient;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.infrastructure.channel.reject.RejectPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class AsyncNetworkClientConnector {

	private final Object lock = new Object();
	private INetworkClient networkClient = null;
	private AsyncNetworkClientFactoryState state = AsyncNetworkClientFactoryState.CONNECTING_TO_SERVER;

	public AsyncNetworkClientConnector(final String serverAddress, final String userId, final String userName,
			final IPacketReceiver<ArrayOfMatchInfosPacket> matchesRetriever) {
		new Thread("AsyncNetworkClientConnector") {
			@Override
			public void run() {
				try {
					networkClient = new NetworkClient(serverAddress, null);
					networkClient.registerRejectReceiver(generateRejectReceiver());
					networkClient.logIn(userId, userName, generateMatchesRetriever(matchesRetriever));
				} catch (IllegalStateException e) {
					e.printStackTrace(); // this can never happen
					setState(AsyncNetworkClientFactoryState.FAILED_CONNECTING);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					setState(AsyncNetworkClientFactoryState.FAILED_SERVER_NOT_FOUND);
				} catch (IOException e) {
					e.printStackTrace();
					setState(AsyncNetworkClientFactoryState.FAILED_CONNECTING);
				}
			}

			private IPacketReceiver<RejectPacket> generateRejectReceiver() {
				return new IPacketReceiver<RejectPacket>() {
					@Override
					public void receivePacket(RejectPacket packet) {
						if (packet.getRejectedKey() == ENetworkKey.IDENTIFY_USER) {
							setState(AsyncNetworkClientFactoryState.FAILED_CONNECTING);
						}
						System.out.println("Received reject packet: " + packet.getRejectedKey() + " messageid: " + packet.getErrorMessageId());
					}
				};
			}

			private IPacketReceiver<ArrayOfMatchInfosPacket> generateMatchesRetriever(final IPacketReceiver<ArrayOfMatchInfosPacket> matchesRetriever) {
				return new IPacketReceiver<ArrayOfMatchInfosPacket>() {
					@Override
					public void receivePacket(ArrayOfMatchInfosPacket packet) {
						setState(AsyncNetworkClientFactoryState.CONNECTED_TO_SERVER);
						matchesRetriever.receivePacket(packet);
					}
				};
			}

		}.start();
	}

	private void setState(AsyncNetworkClientFactoryState state) {
		if (this.state == AsyncNetworkClientFactoryState.CLOSED) {
			close();
		} else {
			this.state = state;
		}

		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public synchronized void close() {
		if (networkClient != null) {
			networkClient.close();
			networkClient = null;
		}
	}

	/**
	 * This call blocks until the network client is fully connected or failed to connect.
	 * 
	 * @return
	 */
	public INetworkClient getNetworkClient() {
		synchronized (lock) {
			while (state == AsyncNetworkClientFactoryState.CONNECTING_TO_SERVER) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return networkClient;
	}

	public INetworkClient getNetworkClientAsync() {
		return networkClient;
	}

	public enum AsyncNetworkClientFactoryState {
		CONNECTING_TO_SERVER,
		CONNECTED_TO_SERVER,

		FAILED_CONNECTING,
		FAILED_SERVER_NOT_FOUND,

		CLOSED,
	}

}
