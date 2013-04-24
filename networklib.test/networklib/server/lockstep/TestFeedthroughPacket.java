package networklib.server.lockstep;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.feedthrough.FeedthroughablePacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestFeedthroughPacket extends FeedthroughablePacket {
	private int testInt;
	private String testString;

	public TestFeedthroughPacket(int key) {
		super(key);
	}

	public TestFeedthroughPacket(int key, String testString, int testInt) {
		this(key);
		this.testInt = testInt;
		this.testString = testString;
	}

	@Override
	protected void serializeData(DataOutputStream dos) throws IOException {
		dos.writeInt(testInt);
		dos.writeUTF(testString);
	}

	@Override
	protected void deserializeData(int length, DataInputStream dis) throws IOException {
		testInt = dis.readInt();
		testString = dis.readUTF();
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
		TestFeedthroughPacket other = (TestFeedthroughPacket) obj;
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
