/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
 */
package jsettlers.main.swing.settings;

import go.graphics.swing.contextcreator.EBackendType;
import go.graphics.swing.sound.ISoundSettingsProvider;
import java8.util.Maps;
import java8.util.Optional;
import java8.util.function.Supplier;
import jsettlers.common.CommonConstants;
import jsettlers.common.resources.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import go.graphics.swing.contextcreator.BackendSelector;

public class SettingsManager implements ISoundSettingsProvider {
	private static final String CONFIGURATION_FILE = ".jsettlers";

	private static final String ENV_PREFIX = "SETTLERS_";
	private static final String PROPERTIES_PREFIX = "settlers.";

	public static final String SETTING_UUID = "gid";

	public static final String SETTING_BACKEND = "backend";
	public static final String SETTING_FPS_LIMIT = "fpsLimit";
  
	private static final String SETTING_SETTLERS_FOLDER = "settlers-folder";
	private static final String SETTING_SETTLERS_VERSION_ID = "settlers-folder-version-id";

	private static final String SETTING_USERNAME = "name";
	private static final String SETTING_LOCALE = "locale";
	private static final String SETTING_SERVER = "server";
	private static final String SETTING_VOLUME = "volume";
	private static final String SETTING_FULL_SCREEN_MODE = "fullScreenMode";

	private static final String SETTING_CONTROL_ALL = "control-all";
	private static final String SETTING_ACTIVATE_ALL_PLAYERS = "activate-all-players";
	private static final String SETTING_ENABLE_CONSOLE_LOGGING = "console-output";
	private static final String SETTING_DISABLE_ORIGINAL_MAPS = "disable-original-maps";
	private static final String SETTING_MAPFILE = "map-file";
	private static final String SETTING_RANDOM = "random";
	private static final String SETTING_REPLAY_FILE = "replay-file";
	private static final String SETTING_TARGET_TIME = "target-time";
	private static final String SETTING_MAPS = "maps";

	private static SettingsManager manager;

	private final Properties storedSettings = new Properties();
	private final Map<String, String> runtimeProperties = new HashMap<>();

	public static void setup(String... args) throws IOException {
		manager = new SettingsManager(args);
	}

	public static SettingsManager getInstance() {
		return manager;
	}

	private SettingsManager(String[] args) throws IOException {
		storedSettings.load(ResourceManager.getResourcesFileStream(CONFIGURATION_FILE));
		loadRuntimeProperties(args);
	}

	private void loadRuntimeProperties(String[] args) {
		loadFromEnvironment();
		loadArguments(args);
	}

	private void loadFromEnvironment() {
		for (Map.Entry<String, String> e : System.getenv().entrySet()) {
			loadEntry(e, ENV_PREFIX);
		}

		for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
			loadEntry(e, PROPERTIES_PREFIX);
		}
	}

	private void loadEntry(Map.Entry<?, ?> e, String envPrefix) {
		if (e.getKey().toString().startsWith(envPrefix)) {
			String key = e.getKey().toString().substring(envPrefix.length()).toLowerCase(Locale.ENGLISH);
			runtimeProperties.put(key, e.getValue().toString());
			System.out.println("Argument: " + key + " -> " + e.getValue().toString());
		}
	}
	
	private void loadArguments(String[] args) {
		Pattern parameterPattern = Pattern.compile("--(.*?)=(.*?)");
		Pattern optionPattern = Pattern.compile("--(.*?)");

		for (String arg : args) {
			Matcher parameterMatcher = parameterPattern.matcher(arg);
			if (parameterMatcher.matches()) {
				String parameter = parameterMatcher.group(1);
				String value = parameterMatcher.group(2);
				runtimeProperties.put(parameter, value);
			} else {
				Matcher optionMatcher = optionPattern.matcher(arg);
				if (optionMatcher.matches()) {
					String option = optionMatcher.group(1);
					runtimeProperties.put(option, "true");
				}
			}
		}
	}

	private String get(String key) {
		return Maps.computeIfAbsent(runtimeProperties, key, storedSettings::getProperty);
	}

	private boolean getOptional(String key) {
		return get(key) != null;
	}

	private Optional<String> getAsOptional(String key) {
		return Optional.ofNullable(get(key));
	}

	private String getOrDefault(String key, Supplier<String> defaultProvider) {
		String value = get(key);
		return value != null ? value : defaultProvider.get();
	}

	private synchronized void set(String key, String value) {
		storedSettings.setProperty(key, value);
		runtimeProperties.put(key, value);
		try {
			storedSettings.store(ResourceManager.writeConfigurationFile(CONFIGURATION_FILE), new Date().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getServer() {
		return getOrDefault(SETTING_SERVER, () -> CommonConstants.DEFAULT_SERVER_ADDRESS);
	}

	public String getUserName() {
		return getOrDefault(SETTING_USERNAME, () -> System.getProperty("user.name"));
	}

	public synchronized String getUUID() {
		String id = get(SETTING_UUID);
		if (id == null) {
			id = UUID.randomUUID().toString();
			set(SETTING_UUID, id);
		}
		return id;
	}

	public UiPlayer getPlayer() {
		return new UiPlayer(getUUID(), getUserName());
	}

	public float getVolume() {
		String volumeString = get(SETTING_VOLUME);
		try {
			float volume = volumeString != null ? Float.parseFloat(volumeString) : 0.7f;
			return Math.min(Math.max(volume, 0), 1);
		} catch (NumberFormatException e) {
		}
		return 1;
	}

	public int getFpsLimit() {
		String fpsLimitString = get(SETTING_FPS_LIMIT);
		try {
			int fps_limit = fpsLimitString != null ? Integer.parseInt(fpsLimitString) : 60;
			return Math.max(Math.min(fps_limit, 240), 1);
		} catch (NumberFormatException e) {
		}
		return 1;
	}

	public EBackendType getBackend() {
		return BackendSelector.getBackendByName(getOrDefault(SETTING_BACKEND, () -> EBackendType.DEFAULT.cc_name));
  }
	public void setVolume(float volume) {
		set(SETTING_VOLUME, Float.toString(volume));
	}

	public void setFpsLimit(int fpsLimit) {set(SETTING_FPS_LIMIT,Integer.toString(fpsLimit));}

	public void setBackend(String backend) {set(SETTING_BACKEND, backend);}

	public void setFullScreenMode(boolean fullScreenMode) {
		set(SETTING_FULL_SCREEN_MODE, "" + fullScreenMode);
	}

	public boolean getFullScreenMode() {
		return Boolean.valueOf(get(SETTING_FULL_SCREEN_MODE));
	}

	public String getSettlersFolder() {
		return get(SETTING_SETTLERS_FOLDER);
	}

	public void setSettlersFolder(File settlersFolder) {
		set(SETTING_SETTLERS_FOLDER, settlersFolder.getAbsolutePath());
	}

	public String getSettlersVersionId() {
		return get(SETTING_SETTLERS_VERSION_ID);
	}

	public void setSettlersVersionId(String settlersVersionId) {
		set(SETTING_SETTLERS_VERSION_ID, settlersVersionId);
	}

	public boolean isControllAll() {
		return getOptional(SETTING_CONTROL_ALL);
	}

	public boolean isActivateAllPlayers() {
		return getOptional(SETTING_ACTIVATE_ALL_PLAYERS);
	}

	public boolean useConsoleOutput() {
		return getOptional(SETTING_ENABLE_CONSOLE_LOGGING);
	}

	public boolean areOriginalMapsDisabled() {
		return getOptional(SETTING_DISABLE_ORIGINAL_MAPS);
	}

	public Locale getLocale() {
		return Optional.ofNullable(get(SETTING_LOCALE)).map(localeString -> {
			String[] localeParts = localeString.split("_");
			if (localeParts.length == 2) {
				return new Locale(localeParts[0], localeParts[1]);
			} else {
				System.err.println("Please specify the locale with language and country. (For example: de_de or en_us). Using default locale.");
				return Locale.getDefault();
			}
		}).orElse(Locale.getDefault());
	}

	public String getMapFile() {
		return get(SETTING_MAPFILE);
	}

	public Optional<Long> getRandom() {
		return getAsOptional(SETTING_RANDOM).map(Long::valueOf);
	}

	public Optional<String> getReplayFile() {
		return getAsOptional(SETTING_REPLAY_FILE);
	}

	public Optional<Integer> getTargetTime() {
		return getAsOptional(SETTING_TARGET_TIME).map(Integer::valueOf).map(targetTime -> targetTime * 60 * 1000);
	}

	public String getAdditionalMapsDirectory() {
		return get(SETTING_MAPS);
	}

	public void setUserName(String userName) {
		set(SETTING_USERNAME, userName);
	}
}
