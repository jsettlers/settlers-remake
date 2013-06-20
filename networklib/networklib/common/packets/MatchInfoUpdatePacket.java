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
public class MatchInfoUpdatePacket extends Packet {

	private int updateReason;
	private String idOfChangedPlayer;
	private MatchInfoPacket matchInfo;

	public MatchInfoUpdatePacket() {
	}

	public MatchInfoUpdatePacket(int updateReason, String idOfChangedPlayer, MatchInfoPacket matchInfo) {
		this.updateReason = updateReason;
		this.idOfChangedPlayer = idOfChangedPlayer;
		this.matchInfo = matchInfo;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(updateReason);
		dos.writeUTF(idOfChangedPlayer);
		matchInfo.serialize(dos);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		updateReason = dis.readInt();
		idOfChangedPlayer = dis.readUTF();
		matchInfo = new MatchInfoPacket();
		matchInfo.deserialize(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idOfChangedPlayer == null) ? 0 : idOfChangedPlayer.hashCode());
		result = prime * result + ((matchInfo == null) ? 0 : matchInfo.hashCode());
		result = prime * result + updateReason;
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
		MatchInfoUpdatePacket other = (MatchInfoUpdatePacket) obj;
		if (idOfChangedPlayer == null) {
			if (other.idOfChangedPlayer != null)
				return false;
		} else if (!idOfChangedPlayer.equals(other.idOfChangedPlayer))
			return false;
		if (matchInfo == null) {
			if (other.matchInfo != null)
				return false;
		} else if (!matchInfo.equals(other.matchInfo))
			return false;
		if (updateReason != other.updateReason)
			return false;
		return true;
	}

	public int getUpdateReason() {
		return updateReason;
	}

	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

	public String getIdOfChangedPlayer() {
		return idOfChangedPlayer;
	}
}
