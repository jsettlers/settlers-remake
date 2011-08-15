package jsettlers.logic.map.random.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MapSettings {
	private final List<PlayerSetting> players;

	public MapSettings(int players) {
		ArrayList<PlayerSetting> playerCreation = new ArrayList<PlayerSetting>(players);
		
		for (int i = 0; i < players; i++) {
			playerCreation.add(new PlayerSetting((byte) i, (byte) i));
		}
		
		this.players = Collections.unmodifiableList(playerCreation);
	}

	public List<PlayerSetting> getPlayers() {
	    return players;
    }
}
