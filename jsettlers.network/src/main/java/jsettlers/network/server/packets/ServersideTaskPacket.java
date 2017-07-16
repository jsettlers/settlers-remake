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
package jsettlers.network.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This class extends the {@link Packet} class and does not really deserialize the data. It just stores the bytes to write them back on the stream
 * again.
 * 
 * @author Andreas Eberle
 * 
 */
public final class ServersideTaskPacket extends Packet {
	private byte[] data;

	public ServersideTaskPacket() {
	}

	public ServersideTaskPacket(byte[] data) {
		this.data = data;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(data.length);
		dos.write(data);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		final int length = dis.readInt();
		this.data = new byte[length];

		int alreadyRead = 0;
		while (length - alreadyRead > 0) {
			int numberOfBytesRead = dis.read(data, alreadyRead, length - alreadyRead);
			if (numberOfBytesRead < 0) {
				throw new IOException("Stream ended to early!");
			}

			alreadyRead += numberOfBytesRead;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
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
		ServersideTaskPacket other = (ServersideTaskPacket) obj;
		return Arrays.equals(data, other.data);
	}
}
