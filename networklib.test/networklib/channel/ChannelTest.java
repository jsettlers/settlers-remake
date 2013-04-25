package networklib.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import networklib.NetworkConstants;
import networklib.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	public void setUp() throws InterruptedException {
		TestUtils util = new TestUtils();
		Channel[] channels = util.setUpLoopbackChannels();
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
		TestPacketListener listener = new TestPacketListener(1);
		c2.registerListener(listener);
		TestPacket testPackage = new TestPacket(1);
		c1.sendPacket(testPackage);

		closeAndJoin();

		assertEquals(1, listener.packets.size());
		assertEquals(testPackage, listener.packets.get(0));
	}

	@Test
	public void testMultiPackets() throws Exception {
		TestPacketListener listener = new TestPacketListener(1);
		c2.registerListener(listener);

		final int NUMBER_OF_PACKETS = 200;

		for (int i = 0; i < NUMBER_OF_PACKETS; i++) {
			c1.sendPacket(new TestPacket(1, i));
		}

		closeAndJoin();

		assertEquals(NUMBER_OF_PACKETS, listener.packets.size());

		for (int i = 0; i < NUMBER_OF_PACKETS; i++) {
			assertEquals(i, listener.packets.get(i).getTestInt());
		}
	}

	@Test
	public void testRoundTripTime() throws InterruptedException {
		Thread.sleep(10);

		assertNotNull(c1.getRoundTripTime());
		assertNotNull(c2.getRoundTripTime());

		assertTrue(c1.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
		assertTrue(c2.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);

		Thread.sleep(100);

		assertTrue(c1.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
		assertTrue(c2.getRoundTripTime().getLastUpdated() - System.currentTimeMillis() < 5);
	}

	@Test
	public void testCloseOneSide() throws InterruptedException {
		assertFalse(c1.isClosed());
		assertFalse(c2.isClosed());

		c1.close();
		assertTrue(c1.isClosed());

		Thread.sleep(10);
		assertTrue(c2.isClosed());
	}

	@Test
	public void testCloseOtherSide() throws InterruptedException {
		assertFalse(c1.isClosed());
		assertFalse(c2.isClosed());

		c2.close();
		assertTrue(c2.isClosed());

		Thread.sleep(10);
		assertTrue(c1.isClosed());
	}

	@Test
	public void testChannelClosedListener() throws InterruptedException {
		final int[] closed = new int[1];

		c1.setChannelClosedListener(new IChannelClosedListener() {
			@Override
			public void channelClosed() {
				closed[0]++;
			}
		});

		assertEquals(0, closed[0]);
		c1.close();

		Thread.sleep(10);
		assertEquals(1, closed[0]);

		Thread.sleep(100);
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

		c1.sendPacket(new TestPacket(2));
		c2.sendPacket(new TestPacket(2));
	}

	@Test
	public void testSendingMessageWithoutListener() throws Exception {
		c1.sendPacket(new TestPacket(NetworkConstants.Keys.LIST_OF_MATCHES));
		c2.sendPacket(new TestPacket(NetworkConstants.Keys.LIST_OF_MATCHES)); // test both channels

		Thread.sleep(10);

		testConnection(); // now the normal test should still work.
	}

	private void closeAndJoin() throws InterruptedException {
		Thread.sleep(100);
		c1.close();
		c2.close();
		c1.join();
		c2.join();
	}
}
