/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.integration.replay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jsettlers.testutils.TestUtils;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.IGameCreator.MainGridWithUiSettings;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.network.synchronic.timer.NetworkTimer;
import jsettlers.testutils.map.MapUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Andreas Eberle
 *
 */
public class ReplayValidationIT {
	private static final String REMAINING_REPLAY_FILENAME = "out/remainingReplay.log";

	@BeforeClass
	public static void loadSettings() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = false;
		CommonConstants.ALL_AI = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;

		TestUtils.setupTempResourceManager();
	}

	@Test
	public void testIfReplayIsEqualToOriginalPlay() throws IOException, MapLoadException, ClassNotFoundException {
		final float targetTimeMinutes = 60f;
		MapLoader map = MapUtils.getMountainlake();

		ReplayUtils.PlayMapResult directSavegameReplay = ReplayUtils.playMapToTargetTimes(map, targetTimeMinutes);
		assertDirectSavegameReplay(1, directSavegameReplay);
		MapLoader savegame = directSavegameReplay.getSavegames()[0];

		MapLoader replayedSavegame = ReplayUtils.replayAndCreateSavegame(directSavegameReplay, targetTimeMinutes,
				REMAINING_REPLAY_FILENAME);

		// compare direct savegame with replayed savegame.
		MapUtils.compareMapFiles(savegame, replayedSavegame);
	}

	@Test
	public void testIfSavegameOfSavegameEqualsSavegame() throws IOException, MapLoadException, ClassNotFoundException, InterruptedException {
		final float targetTimeMinutes = 30f;
		MapLoader map = MapUtils.getMountainlake();

		ReplayUtils.PlayMapResult directSavegameReplay = ReplayUtils.playMapToTargetTimes(map, targetTimeMinutes);
		assertDirectSavegameReplay(1, directSavegameReplay);
		MapLoader savegame = directSavegameReplay.getSavegames()[0];

		Thread.sleep(2000); // loading + saving might happend in less than a second => make sure the next savegame is saved with a different name

		System.out.println("Loading savegame...");
		MatchConstants.init(new NetworkTimer(true), 0L);
		MainGridWithUiSettings mainGrid = savegame.loadMainGrid(PlayerSetting.createDefaultSettings((byte) 0, (byte) savegame.getMaxPlayers()));
		MapLoader savegameOfSavegame;
		try {
			System.out.println("Creating savegame of savegame...");
			savegameOfSavegame = MapUtils.saveMainGrid(mainGrid.getMainGrid(), mainGrid.getPlayerStates());
			assertNotNull(savegameOfSavegame);
		} finally {
			mainGrid.getMainGrid().stopThreads();
			JSettlersGame.clearState();
		}

		// compare direct savegame with replayed savegame.
		MapUtils.compareMapFiles(savegame, savegameOfSavegame);
	}

	@Ignore
	@Test
	public void testReplayForSavegame() throws IOException, MapLoadException, ClassNotFoundException {
		final float[] targetTimeMinutes = new float[] { 30f, 60f };
		MapLoader map = MapUtils.getMountainlake();

		ReplayUtils.PlayMapResult directSavegameReplay = ReplayUtils.playMapToTargetTimes(map, targetTimeMinutes);
		assertDirectSavegameReplay(targetTimeMinutes.length, directSavegameReplay);

		MapLoader[] savegames = directSavegameReplay.getSavegames();
		for (int i = 0; i < targetTimeMinutes.length; i++) {
			float targetTime = targetTimeMinutes[i];
			MapLoader savegame = savegames[i];

			MapLoader replayedSavegame = ReplayUtils.replayAndCreateSavegame(directSavegameReplay, targetTime,
					REMAINING_REPLAY_FILENAME);

			// compare direct savegame with replayed savegame.
			System.out.println("Comparing jsettlers.integration.replay for savegame at targetTime: " + targetTime);
			MapUtils.compareMapFiles(savegame, replayedSavegame);
		}
	}

	private void assertDirectSavegameReplay(int expectedNumberOfSavegames, ReplayUtils.PlayMapResult directSavegameReplay) {
		assertNotNull(directSavegameReplay);
		assertEquals(expectedNumberOfSavegames, directSavegameReplay.getSavegames().length);
	}
}
