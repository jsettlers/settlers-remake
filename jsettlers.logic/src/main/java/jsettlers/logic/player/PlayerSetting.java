/*******************************************************************************
 * Copyright (c) 2016 - 2018
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
package jsettlers.logic.player;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * @author codingberlin
 * @author Andreas Eberle
 */
public class PlayerSetting {

	private static final short INITIAL_VERSION = 1;

	private final boolean available;
	private final Byte teamId;
	private final ECivilisation civilisation;
	private final EPlayerType playerType;

	/**
	 * Creates a new {@link PlayerSetting} object for a human player.
	 */
	public PlayerSetting(byte teamId) {
		this(true, EPlayerType.HUMAN, ECivilisation.ROMAN, teamId);
	}

	/**
	 * Creates a new PlayerSetting object for a not available player
	 */
	public PlayerSetting() {
		this(false, null, null, null);
	}

	/**
	 * Creates a new {@link PlayerSetting} object for a human player or an AI player.
	 *
	 * @param playerType
	 * 		{@link EPlayerType} defining the type of the AI player. If <code>null</code>, a human player is assumed.
	 */
	public PlayerSetting(EPlayerType playerType, ECivilisation civilisation, Byte teamId) {
		this(true, playerType, civilisation != null ? civilisation : getRandomCivilisation(), teamId);
	}

	public PlayerSetting(boolean available, EPlayerType playerType, ECivilisation civilisation, Byte teamId) {
		this.available = available;
		this.playerType = playerType;
		this.civilisation = civilisation;
		this.teamId = teamId;
	}

	private static ECivilisation getRandomCivilisation() {
		return ECivilisation.values()[new Random().nextInt(ECivilisation.values().length)];
	}

	public static PlayerSetting[] getUnspecifiedPlayerSettings(short maxPlayers) {
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];
		for (int i = 0; i < maxPlayers; i++) {
			playerSettings[i] = new PlayerSetting(true, null, null, null);
		}
		return playerSettings;
	}

	public boolean isAvailable() {
		return available;
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	public ECivilisation getCivilisation() {
		return civilisation;
	}

	@Override
	public String toString() {
		return "PlayerSetting{" +
				"available=" + available +
				", teamId=" + teamId +
				", civilisation=" + civilisation +
				", playerType=" + playerType +
				'}';
	}

	/**
	 * @return The id of the team this player is fixed to (e.g. if the map designer fixed the team id of this player or it is the team from a
	 * savegame) or <code>null</code> if the team is not fixed and the user can freely choose his team.
	 */
	public Byte getTeamId() {
		return teamId;
	}

	public static PlayerSetting readFromStream(DataInputStream dis) throws IOException {
		dis.readShort(); // read version
		boolean available = dis.readBoolean();
		if (available) {
			byte readTeamId = dis.readByte();
			Byte teamId = readTeamId == -1 ? null : readTeamId;

			String civilizationName = dis.readUTF();
			ECivilisation civilisation = civilizationName.isEmpty() ? null : ECivilisation.valueOf(civilizationName);

			String playerTypeName = dis.readUTF();
			EPlayerType playerType = playerTypeName.isEmpty() ? null : EPlayerType.valueOf(playerTypeName);

			return new PlayerSetting(true, playerType, civilisation, teamId);
		} else {
			return new PlayerSetting();
		}
	}

	public void writeTo(DataOutputStream dos) throws IOException {
		dos.writeShort(INITIAL_VERSION);

		dos.writeBoolean(available);
		if (available) {
			dos.writeByte(teamId == null ? -1 : teamId);
			dos.writeUTF(civilisation == null ? "" : civilisation.name());
			dos.writeUTF(playerType == null ? "" : playerType.name());
		}
	}

	public static PlayerSetting[] createDefaultSettings(byte playerId, byte maxPlayers) {
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];

		byte offsetToSkipHuman = 0;
		for (byte i = 0; i < playerSettings.length; i++) {
			if (i == playerId) {
				playerSettings[playerId] = new PlayerSetting(i);
			} else {
				EPlayerType aiType;

				aiType = EPlayerType.getTypeByIndex(i + offsetToSkipHuman);
				if (aiType == EPlayerType.HUMAN) {
					offsetToSkipHuman++;
					aiType = EPlayerType.getTypeByIndex(i + offsetToSkipHuman);
				}
				playerSettings[i] = new PlayerSetting(true, aiType, ECivilisation.ROMAN, i);
			}
		}
		System.out.println("created player settings: " + Arrays.toString(playerSettings));

		return playerSettings;
	}
}
