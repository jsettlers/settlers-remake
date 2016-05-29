/*******************************************************************************
 * Copyright (c) 2015, 2016
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
import java.io.Serializable;

import jsettlers.common.ai.EPlayerType;
import jsettlers.logic.player.PlayerSetting;

/**
 * @author Andreas Eberle
 */
public class ReplayStartInformation implements Serializable {

	private long randomSeed;
	private String mapName;
	private String mapId;
	private int playerId;
	private PlayerSetting[] playerSettings;

	public ReplayStartInformation() {
	}

	public ReplayStartInformation(long randomSeed, String mapName, String mapId, int playerId, PlayerSetting[] playerSettings) {
		this.randomSeed = randomSeed;
		this.playerId = playerId;
		this.mapName = mapName;
		this.mapId = mapId;
		this.playerSettings = playerSettings;
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

	public PlayerSetting[] getPlayerSettings() {
		return playerSettings;
	}

	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeLong(randomSeed);
		dos.writeByte(playerId);
		dos.writeUTF(mapName);
		dos.writeUTF(mapId);

		dos.writeInt(playerSettings.length);
		for (PlayerSetting playerSetting : playerSettings) {
			playerSetting.writeTo(dos);
		}
	}

	public void deserialize(DataInputStream dis) throws IOException {
		randomSeed = dis.readLong();
		playerId = dis.readByte();
		mapName = dis.readUTF();
		mapId = dis.readUTF();

		playerSettings = new PlayerSetting[dis.readInt()];
		for (int i = 0; i < playerSettings.length; i++) {
			playerSettings[i] = PlayerSetting.readFromStream(dis);
		}
	}

	public PlayerSetting[] getReplayablePlayerSettings() {
		PlayerSetting[] playerSettings = new PlayerSetting[this.playerSettings.length];
		for (int i = 0; i < playerSettings.length; i++) {
			PlayerSetting originalSetting = this.playerSettings[i];
			playerSettings[i] = new PlayerSetting(originalSetting.isAvailable(), EPlayerType.HUMAN, originalSetting.getCivilisation(), originalSetting.getTeamId());
		}
		return playerSettings;
	}
}
