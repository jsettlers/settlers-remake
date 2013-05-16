package networklib.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.log.Log;
import networklib.server.db.inMemory.InMemoryDB;
import networklib.server.lan.LanServerAddressBroadcastListener;
import networklib.server.lan.LanServerBroadcastThread;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class GameServerThread extends Thread {

	private final ServerSocket serverSocket;
	private final ServerManager manager;
	private final LanServerBroadcastThread lanBroadcastThread;

	private long counter = 0;
	private boolean canceled = false;

	public GameServerThread(boolean lan) throws IOException {
		super("GameServer");
		this.serverSocket = new ServerSocket(NetworkConstants.Server.SERVER_PORT);
		this.manager = new ServerManager(new InMemoryDB());

		this.setDaemon(true);

		if (lan) {
			lanBroadcastThread = new LanServerBroadcastThread();
			lanBroadcastThread.start();
		} else {
			lanBroadcastThread = null;
		}
	}

	@Override
	public void run() {
		Log.log("Server up and running!\n");
		while (!canceled) {
			try {
				Socket clientSocket = serverSocket.accept();

				Channel clientChannel = new Channel(clientSocket);

				manager.identifyNewChannel(clientChannel);

				Log.log("accepted new client (" + ++counter + "): " + clientSocket);
			} catch (SocketException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * NOTE: THIS METHOD IS BLOCKING for the given time
	 * 
	 * @param seconds
	 *            block at maximum the given number of seconds to find the address
	 * 
	 * @return
	 */
	public static String retrieveLanServerAddress(int seconds) {
		LanServerAddressBroadcastListener serverAddressReceiver = new LanServerAddressBroadcastListener();
		try {
			serverAddressReceiver.start();

			for (int i = 0; i < 2 * seconds && !serverAddressReceiver.hasFoundServer(); i++) { // wait at max 5 sek
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (serverAddressReceiver.hasFoundServer()) {
				System.out.println("found server!");
				return serverAddressReceiver.getServerAddress().getHostAddress();
			} else {
				return null;
			}
		} finally {
			serverAddressReceiver.shutdown();
		}
	}

	public void shutdown() {
		canceled = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
		}

		if (lanBroadcastThread != null)
			lanBroadcastThread.shutdown();
	}

	public boolean isLandBroadcasterAlive() {
		return lanBroadcastThread != null ? lanBroadcastThread.isAlive() : false;
	}
}
