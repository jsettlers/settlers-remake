/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.network.infrastructure.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.TestUtils;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;

/**
 * Test for class {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class ChannelTest {
	private Channel c1;
	private Channel c2;

	@Before
	public void setUp() throws IOException {
		Channel[] channels = TestUtils.setUpLoopbackChannels();
		c1 = channels[0];
		c2 = channels[1];
	}

	@After
	public void tearDown() {
		c1.close();
		c2.close();
	}

	@Test
	public void testConnection() throws Exception {
		TestPacketListener listener1 = new TestPacketListener(ENetworkKey.TEST_PACKET);
		TestPacketListener listener2 = new TestPacketListener(ENetworkKey.TEST_PACKET);
		c1.registerListener(listener1);
		c2.registerListener(listener2);
		TestPacket testPackage = new TestPacket("dlkfjs", -23423);
		c1.sendPacket(ENetworkKey.TEST_PACKET, testPackage);
		c2.sendPacket(ENetworkKey.TEST_PACKET, testPackage);

		Thread.sleep(80L);

		assertEquals(1, listener1.packets.size());
		assertEquals(testPackage, listener1.packets.get(0));

		assertEquals(1, listener2.packets.size());
		assertEquals(testPackage, listener2.packets.get(0));
	}

	@Test
	public void testMultiPackets() throws Exception {
		TestPacketListener listener = new TestPacketListener(ENetworkKey.TEST_PACKET);
		c2.registerListener(listener);

		final int NUMBER_OF_PACKETS = 200;

		for (int i = 0; i < NUMBER_OF_PACKETS; i++) {
			c1.sendPacket(ENetworkKey.TEST_PACKET, new TestPacket(i));
		}

		Thread.sleep(30L);

		assertEquals(NUMBER_OF_PACKETS, listener.packets.size());

		for (int i = 0; i < NUMBER_OF_PACKETS; i++) {
			assertEquals(i, listener.packets.get(i).getTestInt());
		}
	}

	@Test
	public void testRoundTripTime() throws InterruptedException {
		Thread.sleep(10L);

		assertNotNull(c1.getRoundTripTime());
		assertNotNull(c2.getRoundTripTime());

		assertTrue(c1.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
		assertTrue(c2.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);

		Thread.sleep(100L);

		assertTrue(c1.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
		assertTrue(c2.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
	}

	@Test
	public void testCloseOneSide() throws InterruptedException {
		assertFalse(c1.isClosed());
		assertFalse(c2.isClosed());

		c1.close();
		assertTrue(c1.isClosed());

		Thread.sleep(40L);
		assertTrue(c2.isClosed());
	}

	@Test
	public void testCloseOtherSide() throws InterruptedException {
		assertFalse(c1.isClosed());
		assertFalse(c2.isClosed());

		c2.close();
		assertTrue(c2.isClosed());

		Thread.sleep(40L);
		assertTrue(c1.isClosed());
	}

	@Test
	public void testChannelClosedListener() throws InterruptedException {
		final int[] closed = new int[1];

		c1.setChannelClosedListener(() -> closed[0]++);

		assertEquals(0, closed[0]);
		c1.close();

		Thread.sleep(30L);
		assertEquals(1, closed[0]);

		Thread.sleep(150L);
		assertEquals(1, closed[0]);
	}

	@Test
	public void testMultiClose() {
		c1.close();
		c2.close();
		c1.close();
		c2.close();
		c1.close();
		c2.close();
	}

	@Test
	public void testSendingOnClosedChannel() {
		c1.close();
		c2.close();

		c1.sendPacket(ENetworkKey.TEST_PACKET, new TestPacket("sdfsdf", 1434));
		c2.sendPacket(ENetworkKey.TEST_PACKET, new TestPacket("dsfsw", 32423));
	}

	@Test
	public void testSendingMessageWithoutListener() throws Exception {
		c1.sendPacket(NetworkConstants.ENetworkKey.ARRAY_OF_MATCHES, new TestPacket("sdfsf���", -2342));
		c2.sendPacket(NetworkConstants.ENetworkKey.ARRAY_OF_MATCHES, new TestPacket("dsfs", 4234)); // test both channels

		Thread.sleep(40L);

		testConnection(); // now the normal test should still work.
	}

	@Test
	public void testRemovingListener() throws Exception {
		TestPacketListener listener = new TestPacketListener(ENetworkKey.TEST_PACKET);
		c2.registerListener(listener);

		TestPacket testPackage = new TestPacket("dsfs", 2332);
		c1.sendPacket(ENetworkKey.TEST_PACKET, testPackage);

		Thread.sleep(80L);

		assertEquals(1, listener.packets.size());
		assertEquals(testPackage, listener.packets.get(0));

		c2.removeListener(listener.getKeys()[0]);

		c1.sendPacket(ENetworkKey.TEST_PACKET, testPackage);
		Thread.sleep(80L);
		assertEquals(1, listener.packets.size());
	}

	@Test
	public void testReadNotAllFromStream() throws Exception {
		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET,
				(key, dis) -> {
					dis.readInt();
					return new TestPacket();
				});

		c2.registerListener(listener);

		TestPacket testPacket = new TestPacket("dfsdufh", 4);
		c1.sendPacket(ENetworkKey.TEST_PACKET, testPacket);

		Thread.sleep(50L);
		assertEquals(1, listener.popBufferedPackets().size());

		testConnection(); // test if connection is still ok
	}

	@Test
	public void testReadMoreFromStream() throws Exception {
		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET,
				(key, dis) -> {
					TestPacket packet = new TestPacket();
					packet.deserialize(dis);
					dis.readInt(); // try to read some more bytes
					dis.readUTF();
					dis.readInt();
					return packet;
				});

		c2.registerListener(listener);

		System.out.println("DON'T WORRY, AN EXCEPTION IS EXPECTED AFTER THIS:");
		TestPacket testPacket = new TestPacket("dfsdufh", 4);
		c1.sendPacket(ENetworkKey.TEST_PACKET, testPacket);

		Thread.sleep(100L);

		testConnection(); // test if connection is still ok
	}

	@Test
	public void testRejectSendingForUnlistenedPackets() throws InterruptedException {
		// construct and connect the listener for reject packets
		BufferingPacketListener<RejectPacket> c1RejectListener = new BufferingPacketListener<>(NetworkConstants.ENetworkKey.REJECT_PACKET, new GenericDeserializer<>(RejectPacket.class));
		c1.registerListener(c1RejectListener);

		// send the packet for an unconnected key from the channel with a reject listener
		c1.sendPacket(ENetworkKey.TEST_PACKET, new EmptyPacket());
		assertEquals(0, c1RejectListener.popBufferedPackets().size());

		Thread.sleep(30L);
		List<RejectPacket> rejects = c1RejectListener.popBufferedPackets();
		assertEquals(1, rejects.size());
		assertEquals(NetworkConstants.ENetworkMessage.NO_LISTENER_FOUND, rejects.get(0).getErrorMessageId());
		assertEquals(ENetworkKey.TEST_PACKET, rejects.get(0).getRejectedKey());
	}

	@Test
	public void testRejectSendingForUnlistenedPacketsLoopProtection() throws InterruptedException {
		// construct and connect the listener for reject packets
		BufferingPacketListener<RejectPacket> c1RejectListener = new BufferingPacketListener<>(NetworkConstants.ENetworkKey.REJECT_PACKET, new GenericDeserializer<>(RejectPacket.class));
		c1.registerListener(c1RejectListener);

		// send the packet for an unconnected key from the channel without a reject listener
		// this provokes a reject packet being send back to c2 BUT this MUST NOT cause a reject packet send to c1.
		c2.sendPacket(ENetworkKey.TEST_PACKET, new EmptyPacket());
		assertEquals(0, c1RejectListener.popBufferedPackets().size());

		Thread.sleep(50L);
		List<RejectPacket> rejects = c1RejectListener.popBufferedPackets();
		assertEquals(0, rejects.size());
	}
}
