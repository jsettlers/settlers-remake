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
