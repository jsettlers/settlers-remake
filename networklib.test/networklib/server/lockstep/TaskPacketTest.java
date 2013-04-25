package networklib.server.lockstep;

import static org.junit.Assert.assertEquals;

import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.Channel;
import networklib.channel.feedthrough.FeedthroughBufferPacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link FeedthroughBufferPacket} and the {@link TaskCollectingListener} classes and that they are correctly send and received over a
 * {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskPacketTest {
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
	public void testSendAndReceive() throws InterruptedException {
		// c1 is the channel of the client
		// c2 is the channel of the server

		TestFeedthroughListener testPacketListener = new TestFeedthroughListener(NetworkConstants.Keys.SYNCHRONOUS_TASK);
		c1.registerListener(testPacketListener);

		TaskCollectingListener taskListener = new TaskCollectingListener();
		c2.registerListener(taskListener);

		TestFeedthroughPacket testPacket1 = new TestFeedthroughPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, "TestMessage42", 4711);
		c1.sendPacket(testPacket1); // send packet 1 to server
		TestFeedthroughPacket testPacket2 = new TestFeedthroughPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK,
				"Bla Böb Bün0928ä38(/§/)\"=$(;:I\"H))!", -2342323);
		c1.sendPacket(testPacket2); // send packet 2 to server

		Thread.sleep(10);

		List<FeedthroughBufferPacket> taskPackets = taskListener.getAndResetTasks(); // get collected tasks of server

		assertEquals(0, testPacketListener.packets.size());
		assertEquals(2, taskPackets.size());
		assertEquals(0, taskListener.getAndResetTasks().size());

		for (FeedthroughBufferPacket curr : taskPackets) { // send packets back to client
			c2.sendPacket(curr);
		}

		Thread.sleep(10);

		assertEquals(0, taskListener.getAndResetTasks().size()); // server must have 0 packets
		assertEquals(2, testPacketListener.packets.size()); // client must have 2 packets
		assertEquals(testPacket1, testPacketListener.packets.get(0)); // check that the packets are correctly received
		assertEquals(testPacket2, testPacketListener.packets.get(1));
	}
}
