/*******************************************************************************
 * Copyright (c) 2016
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.integration.replay;

import java.io.IOException;
import java.util.Collection;
import java8.util.stream.Collectors;

import jsettlers.common.CommonConstants;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.TestUtils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java8.util.stream.StreamSupport.stream;

@RunWith(Parameterized.class)
public class AutoReplayIT {

	@BeforeClass
	public static void setupConstants() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;

		TestUtils.setupTempResourceManager();
	}

	private static final Object ONLY_ONE_TEST_AT_A_TIME_LOCK = new Object();

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> replaySets() {
		return stream( AutoReplaySetting.getDefaultSettings()).map(s -> new Object[] { s }).collect(Collectors.toList());
	}

	private final AutoReplaySetting setting;

	public AutoReplayIT(AutoReplaySetting setting) {
		this.setting = setting;
	}

	@Test
	public void testReplay() throws IOException, MapLoadException, ClassNotFoundException {
		synchronized (ONLY_ONE_TEST_AT_A_TIME_LOCK) {
			MapLoader[] actualSaveGames = ReplayUtils.replayAndCreateSavegames(setting.getReplayFile(), setting.getTimeMinutes());
			setting.compareSaveGamesAndDelete(actualSaveGames);
		}
	}
}
