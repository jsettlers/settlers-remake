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
package jsettlers.network.common.packets;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.TestUtils;
import jsettlers.network.client.task.TestTaskPacket;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;
import jsettlers.network.server.packets.ServersideSyncTasksPacket;
import jsettlers.network.server.packets.ServersideTaskPacket;

/**
 * This class tests that the server can handle client side packets with it's server side representative. After the server received a packet, it must also be possible to send the packet back in the
 * form it was received, so that the client can deserialize it correctly.
 * 
 * @author Andreas Eberle
 * 
 */
@Ignore
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
		BufferingPacketListener<TaskPacket> clientListener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET, TaskPacket.DEFAULT_DESERIALIZER);
		BufferingPacketListener<ServersideTaskPacket> serverListener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET, new GenericDeserializer<>(ServersideTaskPacket.class));

		c1.registerListener(clientListener);
		c2.registerListener(serverListener);

		TestTaskPacket clientTestTask = new TestTaskPacket("sodjfsoj2983", 234, (byte) -23);
		c1.sendPacket(ENetworkKey.TEST_PACKET, clientTestTask);

		Thread.sleep(10L);

		List<ServersideTaskPacket> serverPackets = serverListener.popBufferedPackets();
		assertEquals(1, serverPackets.size());
		c2.sendPacket(ENetworkKey.TEST_PACKET, serverPackets.get(0));

		Thread.sleep(10L);

		List<TaskPacket> clientPackets = clientListener.popBufferedPackets();
		assertEquals(1, clientPackets.size());
		assertEquals(clientTestTask, clientPackets.get(0));
	}

	@Test
	public void testSnycTasksPackets() throws InterruptedException {
		BufferingPacketListener<SyncTasksPacket> clientListener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET, new GenericDeserializer<>(SyncTasksPacket.class));
		BufferingPacketListener<ServersideSyncTasksPacket> serverListener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET, new GenericDeserializer<>(ServersideSyncTasksPacket.class));

		c1.registerListener(clientListener);
		c2.registerListener(serverListener);

		SyncTasksPacket clientTestTask = new SyncTasksPacket(234, Arrays.asList(new TestTaskPacket("dsfdsdf", 23, (byte) -3), new TestTaskPacket("iuz)(Z(/TZ�OJ�Jdf", 987875, (byte) -5)));
		c1.sendPacket(ENetworkKey.TEST_PACKET, clientTestTask);

		Thread.sleep(10L);

		List<ServersideSyncTasksPacket> serverPackets = serverListener.popBufferedPackets();
		assertEquals(1, serverPackets.size());
		c2.sendPacket(ENetworkKey.TEST_PACKET, serverPackets.get(0));

		Thread.sleep(10L);

		List<SyncTasksPacket> clientPackets = clientListener.popBufferedPackets();
		assertEquals(1, clientPackets.size());
		assertEquals(clientTestTask, clientPackets.get(0));
	}
}
