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
package jsettlers.network.infrastructure.channel.ping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This packet is used to determine the ping of a channel.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacket extends Packet {
	private long senderTime;
	private long receiverTime;

	public PingPacket() {
	}

	public PingPacket(long senderTime, long receiverTime) {
		this.senderTime = senderTime;
		this.receiverTime = receiverTime;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeLong(senderTime);
		dos.writeLong(receiverTime);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		senderTime = dis.readLong();
		receiverTime = dis.readLong();
	}

	public long getSenderTime() {
		return senderTime;
	}

	public long getReceiverTime() {
		return receiverTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (receiverTime ^ (receiverTime >>> 32));
		result = prime * result + (int) (senderTime ^ (senderTime >>> 32));
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
		PingPacket other = (PingPacket) obj;
		if (receiverTime != other.receiverTime)
			return false;
		return senderTime == other.senderTime;
	}
}
