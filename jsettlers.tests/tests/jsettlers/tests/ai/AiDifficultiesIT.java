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
package jsettlers.tests.ai;

import jsettlers.TestUtils;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EWhatToDoAiType;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.replay.ReplayTool;
import jsettlers.network.client.OfflineNetworkConnector;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author codingberlin
 */
public class AiDifficultiesIT {
	public static final int TEN_MINUTES = 1000 * 60 * 10;
	public static final int MAXIMUM_TIME = 1000 * 60 * 300;

	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		TestUtils.setupResourcesManager();
	}

	@Test
	public void easyShouldConquerVeryEasy() {
		holdBattleBetween(EWhatToDoAiType.ROMAN_EASY, EWhatToDoAiType.ROMAN_VERY_EASY);
	}

	@Test
	public void hardShouldConquerEasy() {
		holdBattleBetween(EWhatToDoAiType.ROMAN_HARD, EWhatToDoAiType.ROMAN_EASY);
	}

	@Test
	public void veryHardShouldConquerHard() {
		holdBattleBetween(EWhatToDoAiType.ROMAN_VERY_HARD, EWhatToDoAiType.ROMAN_HARD);
	}

	private void holdBattleBetween(EWhatToDoAiType expectedWinner, EWhatToDoAiType expectedLooser) {
		PlayerSetting[] playerSettings = new PlayerSetting[4];
		playerSettings[0] = new PlayerSetting(true, expectedLooser);
		playerSettings[1] = new PlayerSetting(true, expectedWinner);
		playerSettings[2] = new PlayerSetting(false, null);
		playerSettings[3] = new PlayerSetting(false, null);

		MapLoader mapCreator = MapList.getDefaultList().getMapById("066d3c28-8f37-41cf-96c1-270109f00b9f");

		JSettlersGame game = new JSettlersGame(mapCreator, 1l, new OfflineNetworkConnector(), (byte) 0, playerSettings);
		JSettlersGame.GameRunner startingGame = (JSettlersGame.GameRunner) game.start();
		IStartedGame startedGame = ReplayTool.waitForGameStartup(startingGame);
		AiStatistics aiStatistics = new AiStatistics(startingGame.getMainGrid());

		int targetGameTime = 0;
		do {
			targetGameTime += TEN_MINUTES;
			MatchConstants.clock.fastForwardTo(targetGameTime);
			aiStatistics.updateStatistics();
			if (aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.TOWER, (byte) 1) == 0) {
				stopAndFail(expectedWinner + " was defeated by " + expectedLooser, startedGame);
			}
			if (MatchConstants.clock.getTime() > MAXIMUM_TIME) {
				ReplayTool.awaitShutdown(startedGame);
				stopAndFail(expectedWinner + " was not able to defeat " + expectedLooser + " within " + (MAXIMUM_TIME / 60000)
						+ " minutes", startedGame);
			}
		} while (aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.TOWER, (byte) 0) > 0);
		ReplayTool.awaitShutdown(startedGame);

	}

	private void stopAndFail(String reason, IStartedGame startedGame) {
		ReplayTool.awaitShutdown(startedGame);
		fail(reason);
	}

}
