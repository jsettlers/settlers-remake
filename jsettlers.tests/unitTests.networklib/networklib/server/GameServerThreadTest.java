package networklib.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.junit.Test;

/**
 * This class tests the {@link GameServerThread} class.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameServerThreadTest {

	@Test
	public void testStartAndShutdownGameServer() throws IOException, InterruptedException {
		GameServerThread gameServer = new GameServerThread(true);
		gameServer.start();

		Thread.sleep(100);
		assertTrue(gameServer.isAlive());
		assertTrue(gameServer.isLandBroadcasterAlive());

		gameServer.shutdown();

		Thread.sleep(100);
		assertFalse(gameServer.isAlive());
		assertFalse(gameServer.isLandBroadcasterAlive());
	}

	@Test
	public void testRetrieveLanAddress() throws IOException, InterruptedException {
		GameServerThread gameServer = new GameServerThread(true);
		gameServer.start();

		Thread.sleep(10);

		String serverAddress = GameServerThread.retrieveLanServerAddress(1);

		assertNotNull(serverAddress);

		boolean found = false; // check that the returned IP is one of this computers IPs
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface curr = interfaces.nextElement();
			Enumeration<InetAddress> addresses = curr.getInetAddresses();

			while (addresses.hasMoreElements()) {
				if (addresses.nextElement().getHostAddress().equals(serverAddress)) {
					found = true;
				}
			}
		}
		assertTrue(found);

		gameServer.shutdown();
	}

	@Test
	public void testRetrieveLanAddressWithNoSuccess() {
		long start = System.currentTimeMillis();
		String noAddress = GameServerThread.retrieveLanServerAddress(1);
		assertNull(noAddress);

		long diff = System.currentTimeMillis() - start;

		assertTrue(diff < 1050);
	}
}
