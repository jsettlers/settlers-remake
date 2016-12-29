package jsettlers.main.android;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.integration.replay.AutoReplaySetting;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.TestUtils;

/**
 * Created by Andreas Eberle on 29.12.2016.
 */
public class AndroidAutoReplayIT {

	@BeforeClass
	public static void setupConstants() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;

		TestUtils.setupTempResourceManager();
	}

	@Test
	public void testIfReplayWorks() throws MapLoadException, IOException {
		AutoReplaySetting setting = AutoReplaySetting.getDefaultSettings().iterator().next();
		MapLoader[] savegames = ReplayUtils.replayAndCreateSavegames(setting.getReplayFile(), setting.getTimeMinutes());
	}
}
