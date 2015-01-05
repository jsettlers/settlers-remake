package jsettlers.main.android;

import java.util.UUID;

import jsettlers.common.CommonConstants;
import android.content.SharedPreferences;

public class AndroidPreferences {

	private final SharedPreferences preferences;

	public AndroidPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public boolean hasMissingMultiplayerPreferences() {
		return getPlayerName().isEmpty() || getServer().isEmpty();
	}

	public String getPlayerName() {
		return preferences.getString("player-name", "");
	}

	public String getPlayerId() {
		String id = preferences.getString("player-id", "");
		if (id.isEmpty()) {
			id = UUID.randomUUID().toString();
			preferences.edit().putString("player-id", id).commit();
		}
		return id;
	}

	public String getServer() {
		return preferences.getString("server", CommonConstants.DEFAULT_SERVER_ADDRESS);
	}

	public void setPlayerName(String name) {
		preferences.edit().putString("player-name", name).commit();
	}

	public void setServer(String serverName) {
		preferences.edit().putString("server", serverName).commit();
	}
}
