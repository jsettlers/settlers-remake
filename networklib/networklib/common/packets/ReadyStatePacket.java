package networklib.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReadyStatePacket extends Packet {

	private boolean ready;

	public ReadyStatePacket() {
	}

	public ReadyStatePacket(boolean ready) {
		this.ready = ready;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeBoolean(ready);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		ready = dis.readBoolean();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ready ? 1231 : 1237);
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
		ReadyStatePacket other = (ReadyStatePacket) obj;
		if (ready != other.ready)
			return false;
		return true;
	}

	public boolean isReady() {
		return ready;
	}

}
