package networklib.client.receiver;

import static org.junit.Assert.assertEquals;

import java.util.List;

import networklib.channel.TestPacket;

import org.junit.Test;

/**
 * Test for the class {@link BufferingPacketReceiver}.
 * 
 * @author Andreas Eberle
 * 
 */
public class BufferingPacketReceiverTest {

	private BufferingPacketReceiver<TestPacket> receiver = new BufferingPacketReceiver<TestPacket>();

	@Test
	public void testEmptyAtStart() {
		assertEquals(0, receiver.popBufferedPackets().size());
		assertEquals(0, receiver.popBufferedPackets().size());
	}

	@Test
	public void testAddMultipleAndPop() {
		TestPacket[] packets = new TestPacket[] {
				new TestPacket(3, "sgpks", -23424),
				new TestPacket(434, "dsdssdfdsfsfsfgpdsf", 8767624),
				new TestPacket(23, "&/)(&(/\"QZ)U", 234)
		};

		for (TestPacket curr : packets) {
			receiver.receivePacket(curr);
		}

		List<TestPacket> receivedPackets = receiver.popBufferedPackets();
		assertEquals(packets.length, receivedPackets.size());
		assertEquals(0, receiver.popBufferedPackets().size());

		for (int i = 0; i < packets.length; i++) {
			assertEquals(packets[i], receivedPackets.get(i));
		}
	}
}
