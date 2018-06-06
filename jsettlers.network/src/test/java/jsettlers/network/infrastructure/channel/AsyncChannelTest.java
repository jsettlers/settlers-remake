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
package jsettlers.network.infrastructure.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.TestUtils;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for class {@link AsyncChannel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class AsyncChannelTest {
	private AsyncChannel c1;
	private Channel c2;

	@Before
	public void setUp() throws IOException {
		AsyncChannel[] channels = TestUtils.setUpAsyncLoopbackChannels();
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
		BufferingPacketListener<BlockingTestPacket> listener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET,
				BlockingTestPacket.DEFAULT_DESERIALIZER);
		c2.registerListener(listener);

		BlockingTestPacket testPackage = new BlockingTestPacket("bla", -234234);

		long start = System.currentTimeMillis();
		c1.sendPacketAsync(ENetworkKey.TEST_PACKET, testPackage);
		assertTrue(System.currentTimeMillis() - start < 5); // check that the sending is asynchronous

		Thread.sleep(100L);

		List<BlockingTestPacket> packets = listener.popBufferedPackets();

		assertEquals(1, packets.size());
		assertEquals(testPackage, packets.get(0));
	}

	@Test
	public void testAsyncReceiveTime() throws InterruptedException {
		final int RUNS = 10;

		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET,
				TestPacket.DEFAULT_DESERIALIZER);
		c2.registerListener(listener);

		TestPacket testPackage = new TestPacket("bla", -234234);

		for (int i = 0; i < RUNS; i++) {
			c1.sendPacketAsync(ENetworkKey.TEST_PACKET, testPackage);
		}

		Thread.sleep(40L);

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
		public static final IDeserializingable<BlockingTestPacket> DEFAULT_DESERIALIZER = new GenericDeserializer<>(
				BlockingTestPacket.class);

		public BlockingTestPacket(String testString, int testInt) {
			super(testString, testInt);
		}

		public BlockingTestPacket() {
		}

		@Override
		public void serialize(DataOutputStream oos) throws IOException {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
			}
			super.serialize(oos);
		}

	}
}
