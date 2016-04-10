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
package jsettlers.network.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import jsettlers.network.server.GameServerThread;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This class tests the {@link GameServerThread} class.
 * 
 * @author Andreas Eberle
 * 
 */
@Ignore
public class GameServerThreadIT {

	@Test
	public void testStartAndShutdownGameServer() throws IOException, InterruptedException {
		GameServerThread gameServer = new GameServerThread(true);
		gameServer.start();

		Thread.sleep(100);
		assertTrue(gameServer.isAlive());
		assertTrue(gameServer.isLanBroadcasterAlive());

		gameServer.shutdown();

		Thread.sleep(100);
		assertFalse(gameServer.isAlive());
		assertFalse(gameServer.isLanBroadcasterAlive());
	}

	@Test
	public void testRetrieveLanAddress() throws IOException, InterruptedException {
		GameServerThread gameServer = new GameServerThread(true);
		gameServer.start();

		Thread.sleep(100);

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

		assertTrue(diff < 1100);
	}
}
