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
package jsettlers.network.client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.client.task.packets.TaskPacket;

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
