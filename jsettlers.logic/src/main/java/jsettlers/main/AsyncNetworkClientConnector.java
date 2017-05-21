/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main;

import java.io.IOException;
import java.net.UnknownHostException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.NetworkClient;
import jsettlers.network.client.interfaces.INetworkClient;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;

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
				return packet -> {
					if (packet.getRejectedKey() == ENetworkKey.IDENTIFY_USER) {
						setState(AsyncNetworkClientFactoryState.FAILED_CONNECTING);
					}
					System.out.println("Received reject packet: " + packet.getRejectedKey() + " messageid: " + packet.getErrorMessageId());
				};
			}

			private IPacketReceiver<ArrayOfMatchInfosPacket> generateMatchesRetriever(final IPacketReceiver<ArrayOfMatchInfosPacket> matchesRetriever) {
				return packet -> {
					setState(AsyncNetworkClientFactoryState.CONNECTED_TO_SERVER);
					matchesRetriever.receivePacket(packet);
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
