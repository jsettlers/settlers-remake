package networklib.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import networklib.channel.Channel;
import networklib.channel.NetworkConstants;
import networklib.log.Log;
import networklib.server.actions.identify.IdentifyUserListener;
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

	private long counter = 0;

	public GameServerThread(boolean lan) throws IOException {
		super("GameServer");
		this.serverSocket = new ServerSocket(NetworkConstants.Server.SERVER_PORT);
		this.manager = new ServerManager(new InMemoryDB());

		this.setDaemon(true);

		if (lan) {
			new LanServerBroadcastThread().start();
		}
	}

	@Override
	public void run() {
		Log.log("Server up and running!\n");
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();

				Channel clientChannel = new Channel(clientSocket);

				clientChannel.registerListener(new IdentifyUserListener(clientChannel, manager));

				Log.log("accepted new client (" + ++counter + "): " + clientSocket);
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
		serverAddressReceiver.start();

		for (int i = 0; i < 2 * seconds && !serverAddressReceiver.hasFoundServer(); i++) { // wait at max 5 sek
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		serverAddressReceiver.cancel();

		if (serverAddressReceiver.hasFoundServer()) {
			System.out.println("found server!");
			return serverAddressReceiver.getServerAddress().getHostAddress();
		} else {
			return null;
		}
	}
}
