package networklib.server.actions.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import networklib.channel.NetworkConstants;
import networklib.channel.Packet;
import networklib.server.game.Match;

public class MatchInfoPacket extends Packet {
	private String id;
	private String matchName;
	private MapInfoPacket mapInfo;
	private PlayerInfoPacket[] players;

	public MatchInfoPacket() {
		super(NetworkConstants.Keys.MATCH_INFO);
	}

	public MatchInfoPacket(String id, String matchName, MapInfoPacket mapInfo, PlayerInfoPacket[] players) {
		this();
		this.id = id;
		this.matchName = matchName;
		this.mapInfo = mapInfo;
		this.players = players;
	}

	public MatchInfoPacket(Match match) {
		this();
		id = match.getId();
		matchName = match.getName();
		mapInfo = match.getMap();
		players = match.getPlayerInfos();
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(matchName);
		mapInfo.serialize(dos);

		PlayerInfoPacket[] players = this.players;
		dos.writeInt(players.length);
		for (PlayerInfoPacket curr : players) {
			curr.serialize(dos);
		}
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readUTF();
		matchName = dis.readUTF();
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
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mapInfo == null) ? 0 : mapInfo.hashCode());
		result = prime * result + ((matchName == null) ? 0 : matchName.hashCode());
		result = prime * result + Arrays.hashCode(players);
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
		if (!Arrays.equals(players, other.players))
			return false;
		return true;
	}

}
