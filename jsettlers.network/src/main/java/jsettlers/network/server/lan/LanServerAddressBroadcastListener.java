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
package jsettlers.network.server.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import jsettlers.network.NetworkConstants;

/**
 * Listens for a broadcasted address of a LAN game server.
 * 
 * @author Andreas Eberle
 * 
 */
public final class LanServerAddressBroadcastListener extends Thread {

	private final ILanServerAddressListener listener;

	private boolean foundServer = false;
	private InetAddress serverAddress = null;

	private boolean canceled;
	private DatagramSocket socket;

	public LanServerAddressBroadcastListener(ILanServerAddressListener listener) {
		super("LanServerAddressListener");
		this.listener = listener;
		super.setDaemon(true);
	}

	public LanServerAddressBroadcastListener() {
		this(null);
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(NetworkConstants.Server.BROADCAST_PORT);

			while (!foundServer && !canceled) {
				try {
					DatagramPacket packet = new DatagramPacket(new byte[NetworkConstants.Server.BROADCAST_BUFFER_LENGTH],
							NetworkConstants.Server.BROADCAST_BUFFER_LENGTH);

					socket.receive(packet);

					String receivedMessage = new String(packet.getData(), packet.getOffset(), packet.getLength());

					System.out.println("sender: " + packet.getAddress() + " port: " + packet.getPort() + " length: " + packet.getData().length
							+ " data: " + receivedMessage);

					if (NetworkConstants.Server.BROADCAST_MESSAGE.equals(receivedMessage)) {
						System.out.println("received broadcast info for jsettlers");
						foundServer = true;
						serverAddress = packet.getAddress();

						if (listener != null) {
							listener.foundServerAddress();
						}
					}
				} catch (SocketException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} finally {
			socket.close();
		}
	}

	/**
	 * 
	 * @return true if a LAN server has been found, <br>
	 *         false otherwise.
	 * 
	 */
	public boolean hasFoundServer() {
		return foundServer;
	}

	public InetAddress getServerAddress() {
		return serverAddress;
	}

	public interface ILanServerAddressListener {
		void foundServerAddress();
	}

	public void shutdown() {
		canceled = true;
		socket.close();
	}
}
