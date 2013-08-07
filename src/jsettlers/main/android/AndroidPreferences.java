package jsettlers.main.android;

import java.util.UUID;

import android.content.SharedPreferences;

public class AndroidPreferences {

	private final SharedPreferences preferences;

	public AndroidPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public boolean hasMissingPreferences() {
		return !getPlayerName().isEmpty();
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
		return "localhost";
	}
}
