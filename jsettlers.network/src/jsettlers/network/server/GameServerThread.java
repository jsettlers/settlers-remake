package jsettlers.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.socket.ISocketFactory;
import jsettlers.network.infrastructure.log.Logger;
import jsettlers.network.infrastructure.log.LoggerManager;
import jsettlers.network.server.db.IDBFacade;
import jsettlers.network.server.db.inMemory.InMemoryDB;
import jsettlers.network.server.lan.LanServerAddressBroadcastListener;
import jsettlers.network.server.lan.LanServerBroadcastThread;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class GameServerThread extends Thread {

	private static final Logger LOGGER = LoggerManager.ROOT_LOGGER;

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
		LOGGER.log("Server up and running!\n");
		System.out.println("Server up and running!");
		while (!canceled) {
			try {
				Socket clientSocket = serverSocket.accept();

				Channel clientChannel = new Channel(LOGGER, ISocketFactory.DEFAULT_FACTORY.generateSocket(clientSocket));
				manager.identifyNewChannel(clientChannel);
				clientChannel.start();

				LOGGER.log("accepted new client (" + ++counter + "): " + clientSocket);
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

	@Override
	public synchronized void start() {
		super.start();
		manager.start();
	}

	public synchronized void shutdown() {
		canceled = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
		}

		if (lanBroadcastThread != null)
			lanBroadcastThread.shutdown();

		manager.shutdown();
	}

	public boolean isLanBroadcasterAlive() {
		return lanBroadcastThread != null ? lanBroadcastThread.isAlive() : false;
	}

	public IDBFacade getDatabase() {
		return manager.getDatabase();
	}
}
