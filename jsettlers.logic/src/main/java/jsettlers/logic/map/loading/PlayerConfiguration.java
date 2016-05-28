package jsettlers.logic.map.loading;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Andreas Eberle on 28.05.2016.
 */
public class PlayerConfiguration {
	private static final short INITIAL_VERSION = 1;

	private short slotNumber;
	private Byte team;
	private ECivilisation civilisation;
	private EPlayerType playerType;

	public PlayerConfiguration(short slotNumber, Byte team, ECivilisation civilisation, EPlayerType playerType) {
		this.slotNumber = slotNumber;
		this.team = team;
		this.civilisation = civilisation;
		this.playerType = playerType;
	}

	public PlayerConfiguration(short slotNumber) {
		this(slotNumber, null, null, null);
	}

	public static PlayerConfiguration readFromStream(DataInputStream dis) throws IOException {
		dis.readShort(); // read version
		short slotNumber = dis.readShort();
		byte readTeam = dis.readByte();
		Byte team = readTeam == -1 ? null : readTeam;

		String civilizationName = dis.readUTF();
		ECivilisation civilisation = civilizationName.isEmpty() ? null : ECivilisation.valueOf(civilizationName);

		String playerTypeName = dis.readUTF();
		EPlayerType playerType = playerTypeName.isEmpty() ? null : EPlayerType.valueOf(playerTypeName);

		return new PlayerConfiguration(slotNumber, team, civilisation, playerType);
	}

	public void writeTo(DataOutputStream dos) throws IOException {
		dos.writeShort(INITIAL_VERSION);
		dos.writeShort(slotNumber);
		dos.writeByte(team == null ? -1 : team.byteValue());
		dos.writeUTF(civilisation == null ? "" : civilisation.name());
		dos.writeUTF(playerType == null ? "" : playerType.name());
	}

	public short getSlotNumber() {
		return slotNumber;
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
}
