package networklib.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.infrastructure.channel.packet.Packet;

/**
 * This packet contains the data needed to start a network game.
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchStartPacket extends Packet {

	private MatchInfoPacket matchInfo;
	private long randomSeed;

	public MatchStartPacket() {
	}

	public MatchStartPacket(MatchInfoPacket matchInfo, long seed) {
		this.matchInfo = matchInfo;
		this.randomSeed = seed;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		matchInfo.serialize(dos);
		dos.writeLong(randomSeed);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		MatchInfoPacket match = new MatchInfoPacket();
		match.deserialize(dis);
		this.matchInfo = match;
		randomSeed = dis.readLong();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matchInfo == null) ? 0 : matchInfo.hashCode());
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
		MatchStartPacket other = (MatchStartPacket) obj;
		if (matchInfo == null) {
			if (other.matchInfo != null)
				return false;
		} else if (!matchInfo.equals(other.matchInfo))
			return false;
		if (randomSeed != other.randomSeed)
			return false;
		return true;
	}

	/**
	 * @return the match
	 */
	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

	/**
	 * @return the seed
	 */
	public long getRandomSeed() {
		return randomSeed;
	}
}
