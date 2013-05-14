package networklib.client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestTaskPacket extends TaskPacket {

	private String testString;
	private int testInt;
	private byte testByte;

	public TestTaskPacket() {
	}

	public TestTaskPacket(String testString, int testInt, byte testByte) {
		this.testString = testString;
		this.testInt = testInt;
		this.testByte = testByte;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		dos.writeUTF(testString);
		dos.writeInt(testInt);
		dos.writeByte(testByte);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		testString = dis.readUTF();
		testInt = dis.readInt();
		testByte = dis.readByte();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + testByte;
		result = prime * result + testInt;
		result = prime * result + ((testString == null) ? 0 : testString.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestTaskPacket other = (TestTaskPacket) obj;
		if (testByte != other.testByte)
			return false;
		if (testInt != other.testInt)
			return false;
		if (testString == null) {
			if (other.testString != null)
				return false;
		} else if (!testString.equals(other.testString))
			return false;
		return true;
	}

}
