package networklib.server.lockstep;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.client.task.TestTaskPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.listeners.BufferingPacketListener;
import networklib.server.match.lockstep.TaskCollectingListener;
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
	private Channel client;
	private Channel server;

	@Before
	public void setUp() throws IOException {
		Channel[] channels = TestUtils.setUpLoopbackChannels();
		client = channels[0];
		server = channels[1];
	}

	@After
	public void tearDown() {
		client.close();
		server.close();
	}

	@Test
	public void testSendAndReceive() throws InterruptedException {
		BufferingPacketListener<TaskPacket> clientListener = new BufferingPacketListener<TaskPacket>(
				NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, TaskPacket.DEFAULT_DESERIALIZER);
		client.registerListener(clientListener);

		TaskCollectingListener serverListener = new TaskCollectingListener();
		server.registerListener(serverListener);

		TestTaskPacket testPacket1 = new TestTaskPacket("TestMessage42", 4711, (byte) -3);
		client.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, testPacket1); // send packet 1 to server
		TestTaskPacket testPacket2 = new TestTaskPacket("Bla B�b B�n0928�38(/�/)\"=$(;:I\"H))!", -2342323, (byte) 4);
		client.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, testPacket2); // send packet 2 to server

		Thread.sleep(10);

		List<ServersideTaskPacket> serversideTaskPackets = serverListener.getAndResetTasks(); // get collected tasks of server

		assertEquals(0, clientListener.popBufferedPackets().size());
		assertEquals(2, serversideTaskPackets.size());
		assertEquals(0, serverListener.getAndResetTasks().size());

		for (ServersideTaskPacket curr : serversideTaskPackets) { // send packets back to client
			server.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, curr);
		}

		Thread.sleep(10);

		List<TaskPacket> packets = clientListener.popBufferedPackets();

		assertEquals(0, serverListener.getAndResetTasks().size()); // server must have 0 packets
		assertEquals(2, packets.size()); // client must have 2 packets
		assertEquals(testPacket1, packets.get(0)); // check that the packets are correctly received
		assertEquals(testPacket2, packets.get(1));
	}
}
