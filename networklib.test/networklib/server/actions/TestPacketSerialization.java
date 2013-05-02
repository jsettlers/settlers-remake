package networklib.server.actions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.Channel;
import networklib.channel.GenericDeserializer;
import networklib.channel.IDeserializingable;
import networklib.channel.Packet;
import networklib.channel.listeners.BufferingPacketListener;
import networklib.server.actions.packets.ArrayOfMatchInfosPacket;
import networklib.server.actions.packets.KeyOnlyPacket;
import networklib.server.actions.packets.MapInfoPacket;
import networklib.server.actions.packets.MatchInfoPacket;
import networklib.server.actions.packets.OpenNewMatchPacket;
import networklib.server.actions.packets.PlayerInfoPacket;
import networklib.server.actions.packets.RejectPacket;

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
public class TestPacketSerialization {

	private Channel c1;
	private Channel c2;

	@Before
	public void setUp() throws InterruptedException {
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

	private Packet packet;
	private BufferingPacketListener<? extends Packet> listener;

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ new KeyOnlyPacket(NetworkConstants.Keys.ARRAY_OF_MATCHES), KeyOnlyPacket.DEFAULT_DESERIALIZER },
				{ new PlayerInfoPacket("IDBLA82348-#ülü34r", "NameBKUIH893428())/\"§/"), d(PlayerInfoPacket.class) },
				{ new PlayerInfoPacket(23, "IDBLA82348-#ülü34r", "NameBKUIH893428())/\"§/"), PlayerInfoPacket.WITH_KEY_DESERIALIZER },
				{ new MapInfoPacket("id<30u9Hjdi w3", "Nameo8/(§\"(/!=°", "authorId8unsdkjfn8932", "authorName uHh89023u9h"), d(MapInfoPacket.class) },
				{ createMatchInfoPacket(), d(MatchInfoPacket.class) },
				{ new ArrayOfMatchInfosPacket(new MatchInfoPacket[0]), d(ArrayOfMatchInfosPacket.class) },
				{ new ArrayOfMatchInfosPacket(new MatchInfoPacket[] { createMatchInfoPacket(), createMatchInfoPacket() }),
						d(ArrayOfMatchInfosPacket.class) },
				{ new OpenNewMatchPacket("dfjosj", (byte) 5, new MapInfoPacket("id", "name", "authorid", "authorName")), d(OpenNewMatchPacket.class) },
				{ new RejectPacket(NetworkConstants.Strings.UNAUTHORIZED, NetworkConstants.Keys.IDENTIFY_USER), d(RejectPacket.class) }
		};
		return Arrays.asList(data);
	}

	private static MatchInfoPacket createMatchInfoPacket() {
		MapInfoPacket mapInfo = new MapInfoPacket("sdjfij", "sdfsdflksjdlfk", "sdflnnp0928u30894", "sdlkfkjlÖ:Ö_Ö");
		PlayerInfoPacket[] players = new PlayerInfoPacket[] {
				new PlayerInfoPacket("1dddsfsfd", "787(/(hdsfjhk2"),
				new PlayerInfoPacket("2lkkjsdofij", "0sdfsddfsfgw32dsfjhk2")
		};
		return new MatchInfoPacket("id28948298fedkj", "KHDHifuh(&/%T", (byte) 3, mapInfo, players);
	}

	private static <T extends Packet> Object d(Class<T> classType) {
		return new GenericDeserializer<T>(classType);
	}

	/**
	 * Constructor to accept the parameters of the test.
	 * 
	 * @param packet
	 *            The packet to be sent over the channel and to be compared with the resulting packet.
	 * @param deserializer
	 *            The {@link IDeserializingable} used to deserialize the given packet.
	 */
	public <T extends Packet> TestPacketSerialization(T packet, IDeserializingable<T> deserializer) {
		this.packet = packet;
		this.listener = new BufferingPacketListener<T>(packet.getKey(), deserializer);
	}

	@Test
	public void testSerializationAndDeserialization() throws InterruptedException {
		c2.registerListener(listener);
		c1.sendPacket(packet);

		Thread.sleep(20);

		List<? extends Packet> bufferedPackets = listener.popBufferedPackets();
		assertEquals(1, bufferedPackets.size());
		assertEquals(packet, bufferedPackets.get(0));
	}

}
