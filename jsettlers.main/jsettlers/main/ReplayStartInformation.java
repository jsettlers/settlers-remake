package jsettlers.main;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReplayStartInformation {

	private final long randomSeed;
	private final byte playerNumber;
	private final String mapName;
	private final String mapId;

	public ReplayStartInformation(long randomSeed, byte playerNumber, String mapName, String mapId) {
		this.randomSeed = randomSeed;
		this.playerNumber = playerNumber;
		this.mapName = mapName;
		this.mapId = mapId;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public byte getPlayerNumber() {
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
}
