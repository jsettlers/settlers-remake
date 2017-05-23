/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.network.common.packets;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkMessage;
import jsettlers.network.TestUtils;
import jsettlers.network.client.task.TestTaskPacket;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.listeners.BufferingPacketListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.infrastructure.channel.ping.PingPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.packets.ServersideSyncTasksPacket;
import jsettlers.network.server.packets.ServersideTaskPacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the serialization and deserialization of multiple subclasses of {@link Packet} when sent over a {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
@RunWith(value = Parameterized.class)
public class PacketSerializationTest {

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

	private Packet packet;
	private BufferingPacketListener<? extends Packet> listener;

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ new EmptyPacket(), EmptyPacket.DEFAULT_DESERIALIZER },
				{ new PlayerInfoPacket("IDBLA82348-#�l�34r", "NameBKUIH893428())/\"�/", true), d(PlayerInfoPacket.class) },
				{ new MapInfoPacket("id<30u9Hjdi w3", "Nameo8/(�\"(/!=�", "authorId8unsdkjfn8932", "authorName uHh89023u9h", 6),
						d(MapInfoPacket.class) },
				{ createMatchInfoPacket(), d(MatchInfoPacket.class) },
				{ new ArrayOfMatchInfosPacket(new MatchInfoPacket[0]), d(ArrayOfMatchInfosPacket.class) },
				{ new ArrayOfMatchInfosPacket(new MatchInfoPacket[] { createMatchInfoPacket(), createMatchInfoPacket() }),
						d(ArrayOfMatchInfosPacket.class) },
				{ new OpenNewMatchPacket("dfjosj", (byte) 5, new MapInfoPacket("id", "name", "authorid", "authorName", 6), -3453434534329434535L),
						d(OpenNewMatchPacket.class) },
				{ new RejectPacket(NetworkConstants.ENetworkMessage.UNAUTHORIZED, NetworkConstants.ENetworkKey.IDENTIFY_USER), d(RejectPacket.class) },
				{ new MatchStartPacket(createMatchInfoPacket(), 23424L), d(MatchStartPacket.class) },
				{ new MatchInfoUpdatePacket(ENetworkMessage.NO_LISTENER_FOUND, new PlayerInfoPacket("IDBLA82348-#�l�34r",
						"NameBKUIH893428())/\"�/", true), createMatchInfoPacket()), d(MatchInfoUpdatePacket.class) },
				{ new TimeSyncPacket(23424), d(TimeSyncPacket.class) },

				{ new ServersideTaskPacket("sdfsfsdf".getBytes()), d(ServersideTaskPacket.class) },
				{ new ServersideSyncTasksPacket(23, Arrays.asList(new ServersideTaskPacket("dsfjsfj".getBytes()),
						new ServersideTaskPacket("ehgdhd".getBytes()))), d(ServersideSyncTasksPacket.class) },

				{ new TestTaskPacket("tesdfk��l9/&%/%&\"\\u8u23jo", 23424, (byte) -2), TaskPacket.DEFAULT_DESERIALIZER },
				{ new SyncTasksPacket(234, Arrays.asList(new TestTaskPacket("dsfdsdf", 23, (byte) -3),
						new TestTaskPacket("dsfs��#��dsdf", 4345, (byte) 5))), d(SyncTasksPacket.class) },

				{ new BooleanMessagePacket(true), d(BooleanMessagePacket.class) },
				{ new ChatMessagePacket("authorId(, message)U)(Z", "message'**�##\"\\ppoisudf08u("), d(ChatMessagePacket.class) },

				{ new PingPacket(2324L, -2349879879787987234L), d(PingPacket.class) },
				{ new IdPacket("()Z(/�\\\"THKJNI+�02i3ej"), d(IdPacket.class) }
		};
		return Arrays.asList(data);
	}

	private static MatchInfoPacket createMatchInfoPacket() {
		MapInfoPacket mapInfo = new MapInfoPacket("sdjfij", "sdfsdflksjdlfk", "sdflnnp0928u30894", "sdlkfkjl�:�_�", 5);
		PlayerInfoPacket[] players = new PlayerInfoPacket[] {
				new PlayerInfoPacket("1dddsfsfd", "787(/(hdsfjhk2", true),
				new PlayerInfoPacket("2lkkjsdofij", "0sdfsddfsfgw32dsfjhk2", false)
		};
		return new MatchInfoPacket("id28948298fedkj", "KHDHifuh(&/%T", (byte) 3, mapInfo, players);
	}

	private static <T extends Packet> Object d(Class<T> classType) {
		return new GenericDeserializer<>(classType);
	}

	/**
	 * Constructor to accept the parameters of the test.
	 * 
	 * @param packet
	 *            The packet to be sent over the channel and to be compared with the resulting packet.
	 * @param deserializer
	 *            The {@link IDeserializingable} used to deserialize the given packet.
	 */
	public <T extends Packet> PacketSerializationTest(T packet, IDeserializingable<T> deserializer) {
		this.packet = packet;
		this.listener = new BufferingPacketListener<>(NetworkConstants.ENetworkKey.TEST_PACKET, deserializer);
	}

	@Test
	public void testSerializationAndDeserialization() throws InterruptedException {
		c2.registerListener(listener);
		c1.sendPacket(NetworkConstants.ENetworkKey.TEST_PACKET, packet);

		Thread.sleep(150);

		List<? extends Packet> bufferedPackets = listener.popBufferedPackets();
		assertEquals(1, bufferedPackets.size());
		assertEquals(packet, bufferedPackets.get(0));
		assertEquals(packet.hashCode(), bufferedPackets.get(0).hashCode());
	}
}
