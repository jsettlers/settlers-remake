package networklib.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants.ENetworkMessage;
import networklib.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoUpdatePacket extends Packet {

	private ENetworkMessage updateReason;
	private String idOfChangedPlayer;
	private MatchInfoPacket matchInfo;

	public MatchInfoUpdatePacket() {
	}

	public MatchInfoUpdatePacket(ENetworkMessage updateReason, String idOfChangedPlayer, MatchInfoPacket matchInfo) {
		this.updateReason = updateReason;
		this.idOfChangedPlayer = idOfChangedPlayer;
		this.matchInfo = matchInfo;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		updateReason.writeTo(dos);
		dos.writeUTF(idOfChangedPlayer);
		matchInfo.serialize(dos);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		updateReason = ENetworkMessage.readFrom(dis);
		idOfChangedPlayer = dis.readUTF();
		matchInfo = new MatchInfoPacket();
		matchInfo.deserialize(dis);
	}

	public ENetworkMessage getUpdateReason() {
		return updateReason;
	}

	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

	public String getIdOfChangedPlayer() {
		return idOfChangedPlayer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idOfChangedPlayer == null) ? 0 : idOfChangedPlayer.hashCode());
		result = prime * result + ((matchInfo == null) ? 0 : matchInfo.hashCode());
		result = prime * result + ((updateReason == null) ? 0 : updateReason.hashCode());
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
}
