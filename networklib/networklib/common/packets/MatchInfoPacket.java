package networklib.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import networklib.infrastructure.channel.packet.Packet;
import networklib.server.game.Match;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoPacket extends Packet {
	private String id;
	private String matchName;
	private byte maxPlayers;
	private MapInfoPacket mapInfo;
	private PlayerInfoPacket[] players;
	private long randomSeed;

	public MatchInfoPacket() {
	}

	public MatchInfoPacket(String id, String matchName, byte maxPlayers, MapInfoPacket mapInfo, PlayerInfoPacket[] players, long randomSeed) {
		this.id = id;
		this.matchName = matchName;
		this.maxPlayers = maxPlayers;
		this.mapInfo = mapInfo;
		this.players = players;
		this.randomSeed = randomSeed;
	}

	public MatchInfoPacket(Match match) {
		this();
		id = match.getId();
		matchName = match.getName();
		maxPlayers = match.getMaxPlayers();
		mapInfo = match.getMap();
		players = match.getPlayerInfos();
		randomSeed = match.getRandomSeed();
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(matchName);
		dos.writeByte(maxPlayers);
		mapInfo.serialize(dos);

		PlayerInfoPacket[] players = this.players;
		dos.writeInt(players.length);
		for (PlayerInfoPacket curr : players) {
			curr.serialize(dos);
		}

		dos.writeLong(randomSeed);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readUTF();
		matchName = dis.readUTF();
		maxPlayers = dis.readByte();
		mapInfo = new MapInfoPacket();
		mapInfo.deserialize(dis);

		int length = dis.readInt();
		PlayerInfoPacket[] players = new PlayerInfoPacket[length];
		for (int i = 0; i < length; i++) {
			PlayerInfoPacket curr = new PlayerInfoPacket();
			curr.deserialize(dis);
			players[i] = curr;
		}
		this.players = players;

		randomSeed = dis.readLong();
	}

	public String getId() {
		return id;
	}

	public String getMatchName() {
		return matchName;
	}

	public MapInfoPacket getMapInfo() {
		return mapInfo;
	}

	public PlayerInfoPacket[] getPlayers() {
		return players;
	}

	public byte getMaxPlayers() {
		return maxPlayers;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mapInfo == null) ? 0 : mapInfo.hashCode());
		result = prime * result + ((matchName == null) ? 0 : matchName.hashCode());
		result = prime * result + maxPlayers;
		result = prime * result + Arrays.hashCode(players);
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
		MatchInfoPacket other = (MatchInfoPacket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
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
		if (!Arrays.equals(players, other.players))
			return false;
		if (randomSeed != other.randomSeed)
			return false;
		return true;
	}

}
