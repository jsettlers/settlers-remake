package jsettlers.graphics.startscreen;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import jsettlers.common.CommonConstants;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.startscreen.interfaces.Player;

public class SettingsManager {
	private static final String FILE = ".jsettlers";
	public static final String SETTING_UUID = "gid";
	public static final String SETTING_USERNAME = "name";
	public static final String SETTING_SERVER = "server";
	public static final String SETTING_VOLUME = "volume";

	private static Reference<SettingsManager> manager;

	private Properties settings;

	private SettingsManager() {
	}

	private Properties getSettingsFile() {
		if (settings == null) {
			settings = new Properties();
			try {
				InputStream in = ResourceManager.getFile(FILE);
				settings.load(in);
			} catch (IOException e) {
			}
		}
		return settings;
	}

	public synchronized String get(String key) {
		String property = getSettingsFile().getProperty(key);
		return property == null ? getDefault(key) : property;
	}

	private String getDefault(String key) {
		if (SETTING_USERNAME.equals(key)) {
			return System.getProperty("user.name");
		} else if (SETTING_SERVER.equals(key)) {
			return CommonConstants.DEFAULT_SERVER_ADDRESS;
		} else if (SETTING_VOLUME.equals(key)) {
			return 0.7f + "";
		}
		return null;
	}

	public synchronized void set(String key, String value) {
		getSettingsFile().setProperty(key, value);
		try {
	        settings.store(ResourceManager.writeFile(FILE), new Date().toString());
        } catch (IOException e) {
        }
	}

	public static SettingsManager getInstance() {
		SettingsManager man = manager == null ? null : manager.get();
		if (man == null) {
			man = new SettingsManager();
			manager = new SoftReference<SettingsManager>(man);
		}
		return man;
	}

	public synchronized Player getPlayer() {
		String username = get(SETTING_USERNAME);
		String id = get(SETTING_UUID);
		if (id == null) {
			id = UUID.randomUUID().toString();
			set(SETTING_UUID, id);
		}
		return new Player(id, username);
	}

	public float getVolume() {
		String volumeString = get(SETTING_VOLUME);
		try {
			float volume = Float.parseFloat(volumeString);
			return Math.min(Math.max(volume, 0), 1);
		} catch (NumberFormatException e) {
		}
		return 1;
	}
}
