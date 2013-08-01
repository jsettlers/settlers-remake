package networklib.common.packets;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import networklib.NetworkConstants.ENetworkKey;
import networklib.TestUtils;
import networklib.client.task.TestTaskPacket;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.BufferingPacketListener;
import networklib.server.packets.ServersideSyncTasksPacket;
import networklib.server.packets.ServersideTaskPacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests that the server can handle client side packets with it's server side representative. After the server received a packet, it must
 * also be possible to send the packet back in the form it was received, so that the client can deserialize it correctly.
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerClientSideInteroperabilityTest {
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
	public void testTaskPackets() throws InterruptedException {
		BufferingPacketListener<TaskPacket> clientListener = new BufferingPacketListener<TaskPacket>(ENetworkKey.TEST_PACKET,
				TaskPacket.DEFAULT_DESERIALIZER);
		BufferingPacketListener<ServersideTaskPacket> serverListener = new BufferingPacketListener<ServersideTaskPacket>(ENetworkKey.TEST_PACKET,
				new GenericDeserializer<ServersideTaskPacket>(ServersideTaskPacket.class));

		c1.registerListener(clientListener);
		c2.registerListener(serverListener);

		TestTaskPacket clientTestTask = new TestTaskPacket("sodjfsoj2983", 234, (byte) -23);
		c1.sendPacket(ENetworkKey.TEST_PACKET, clientTestTask);

		Thread.sleep(10);

		List<ServersideTaskPacket> serverPackets = serverListener.popBufferedPackets();
		assertEquals(1, serverPackets.size());
		c2.sendPacket(ENetworkKey.TEST_PACKET, serverPackets.get(0));

		Thread.sleep(10);

		List<TaskPacket> clientPackets = clientListener.popBufferedPackets();
		assertEquals(1, clientPackets.size());
		assertEquals(clientTestTask, clientPackets.get(0));
	}

	@Test
	public void testSnycTasksPackets() throws InterruptedException {
		BufferingPacketListener<SyncTasksPacket> clientListener = new BufferingPacketListener<SyncTasksPacket>(ENetworkKey.TEST_PACKET,
				new GenericDeserializer<SyncTasksPacket>(SyncTasksPacket.class));
		BufferingPacketListener<ServersideSyncTasksPacket> serverListener = new BufferingPacketListener<ServersideSyncTasksPacket>(ENetworkKey.TEST_PACKET,
				new GenericDeserializer<ServersideSyncTasksPacket>(ServersideSyncTasksPacket.class));

		c1.registerListener(clientListener);
		c2.registerListener(serverListener);

		SyncTasksPacket clientTestTask = new SyncTasksPacket(234, Arrays.asList((TaskPacket) new TestTaskPacket("dsfdsdf", 23, (byte) -3),
				(TaskPacket) new TestTaskPacket("iuz)(Z(/TZ§OJÖJdf", 987875, (byte) -5)));
		c1.sendPacket(ENetworkKey.TEST_PACKET, clientTestTask);

		Thread.sleep(10);

		List<ServersideSyncTasksPacket> serverPackets = serverListener.popBufferedPackets();
		assertEquals(1, serverPackets.size());
		c2.sendPacket(ENetworkKey.TEST_PACKET, serverPackets.get(0));

		Thread.sleep(10);

		List<SyncTasksPacket> clientPackets = clientListener.popBufferedPackets();
		assertEquals(1, clientPackets.size());
		assertEquals(clientTestTask, clientPackets.get(0));
	}
}
