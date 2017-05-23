/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.server.match.Match;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoPacket extends Packet {
	private String id;
	private String matchName;
	private int maxPlayers;
	private MapInfoPacket mapInfo;
	private PlayerInfoPacket[] players;

	public MatchInfoPacket() {
	}

	public MatchInfoPacket(String id, String matchName, int maxPlayers, MapInfoPacket mapInfo, PlayerInfoPacket[] players) {
		this.id = id;
		this.matchName = matchName;
		this.maxPlayers = maxPlayers;
		this.mapInfo = mapInfo;
		this.players = players;
	}

	public MatchInfoPacket(Match match) {
		this();
		id = match.getId();
		matchName = match.getName();
		maxPlayers = match.getMaxPlayers();
		mapInfo = match.getMap();
		players = match.getPlayerInfos();
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(id);
		dos.writeUTF(matchName);
		dos.writeInt(maxPlayers);
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
		maxPlayers = dis.readInt();
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

	public int getMaxPlayers() {
		return maxPlayers;
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
		return Arrays.equals(players, other.players);
	}

	@Override
	public String toString() {
		return "MatchInfoPacket [id=" + id + ", matchName=" + matchName + ", maxPlayers=" + maxPlayers + ", mapInfo=" + mapInfo + ", players="
				+ Arrays.toString(players) + "]";
	}
}
