/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.logic.map.loading;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;

/**
 * Created by Andreas Eberle on 28.05.2016.
 */
public class PlayerConfiguration {
	private static final short INITIAL_VERSION = 1;

	private final boolean available;
	private final Byte team;
	private final ECivilisation civilisation;
	private final EPlayerType playerType;

	private PlayerConfiguration(boolean available, Byte team, ECivilisation civilisation, EPlayerType playerType) {
		this.available = available;
		this.team = team;
		this.civilisation = civilisation;
		this.playerType = playerType;
	}

	public PlayerConfiguration(Byte team, ECivilisation civilisation, EPlayerType playerType) {
		this(true, team, civilisation, playerType);
	}

	public PlayerConfiguration(boolean available) {
		this(available, null, null, null);
	}

	public static PlayerConfiguration readFromStream(DataInputStream dis) throws IOException {
		dis.readShort(); // read version
		boolean available = dis.readBoolean();
		if (available) {
			byte readTeam = dis.readByte();
			Byte team = readTeam == -1 ? null : readTeam;

			String civilizationName = dis.readUTF();
			ECivilisation civilisation = civilizationName.isEmpty() ? null : ECivilisation.valueOf(civilizationName);

			String playerTypeName = dis.readUTF();
			EPlayerType playerType = playerTypeName.isEmpty() ? null : EPlayerType.valueOf(playerTypeName);

			return new PlayerConfiguration(team, civilisation, playerType);
		} else {
			return new PlayerConfiguration(false);
		}
	}

	public void writeTo(DataOutputStream dos) throws IOException {
		dos.writeShort(INITIAL_VERSION);

		dos.writeBoolean(available);
		if (available) {
			dos.writeByte(team == null ? -1 : team.byteValue());
			dos.writeUTF(civilisation == null ? "" : civilisation.name());
			dos.writeUTF(playerType == null ? "" : playerType.name());
		}
	}

	public Byte getTeam() {
		return team;
	}

	public ECivilisation getCivilisation() {
		return civilisation;
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	public boolean isAvailable() {
		return available;
	}
}
