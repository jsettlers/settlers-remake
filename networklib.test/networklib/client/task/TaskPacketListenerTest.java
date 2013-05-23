package networklib.client.task;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.infrastructure.channel.Channel;

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

		TaskPacket testPacket1 = new TestTaskPacket("tesdfköäl9u8u23jo", 23424, (byte) -2);
		TaskPacket testPacket2 = new TestTaskPacket("?=?=O\"KÖ#'*'::Ö;;Ü", -2342342, (byte) -67);
		int lockstep = 23;
		SyncTasksPacket syncTasksPacket = new SyncTasksPacket(lockstep, Arrays.asList(testPacket1, testPacket2));

		c2.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, syncTasksPacket);

		Thread.sleep(10);
		List<SyncTasksPacket> packets = taskReceiver.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(lockstep, packets.get(0).getLockstepNumber());
		List<TaskPacket> tasks = packets.get(0).getTasks();
		assertEquals(2, tasks.size());
		assertEquals(testPacket1, tasks.get(0));
		assertEquals(testPacket2, tasks.get(1));
	}
}
