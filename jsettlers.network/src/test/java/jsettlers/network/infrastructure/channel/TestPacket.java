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
	public static final IDeserializingable<TestPacket> DEFAULT_DESERIALIZER = new GenericDeserializer<>(TestPacket.class);

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