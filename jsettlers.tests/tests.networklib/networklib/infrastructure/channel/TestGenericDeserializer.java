package networklib.infrastructure.channel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants.ENetworkKey;
import networklib.TestUtils;
import networklib.infrastructure.channel.listeners.BufferingPacketListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenericDeserializer {
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
	public void testGenericPacket() throws InterruptedException {
		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<TestPacket>(ENetworkKey.TEST_PACKET,
				new GenericDeserializer<TestPacket>(
						TestPacket.class));
		c1.registerListener(listener);

		TestPacket packet1 = new TestPacket("dsdfskf", 234);
		c2.sendPacket(ENetworkKey.TEST_PACKET, packet1);

		TestPacket packet2 = new TestPacket("sdfsUHUHIhdsjfno09�23#23l4poi09987)(/)(/�&(/&\"$'_�l�2", -345234);
		c2.sendPacket(ENetworkKey.TEST_PACKET, packet2);

		Thread.sleep(10);

		List<TestPacket> packets = listener.popBufferedPackets();
		assertEquals(2, packets.size());
		assertEquals(packet1, packets.get(0));
		assertEquals(packet2, packets.get(1));
	}

}
