package networklib.infrastructure.channel.reject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.NetworkConstants.ENetworkMessage;
import networklib.infrastructure.channel.packet.Packet;

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
		if (rejectedKey != other.rejectedKey)
			return false;
		return true;
	}
}
