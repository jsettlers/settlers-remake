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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ArrayOfMatchInfosPacket extends Packet {

	private MatchInfoPacket[] matches;

	public ArrayOfMatchInfosPacket() {
	}

	public ArrayOfMatchInfosPacket(MatchInfoPacket[] matches) {
		this.matches = matches;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(matches.length);
		for (int i = 0; i < matches.length; i++) {
			matches[i].serialize(dos);
		}
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		matches = new MatchInfoPacket[length];

		for (int i = 0; i < length; i++) {
			matches[i] = new MatchInfoPacket();
			matches[i].deserialize(dis);
		}
	}

	public MatchInfoPacket[] getMatches() {
		return matches;
	}

	public void setMatches(MatchInfoPacket[] matches) {
		this.matches = matches;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(matches);
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
		ArrayOfMatchInfosPacket other = (ArrayOfMatchInfosPacket) obj;
		return Arrays.equals(matches, other.matches);
	}
}
