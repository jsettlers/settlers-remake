package networklib.server.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import networklib.channel.NetworkConstants;

/**
 * This thread broadcasts a small package over the network to inform LAN members of the server address
 * 
 * @author Andreas Eberle
 * 
 */
public final class LanServerBroadcastThread extends Thread {

	private boolean canceled = false;

	public LanServerBroadcastThread() {
		super("LanServerBroadcastThread");
		super.setDaemon(true);
	}

	public void cancel() {
		canceled = true;
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			while (!canceled) {
				try {
					byte[] data = NetworkConstants.Server.BROADCAST_MESSAGE.getBytes();

					broadcast(NetworkConstants.Server.BROADCAST_PORT, socket, data);

					Thread.sleep(500);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	private void broadcast(int udpPort, DatagramSocket socket, byte[] data) throws IOException {
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			for (InetAddress address : Collections.list(iface.getInetAddresses())) {
				if (!address.isSiteLocalAddress())
					continue;
				// Java 1.5 doesn't support getting the subnet mask, so try the two most common.
				byte[] ip = address.getAddress();
				ip[3] = -1; // 255.255.255.0
				socket.send(new DatagramPacket(data, data.length, InetAddress.getByAddress(ip), udpPort));
				ip[2] = -1; // 255.255.0.0
				socket.send(new DatagramPacket(data, data.length, InetAddress.getByAddress(ip), udpPort));
			}
		}
	}

}
