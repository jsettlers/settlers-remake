package networklib.server.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import networklib.NetworkConstants;

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
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(NetworkConstants.Server.BROADCAST_PORT);

			while (!foundServer && !canceled) {
				try {
					DatagramPacket packet = new DatagramPacket(new byte[NetworkConstants.Server.BROADCAST_BUFFER_LENGTH],
							NetworkConstants.Server.BROADCAST_BUFFER_LENGTH);

					socket.receive(packet);

					String receivedMessage = new String(packet.getData(), 0, packet.getLength());

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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} finally {
			if (socket != null)
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

	public static interface ILanServerAddressListener {
		void foundServerAddress();
	}

	public void cancel() {
		canceled = true;
	}
}
