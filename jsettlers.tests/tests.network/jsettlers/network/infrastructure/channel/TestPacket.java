package jsettlers.network.infrastructure.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestPacket extends Packet {
	public static final IDeserializingable<TestPacket> DEFAULT_DESERIALIZER = new GenericDeserializer<TestPacket>(TestPacket.class);

	private int testInt;
	private String testString;

	public TestPacket() {
	}

	public TestPacket(int testInt) {
		this("TestMessage", testInt);
	}

	public TestPacket(String testString, int testInt) {
		this.testString = testString;
		this.testInt = testInt;
	}

	@Override
	public void serialize(DataOutputStream oos) throws IOException {
		oos.writeInt(testInt);
		oos.writeUTF(testString);
	}

	@Override
	public void deserialize(DataInputStream ois) throws IOException {
		testInt = ois.readInt();
		testString = ois.readUTF();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + testInt;
		result = prime * result + ((testString == null) ? 0 : testString.hashCode());
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
		TestPacket other = (TestPacket) obj;
		if (testInt != other.testInt)
			return false;
		if (testString == null) {
			if (other.testString != null)
				return false;
		} else if (!testString.equals(other.testString))
			return false;
		return true;
	}

	public int getTestInt() {
		return testInt;
	}

	public String getTestString() {
		return testString;
	}
}