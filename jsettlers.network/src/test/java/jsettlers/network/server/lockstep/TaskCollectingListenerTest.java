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
package jsettlers.network.server.lockstep;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jsettlers.network.NetworkConstants;
import jsettlers.network.TestUtils;
import jsettlers.network.client.task.TestTaskPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;
import jsettlers.network.server.match.lockstep.TaskCollectingListener;
import jsettlers.network.server.packets.ServersideTaskPacket;

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
		BufferingPacketListener<TaskPacket> clientListener = new BufferingPacketListener<>(
				NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, TaskPacket.DEFAULT_DESERIALIZER);
		client.registerListener(clientListener);

		TaskCollectingListener serverListener = new TaskCollectingListener();
		server.registerListener(serverListener);

		TestTaskPacket testPacket1 = new TestTaskPacket("TestMessage42", 4711, (byte) -3);
		client.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, testPacket1); // send packet 1 to server
		TestTaskPacket testPacket2 = new TestTaskPacket("Bla B�b B�n0928�38(/�/)\"=$(;:I\"H))!", -2342323, (byte) 4);
		client.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, testPacket2); // send packet 2 to server

		Thread.sleep(50L);

		List<ServersideTaskPacket> serversideTaskPackets = serverListener.getAndResetTasks(); // get collected tasks of server

		assertEquals(0, clientListener.popBufferedPackets().size());
		assertEquals(2, serversideTaskPackets.size());
		assertEquals(0, serverListener.getAndResetTasks().size());

		for (ServersideTaskPacket curr : serversideTaskPackets) { // send packets back to client
			server.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, curr);
		}

		Thread.sleep(50L);

		List<TaskPacket> packets = clientListener.popBufferedPackets();

		assertEquals(0, serverListener.getAndResetTasks().size()); // server must have 0 packets
		assertEquals(2, packets.size()); // client must have 2 packets
		assertEquals(testPacket1, packets.get(0)); // check that the packets are correctly received
		assertEquals(testPacket2, packets.get(1));
	}
}
