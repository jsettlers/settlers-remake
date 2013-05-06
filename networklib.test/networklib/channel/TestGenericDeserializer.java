package networklib.channel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.listeners.BufferingPacketListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenericDeserializer {
	private static final int TEST_KEY = NetworkConstants.Keys.TEST_PACKET;

	private Channel c1;
	private Channel c2;

	@Before
	public void setUp() throws IOException {
		TestUtils util = new TestUtils();
		Channel[] channels = util.setUpLoopbackChannels();
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
		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<TestPacket>(TEST_KEY, new GenericDeserializer<TestPacket>(
				TestPacket.class));
		c1.registerListener(listener);

		TestPacket packet1 = new TestPacket("dsdfskf", 234);
		c2.sendPacket(TEST_KEY, packet1);

		TestPacket packet2 = new TestPacket("sdfsUHUHIhdsjfno09ü23#23l4poi09987)(/)(/§&(/&\"$'_ülü2", -345234);
		c2.sendPacket(TEST_KEY, packet2);

		Thread.sleep(10);

		List<TestPacket> packets = listener.popBufferedPackets();
		assertEquals(2, packets.size());
		assertEquals(packet1, packets.get(0));
		assertEquals(packet2, packets.get(1));
	}

}
