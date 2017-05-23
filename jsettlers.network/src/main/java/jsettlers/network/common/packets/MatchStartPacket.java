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

import jsettlers.network.infrastructure.channel.packet.Packet;

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
		return randomSeed == other.randomSeed;
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
