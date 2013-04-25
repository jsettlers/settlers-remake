package networklib.server.actions.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.Packet;

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

	public MapInfoPacket() {
		super(NetworkConstants.Keys.MAP_INFO);
	}

	public MapInfoPacket(String id, String name, String authorId, String authorName) {
		this();
		this.id = id;
		this.name = name;
		this.authorId = authorId;
		this.authorName = authorName;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(name);
		dos.writeUTF(authorId);
		dos.writeUTF(authorName);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readUTF();
		name = dis.readUTF();
		authorId = dis.readUTF();
		authorName = dis.readUTF();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((authorId == null) ? 0 : authorId.hashCode());
		result = prime * result + ((authorName == null) ? 0 : authorName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
