package networklib.channel.reject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RejectPacket extends Packet {
	private int errorMessageId;
	private int rejectedKey;

	public RejectPacket() {
	}

	public RejectPacket(int errorMessageId, int rejectedKey) {
		this.errorMessageId = errorMessageId;
		this.rejectedKey = rejectedKey;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(errorMessageId);
		dos.writeInt(rejectedKey);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		errorMessageId = dis.readInt();
		rejectedKey = dis.readInt();
	}

	public int getErrorMessageId() {
		return errorMessageId;
	}

	public int getRejectedKey() {
		return rejectedKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + errorMessageId;
		result = prime * result + rejectedKey;
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
		if (errorMessageId != other.errorMessageId)
			return false;
		if (rejectedKey != other.rejectedKey)
			return false;
		return true;
	}
}
