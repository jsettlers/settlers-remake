package networklib.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import networklib.TestUtils;
import networklib.channel.Channel;

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
		util.setUpLoopbackChannels();
		c1 = util.getChannel1();
		c2 = util.getChannel2();
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

	private void closeAndJoin() throws InterruptedException {
		Thread.sleep(100);
		c1.close();
		c2.close();
		c1.join();
		c2.join();
	}
}
