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
package jsettlers.network.infrastructure.channel.reject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.NetworkConstants.ENetworkMessage;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This packet is used to notify a communication partner, that there was a problem fulfilling one of it's requests.
 * 
 * @author Andreas Eberle
 * 
 */
public class RejectPacket extends Packet {
	private ENetworkMessage errorMessage;
	private ENetworkKey rejectedKey;

	public RejectPacket() {
	}

	public RejectPacket(ENetworkMessage errorMessageId, ENetworkKey rejectedKey) {
		this.errorMessage = errorMessageId;
		this.rejectedKey = rejectedKey;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		errorMessage.writeTo(dos);
		rejectedKey.writeTo(dos);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		errorMessage = ENetworkMessage.readFrom(dis);
		rejectedKey = ENetworkKey.readFrom(dis);
	}

	public ENetworkMessage getErrorMessageId() {
		return errorMessage;
	}

	public ENetworkKey getRejectedKey() {
		return rejectedKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((rejectedKey == null) ? 0 : rejectedKey.hashCode());
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
		RejectPacket other = (RejectPacket) obj;
		if (errorMessage != other.errorMessage)
			return false;
		return rejectedKey == other.rejectedKey;
	}
}
