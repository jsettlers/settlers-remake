package networklib.server.actions.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 */
public class AcknowledgePacket extends Packet {
	private int acknowledgedKey;

	public AcknowledgePacket() {
		super(NetworkConstants.Keys.ACKNOWLEDGE_PACKET);
	}

	public AcknowledgePacket(int acknowledgedKey) {
		this();
		this.acknowledgedKey = acknowledgedKey;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(acknowledgedKey);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		acknowledgedKey = dis.readInt();
	}

	public int getAcknowledgedKey() {
		return acknowledgedKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + acknowledgedKey;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AcknowledgePacket other = (AcknowledgePacket) obj;
		if (acknowledgedKey != other.acknowledgedKey)
			return false;
		return true;
	}
}
