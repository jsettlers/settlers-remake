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

import java.io.IOException;
import java.util.List;

import jsettlers.network.TestUtils;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;

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
		BufferingPacketListener<TestPacket> listener = new BufferingPacketListener<>(ENetworkKey.TEST_PACKET,
				new GenericDeserializer<>(
						TestPacket.class));
		c1.registerListener(listener);

		TestPacket packet1 = new TestPacket("dsdfskf", 234);
		c2.sendPacket(ENetworkKey.TEST_PACKET, packet1);

		TestPacket packet2 = new TestPacket("sdfsUHUHIhdsjfno09�23#23l4poi09987)(/)(/�&(/&\"$'_�l�2", -345234);
		c2.sendPacket(ENetworkKey.TEST_PACKET, packet2);

		Thread.sleep(10L);

		List<TestPacket> packets = listener.popBufferedPackets();
		assertEquals(2, packets.size());
		assertEquals(packet1, packets.get(0));
		assertEquals(packet2, packets.get(1));
	}

}
