package networklib.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class OpenNewMatchPacket extends Packet {
	private String matchName;
	private byte maxPlayers;
	private MapInfoPacket mapInfo;

	public OpenNewMatchPacket() {
	}

	public OpenNewMatchPacket(String matchName, byte maxPlayers, MapInfoPacket mapInfo) {
		this.matchName = matchName;
		this.maxPlayers = maxPlayers;
		this.mapInfo = mapInfo;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(matchName);
		dos.writeByte(maxPlayers);
		mapInfo.serialize(dos);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		matchName = dis.readUTF();
		maxPlayers = dis.readByte();
		mapInfo = new MapInfoPacket();
		mapInfo.deserialize(dis);
	}

	public String getMatchName() {
		return matchName;
	}

	public MapInfoPacket getMapInfo() {
		return mapInfo;
	}

	public byte getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapInfo == null) ? 0 : mapInfo.hashCode());
		result = prime * result + ((matchName == null) ? 0 : matchName.hashCode());
		result = prime * result + maxPlayers;
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
		OpenNewMatchPacket other = (OpenNewMatchPacket) obj;
		if (mapInfo == null) {
			if (other.mapInfo != null)
				return false;
		} else if (!mapInfo.equals(other.mapInfo))
			return false;
		if (matchName == null) {
			if (other.matchName != null)
				return false;
		} else if (!matchName.equals(other.matchName))
			return false;
		if (maxPlayers != other.maxPlayers)
			return false;
		return true;
	}
}
