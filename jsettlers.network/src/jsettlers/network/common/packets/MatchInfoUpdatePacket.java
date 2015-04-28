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

import jsettlers.network.NetworkConstants.ENetworkMessage;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoUpdatePacket extends Packet {

	private ENetworkMessage updateReason;
	private PlayerInfoPacket updatedPlayer;
	private MatchInfoPacket matchInfo;

	public MatchInfoUpdatePacket() {
	}

	public MatchInfoUpdatePacket(ENetworkMessage updateReason, PlayerInfoPacket updatedPlayer, MatchInfoPacket matchInfo) {
		this.updateReason = updateReason;
		this.updatedPlayer = updatedPlayer;
		this.matchInfo = matchInfo;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		updateReason.writeTo(dos);
		updatedPlayer.serialize(dos);
		matchInfo.serialize(dos);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		updateReason = ENetworkMessage.readFrom(dis);
		updatedPlayer = new PlayerInfoPacket();
		updatedPlayer.deserialize(dis);
		matchInfo = new MatchInfoPacket();
		matchInfo.deserialize(dis);
	}

	public ENetworkMessage getUpdateReason() {
		return updateReason;
	}

	public MatchInfoPacket getMatchInfo() {
		return matchInfo;
	}

	public PlayerInfoPacket getUpdatedPlayer() {
		return updatedPlayer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((updatedPlayer == null) ? 0 : updatedPlayer.hashCode());
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
		if (updatedPlayer == null) {
			if (other.updatedPlayer != null)
				return false;
		} else if (!updatedPlayer.equals(other.updatedPlayer))
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
