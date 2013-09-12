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
	private int playerNumber;
	private String mapName;
	private String mapId;

	public ReplayStartInformation() {
	}

	public ReplayStartInformation(long randomSeed, int playerNumber, String mapName, String mapId) {
		this.randomSeed = randomSeed;
		this.playerNumber = playerNumber;
		this.mapName = mapName;
		this.mapId = mapId;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public String getMapName() {
		return mapName;
	}

	public String getMapId() {
		return mapId;
	}

	public void serialize(DataOutputStream replayFileStream) throws IOException {
		replayFileStream.writeLong(randomSeed);
		replayFileStream.writeByte(playerNumber);
		replayFileStream.writeUTF(mapName);
		replayFileStream.writeUTF(mapId);
	}

	public void deserialize(DataInputStream dis) throws IOException {
		randomSeed = dis.readLong();
		playerNumber = dis.readByte();
		mapName = dis.readUTF();
		mapId = dis.readUTF();
	}
}
