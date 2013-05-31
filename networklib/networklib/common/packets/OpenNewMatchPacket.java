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
public class OpenNewMatchPacket extends Packet {
	private String matchName;
	private int maxPlayers;
	private MapInfoPacket mapInfo;
	private long randomSeed;

	public OpenNewMatchPacket() {
	}

	public OpenNewMatchPacket(String matchName, int maxPlayers, MapInfoPacket mapInfo, long randomSeed) {
		this.matchName = matchName;
		this.maxPlayers = maxPlayers;
		this.mapInfo = mapInfo;
		this.randomSeed = randomSeed;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(matchName);
		dos.writeInt(maxPlayers);
		mapInfo.serialize(dos);
		dos.writeLong(randomSeed);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		matchName = dis.readUTF();
		maxPlayers = dis.readInt();
		mapInfo = new MapInfoPacket();
		mapInfo.deserialize(dis);
		randomSeed = dis.readLong();
	}

	public String getMatchName() {
		return matchName;
	}

	public MapInfoPacket getMapInfo() {
		return mapInfo;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapInfo == null) ? 0 : mapInfo.hashCode());
		result = prime * result + ((matchName == null) ? 0 : matchName.hashCode());
		result = prime * result + maxPlayers;
		result = prime * result + (int) (randomSeed ^ (randomSeed >>> 32));
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
		if (randomSeed != other.randomSeed)
			return false;
		return true;
	}
}
