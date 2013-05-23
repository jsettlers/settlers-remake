package networklib.server.lockstep;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.Channel;
import networklib.channel.listeners.BufferingPacketListener;
import networklib.client.task.TestTaskPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.server.packets.ServersideTaskPacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ServersideTaskPacket} and the {@link TaskCollectingListener} classes and that they are correctly send and received over a
 * {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskCollectingListenerTest {
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
	public void testSendAndReceive() throws InterruptedException {
		// c1 is the channel of the client
		// c2 is the channel of the server

		BufferingPacketListener<TaskPacket> clientListener = new BufferingPacketListener<TaskPacket>(
				NetworkConstants.Keys.SYNCHRONOUS_TASK, TaskPacket.DEFAULT_DESERIALIZER);
		c1.registerListener(clientListener);

		TaskCollectingListener serverListener = new TaskCollectingListener();
		c2.registerListener(serverListener);

		TestTaskPacket testPacket1 = new TestTaskPacket("TestMessage42", 4711, (byte) -3);
		c1.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, testPacket1); // send packet 1 to server
		TestTaskPacket testPacket2 = new TestTaskPacket("Bla Böb Bün0928ä38(/§/)\"=$(;:I\"H))!", -2342323, (byte) 4);
		c1.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, testPacket2); // send packet 2 to server

		Thread.sleep(10);

		List<ServersideTaskPacket> serversideTaskPackets = serverListener.getAndResetTasks(); // get collected tasks of server

		assertEquals(0, clientListener.popBufferedPackets().size());
		assertEquals(2, serversideTaskPackets.size());
		assertEquals(0, serverListener.getAndResetTasks().size());

		for (ServersideTaskPacket curr : serversideTaskPackets) { // send packets back to client
			c2.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, curr);
		}

		Thread.sleep(10);

		List<TaskPacket> packets = clientListener.popBufferedPackets();

		assertEquals(0, serverListener.getAndResetTasks().size()); // server must have 0 packets
		assertEquals(2, packets.size()); // client must have 2 packets
		assertEquals(testPacket1, packets.get(0)); // check that the packets are correctly received
		assertEquals(testPacket2, packets.get(1));
	}
}
