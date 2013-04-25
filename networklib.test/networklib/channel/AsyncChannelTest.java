package networklib.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import networklib.TestUtils;
import networklib.channel.listeners.BufferingPacketListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AsyncChannelTest {
	private AsyncChannel c1;
	private AsyncChannel c2;

	@Before
	public void setUp() throws InterruptedException {
		TestUtils util = new TestUtils();
		AsyncChannel[] channels = util.setUpAsyncLoopbackChannels();
		c1 = channels[0];
		c2 = channels[1];
	}

	@After
	public void tearDown() {
		c1.close();
		c2.close();
	}

	@Test
	public void testAsyncSendTime() throws InterruptedException {
		BufferingPacketListener<BlockingTestPacket> listener = new BufferingPacketListener<BlockingTestPacket>(1,
				BlockingTestPacket.DEFAULT_DESERIALIZER);
		c2.registerListener(listener);

		BlockingTestPacket testPackage = new BlockingTestPacket(1, "bla", -234234);

		long start = System.currentTimeMillis();
		c1.sendPacketAsync(testPackage);
		assertTrue(System.currentTimeMillis() - start < 5); // check that the sending is asynchronous

		Thread.sleep(100);

		List<BlockingTestPacket> packets = listener.popBufferedPackets();

		assertEquals(1, packets.size());
		assertEquals(testPackage, packets.get(0));
	}

	@Test
	public void testAsyncReceiveTime() throws InterruptedException {
		final int RUNS = 10;

		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<TestPacket>(1,
				TestPacket.DEFAULT_DESERIALIZER);
		c2.registerListener(listener);

		TestPacket testPackage = new TestPacket(1, "bla", -234234);

		for (int i = 0; i < RUNS; i++) {
			long start = System.currentTimeMillis();
			c1.sendPacketAsync(testPackage);
			assertTrue(System.currentTimeMillis() - start < 5); // check that the sending is asynchronous
		}

		Thread.sleep(10);

		List<TestPacket> packets = listener.popBufferedPackets();

		assertEquals(RUNS, packets.size());
		for (int i = 0; i < RUNS; i++) {
			assertEquals(testPackage, packets.get(i));
		}
	}

	/**
	 * The {@link BlockingTestPacket} is blocking for some time in serialization to simulate the time needed to write the data when it is sent over
	 * the network instead over a loop back.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public static class BlockingTestPacket extends TestPacket {
		public static final IDeserializingable<BlockingTestPacket> DEFAULT_DESERIALIZER = new IDeserializingable<BlockingTestPacket>() {
			@Override
			public BlockingTestPacket deserialize(int key, DataInputStream dis) throws IOException {
				BlockingTestPacket packet = new BlockingTestPacket(key);
				packet.deserialize(dis);
				return packet;
			}
		};

		public BlockingTestPacket(int key, String testString, int testInt) {
			super(key, testString, testInt);
		}

		public BlockingTestPacket(int key) {
			super(key);
		}

		@Override
		public void serialize(DataOutputStream oos) throws IOException {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			super.serialize(oos);
		}

	}
}
