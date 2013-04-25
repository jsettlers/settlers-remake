package networklib.channel;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.listeners.PacketChannelListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenericDeserializer {
	private static final int TEST_KEY = NetworkConstants.Keys.TEST;

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

	@Test
	public void testGenericPacket() throws InterruptedException {
		GenericTestPacketListener listener = new GenericTestPacketListener();
		c1.registerListener(listener);

		GenericTestPacket packet1 = new GenericTestPacket("dsdfskf", 234);
		c2.sendPacket(packet1);

		GenericTestPacket packet2 = new GenericTestPacket("sdfsUHUHIhdsjfno09ü23#23l4poi09987)(/)(/§&(/&\"$'_ülü2", -345234);
		c2.sendPacket(packet2);

		Thread.sleep(10);

		assertEquals(2, listener.packets.size());
		assertEquals(packet1, listener.packets.get(0));
		assertEquals(packet2, listener.packets.get(1));
	}

	public static class GenericTestPacket extends Packet {
		private String testMessage;
		private int testInt;

		public GenericTestPacket() {
			super(TEST_KEY);
		}

		public GenericTestPacket(String testMessage, int testInt) {
			this();
			this.testMessage = testMessage;
			this.testInt = testInt;
		}

		@Override
		public void serialize(DataOutputStream dos) throws IOException {
			dos.writeUTF(testMessage);
			dos.writeInt(testInt);
		}

		@Override
		public void deserialize(DataInputStream dis) throws IOException {
			testMessage = dis.readUTF();
			testInt = dis.readInt();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + testInt;
			result = prime * result + ((testMessage == null) ? 0 : testMessage.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GenericTestPacket other = (GenericTestPacket) obj;
			if (testInt != other.testInt)
				return false;
			if (testMessage == null) {
				if (other.testMessage != null)
					return false;
			} else if (!testMessage.equals(other.testMessage))
				return false;
			return true;
		}
	}

	public static class GenericTestPacketListener extends PacketChannelListener<GenericTestPacket> {
		public GenericTestPacketListener() {
			super(TEST_KEY, new GenericDeserializer<GenericTestPacket>(GenericTestPacket.class));
		}

		public final LinkedList<GenericTestPacket> packets = new LinkedList<GenericTestPacket>();

		@Override
		protected void receivePacket(GenericTestPacket deserialized) {
			this.packets.addLast(deserialized);
		}
	}

}
