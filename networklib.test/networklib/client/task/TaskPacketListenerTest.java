package networklib.client.task;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.Channel;

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
		TestTaskReceiver taskReceiver = new TestTaskReceiver();
		TaskPacketListener listener = new TaskPacketListener(taskReceiver);
		c1.registerListener(listener);

		TestTaskPacket testPacket1 = new TestTaskPacket("tesdfköäl9u8u23jo", 23424, (byte) -2);
		TestTaskPacket testPacket2 = new TestTaskPacket("?=?=O\"KÖ#'*'::Ö;;Ü", -2342342, (byte) -67);

		c2.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, testPacket1);
		c2.sendPacket(NetworkConstants.Keys.SYNCHRONOUS_TASK, testPacket2);

		Thread.sleep(10);
		List<TaskPacket> packets = taskReceiver.popBufferedPackets();
		assertEquals(2, packets.size());
		assertEquals(testPacket1, packets.get(0));
		assertEquals(testPacket2, packets.get(1));
	}
}
