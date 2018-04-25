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
package jsettlers.network.client.task;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jsettlers.network.NetworkConstants;
import jsettlers.network.TestUtils;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.infrastructure.channel.Channel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link TaskPacketListener} implementation.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskPacketListenerTest {

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
		SyncTasksPacketSchedulerMock taskReceiver = new SyncTasksPacketSchedulerMock();
		TaskPacketListener listener = new TaskPacketListener(taskReceiver);
		c1.registerListener(listener);

		TaskPacket testPacket1 = new TestTaskPacket("tesdfk��l9u8u23jo", 23424, (byte) -2);
		TaskPacket testPacket2 = new TestTaskPacket("?=?=O\"K�#'*'::�;;�", -2342342, (byte) -67);
		int lockstep = 23;
		SyncTasksPacket syncTasksPacket = new SyncTasksPacket(lockstep, Arrays.asList(testPacket1, testPacket2));

		c2.sendPacket(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, syncTasksPacket);

		Thread.sleep(30L);
		List<SyncTasksPacket> packets = taskReceiver.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(lockstep, packets.get(0).getLockstepNumber());
		List<TaskPacket> tasks = packets.get(0).getTasks();
		assertEquals(2, tasks.size());
		assertEquals(testPacket1, tasks.get(0));
		assertEquals(testPacket2, tasks.get(1));
	}
}
