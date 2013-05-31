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
public class MapInfoPacket extends Packet {

	private String id;
	private String name;
	private String authorId;
	private String authorName;
	private int maxPlayers;

	public MapInfoPacket() {
	}

	public MapInfoPacket(String id, String name, String authorId, String authorName, int maxPlayers) {
		this.id = id;
		this.name = name;
		this.authorId = authorId;
		this.authorName = authorName;
		this.maxPlayers = maxPlayers;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(name);
		dos.writeUTF(authorId);
		dos.writeUTF(authorName);
		dos.writeInt(maxPlayers);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readUTF();
		name = dis.readUTF();
		authorId = dis.readUTF();
		authorName = dis.readUTF();
		maxPlayers = dis.readInt();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorId == null) ? 0 : authorId.hashCode());
		result = prime * result + ((authorName == null) ? 0 : authorName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + maxPlayers;
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
		MapInfoPacket other = (MapInfoPacket) obj;
		if (authorId == null) {
			if (other.authorId != null)
				return false;
		} else if (!authorId.equals(other.authorId))
			return false;
		if (authorName == null) {
			if (other.authorName != null)
				return false;
		} else if (!authorName.equals(other.authorName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxPlayers != other.maxPlayers)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
