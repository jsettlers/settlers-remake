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
package jsettlers.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReplayStartInformation {

	private long randomSeed;
	private String mapName;
	private String mapId;
	private int playerId;
	private boolean[] availablePlayers;

	public ReplayStartInformation() {
	}

	public ReplayStartInformation(long randomSeed, String mapName, String mapId, int playerId, boolean[] availablePlayers) {
		this.randomSeed = randomSeed;
		this.playerId = playerId;
		this.mapName = mapName;
		this.mapId = mapId;
		this.availablePlayers = availablePlayers;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public int getPlayerId() {
		return playerId;
	}

	public String getMapName() {
		return mapName;
	}

	public String getMapId() {
		return mapId;
	}

	public boolean[] getAvailablePlayers() {
		return availablePlayers;
	}

	public void serialize(DataOutputStream oos) throws IOException {
		oos.writeLong(randomSeed);
		oos.writeByte(playerId);
		oos.writeUTF(mapName);
		oos.writeUTF(mapId);

		oos.writeByte(availablePlayers.length);
		for (boolean curr : availablePlayers) {
			oos.writeBoolean(curr);
		}
	}

	public void deserialize(DataInputStream ois) throws IOException {
		randomSeed = ois.readLong();
		playerId = ois.readByte();
		mapName = ois.readUTF();
		mapId = ois.readUTF();

		availablePlayers = new boolean[ois.readByte()];
		for (int i = 0; i < availablePlayers.length; i++) {
			availablePlayers[i] = ois.readBoolean();
		}
	}
}
