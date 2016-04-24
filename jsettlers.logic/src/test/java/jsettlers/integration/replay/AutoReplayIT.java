/*******************************************************************************
 * Copyright (c) 2015
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
import java.util.Arrays;
import java.util.Collection;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.TestUtils;
import jsettlers.testutils.map.MapUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AutoReplayIT {
	@BeforeClass
	public static void setConstantes() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;
	}

	static final String REMAINING_REPLAY_FILENAME = "out/remainingReplay.log";
	private static final Object ONLY_ONE_TEST_AT_A_TIME_LOCK = new Object();

	@Parameters(name = "{index}: {0} : {1}")
	public static Collection<Object[]> replaySets() {
		return Arrays.asList(new Object[][]{
				{"basicproduction", 15},

				{"fullproduction", 10},
				{"fullproduction", 20},
				{"fullproduction", 30},
				{"fullproduction", 40},
				{"fullproduction", 50},
				{"fullproduction", 69},

				{"fighting", 8}
		});
	}

	private final String folderName;
	private final int targetTimeMinutes;

	public AutoReplayIT(String folderName, int targetTimeMinutes) {
		this.folderName = folderName;
		this.targetTimeMinutes = targetTimeMinutes;
	}

	@Before
	public void fakeSaveDirectory() {
		TestUtils.setupMemoryResourceManager();
	}

	@Test
	public void testReplay() throws IOException, MapLoadException, ClassNotFoundException {
		synchronized (ONLY_ONE_TEST_AT_A_TIME_LOCK) {
			MapLoader actualSavegame = ReplayUtils.replayAndCreateSavegame(getReplayFile(), targetTimeMinutes, REMAINING_REPLAY_FILENAME);
			MapLoader expectedSavegame = getReferenceSavegamePath();

			MapUtils.compareMapFiles(expectedSavegame, actualSavegame);
			actualSavegame.getListedMap().delete();
		}
	}

	MapLoader getReferenceSavegamePath() throws MapLoadException, IOException {
		String replayPath = "/" + getClass().getPackage().getName().replace('.', '/');
		replayPath += "/" + folderName;
		replayPath += "/savegame-" + targetTimeMinutes + "m.zmap";

		System.out.println("Using reference file: " + replayPath);
		return MapLoader.getLoaderForListedMap(new MapList.ListedResourceMap(replayPath));
	}

	ReplayUtils.IReplayStreamProvider getReplayFile() throws MapLoadException {
		return MapUtils.createReplayForResource(getClass(), getReplayName(), MapUtils.getMap(getClass(), folderName + "/base.rmap"));
	}

	private String getReplayName() {
		return folderName + "/replay.log";
	}
}
