package networklib.server.actions.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.IDeserializingable;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PlayerInfoPacket extends Packet {
	public static IDeserializingable<PlayerInfoPacket> WITH_KEY_DESERIALIZER = new IDeserializingable<PlayerInfoPacket>() {
		@Override
		public PlayerInfoPacket deserialize(int key, DataInputStream dis) throws IOException {
			PlayerInfoPacket packet = new PlayerInfoPacket(key);
			packet.deserialize(dis);
			return packet;
		}
	};

	private String id;
	private String name;

	public PlayerInfoPacket() {
		this(NetworkConstants.Keys.PLAYER_INFO);
	}

	public PlayerInfoPacket(int key) {
		super(key);
	}

	public PlayerInfoPacket(String id, String name) {
		this(NetworkConstants.Keys.PLAYER_INFO, id, name);
	}

	public PlayerInfoPacket(int key, String id, String name) {
		this(key);
		this.id = id;
		this.name = name;
	}

	public PlayerInfoPacket(int key, PlayerInfoPacket playerInfo) {
		this(key, playerInfo.id, playerInfo.name);
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
		int result = super.hashCode();
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
