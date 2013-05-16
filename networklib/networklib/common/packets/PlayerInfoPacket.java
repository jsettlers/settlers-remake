package networklib.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PlayerInfoPacket extends Packet {
	private String id;
	private String name;

	public PlayerInfoPacket() {
	}

	public PlayerInfoPacket(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(name);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readUTF();
		name = dis.readUTF();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PlayerInfoPacket other = (PlayerInfoPacket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
