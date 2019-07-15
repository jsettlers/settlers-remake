/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.integration.ai;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.logging.StatisticsStopWatch;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.testutils.TestUtils;
import jsettlers.testutils.map.MapUtils;

/**
 * @author codingberlin
 */
public class AiDifficultiesIT {
	private static final int MINUTES = 1000 * 60;
	private static final int JUMP_FORWARD = 2 * MINUTES;
	private static final String LOW_PERFORMANCE_FAILURE_MESSAGE = "%s's %s is higher than %d. It was %d\nSome code change caused the AI to have a worse runtime performance.";

	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;

		TestUtils.setupTempResourceManager();
	}

	@Test
	public void easyShouldConquerVeryEasy() throws MapLoadException {
		holdBattleBetween(EPlayerType.AI_EASY, EPlayerType.AI_VERY_EASY, 90 * MINUTES);
	}

	@Test
	public void hardShouldConquerEasy() throws MapLoadException {
		holdBattleBetween(EPlayerType.AI_HARD, EPlayerType.AI_EASY, 75 * MINUTES);
	}

	@Test
	public void veryHardShouldConquerHard() throws MapLoadException {
		holdBattleBetween(EPlayerType.AI_VERY_HARD, EPlayerType.AI_HARD, 75 * MINUTES);
	}

	@Test
	public void veryHardShouldProduceCertainAmountOfSoldiersWithin90Minutes() throws MapLoadException {
		byte playerId = (byte) 0;
		PlayerSetting[] playerSettings = getDefaultPlayerSettings(12);
		playerSettings[playerId] = new PlayerSetting(EPlayerType.AI_VERY_HARD, ECivilisation.ROMAN, playerId);

		JSettlersGame.GameRunner startingGame = createStartingGame(playerSettings);
		IStartedGame startedGame = ReplayUtils.waitForGameStartup(startingGame);

		MatchConstants.clock().fastForwardTo(90 * MINUTES);

		short expectedMinimalProducedSoldiers = 850;
		short producedSoldiers = startingGame.getMainGrid().getPartitionsGrid().getPlayer(0).getEndgameStatistic().getAmountOfProducedSoldiers();
		if (producedSoldiers < expectedMinimalProducedSoldiers) {
			stopAndFail("AI_VERY_HARD was not able to produce " + expectedMinimalProducedSoldiers + " soldiers within 90 minutes.\nOnly " + producedSoldiers
					+ " soldiers were produced. Some code changes make the AI weaker.", startedGame, startingGame.getMainGrid(), playerId);
		}
		ensureRuntimePerformance("to apply rules", startingGame.getAiExecutor().getApplyRulesStopWatch(), 200, 2500);
		ensureRuntimePerformance("to update statistics", startingGame.getAiExecutor().getUpdateStatisticsStopWatch(), 100, 2500);
	}

	private void holdBattleBetween(EPlayerType expectedWinner, EPlayerType expectedLooser, int maximumTimeToWin) throws MapLoadException {
		byte expectedWinnerSlotId = 9;
		byte expectedLooserSlotId = 7;
		PlayerSetting[] playerSettings = getDefaultPlayerSettings(12);
		playerSettings[expectedWinnerSlotId] = new PlayerSetting(expectedWinner, ECivilisation.ROMAN, (byte) 0);
		playerSettings[expectedLooserSlotId] = new PlayerSetting(expectedLooser, ECivilisation.ROMAN, (byte) 1);

		JSettlersGame.GameRunner startingGame = createStartingGame(playerSettings);
		IStartedGame startedGame = ReplayUtils.waitForGameStartup(startingGame);
		AiStatistics aiStatistics = new AiStatistics(startingGame.getMainGrid());

		int targetGameTime = 0;
		do {
			targetGameTime += JUMP_FORWARD;
			MatchConstants.clock().fastForwardTo(targetGameTime);
			aiStatistics.updateStatistics();
			if (!aiStatistics.isAlive(expectedWinnerSlotId)) {
				stopAndFail(expectedWinner + " was defeated by " + expectedLooser, startedGame, startingGame.getMainGrid(), expectedWinnerSlotId);
			}
			if (MatchConstants.clock().getTime() > maximumTimeToWin) {
				stopAndFail(expectedWinner + " was not able to defeat " + expectedLooser + " within " + (maximumTimeToWin / 60000)
						+ " minutes.\nIf the AI code was changed in a way which makes the " + expectedLooser + " stronger with the sideeffect that "
						+ "the " + expectedWinner + " needs more time to win you could make the " + expectedWinner + " stronger, too, or increase "
						+ "the maximumTimeToWin.", startedGame, startingGame.getMainGrid(), expectedWinnerSlotId);
			}
		} while (aiStatistics.isAlive(expectedLooserSlotId));
		System.out.println("The battle between " + expectedWinner + " and " + expectedLooser + " took " + (MatchConstants.clock().getTime() / 60000) +
				" minutes.");
		ReplayUtils.awaitShutdown(startedGame);

		ensureRuntimePerformance("to apply rules", startingGame.getAiExecutor().getApplyRulesStopWatch(), 200, 3000);
		ensureRuntimePerformance("to update statistics", startingGame.getAiExecutor().getUpdateStatisticsStopWatch(), 100, 2500);
	}

	private void ensureRuntimePerformance(String description, StatisticsStopWatch stopWatch, long median, int max) {
		System.out.println(description + ": " + stopWatch);
		if (stopWatch.getMedian() > median) {
			String medianText = String.format(Locale.ENGLISH, LOW_PERFORMANCE_FAILURE_MESSAGE, description, "median", median, stopWatch.getMedian());
			System.out.println(medianText);
			fail(medianText);
		}
		if (stopWatch.getMax() > max) {
			String maxText = String.format(Locale.ENGLISH, LOW_PERFORMANCE_FAILURE_MESSAGE, description, "max", max, stopWatch.getMax());
			System.out.println(maxText);
			fail(maxText);
		}
	}

	private JSettlersGame.GameRunner createStartingGame(PlayerSetting[] playerSettings) throws MapLoadException {
		byte playerId = 0;
		for (byte i = 0; i < playerSettings.length; i++) {
			if (playerSettings[i].isAvailable()) {
				playerId = i;
				break;
			}
		}

		MapLoader mapCreator = MapUtils.getSpezialSumpf();
		JSettlersGame game = new JSettlersGame(mapCreator, 1L, new OfflineNetworkConnector(), playerId, playerSettings);
		return (JSettlersGame.GameRunner) game.start();
	}

	private void stopAndFail(String reason, IStartedGame startedGame, MainGrid mainGrid, byte playerId) {
		MapLoader savegame = MapUtils.saveMainGrid(mainGrid, playerId, null);
		System.out.println("Saved game at: " + savegame.getListedMap().getFile());

		ReplayUtils.awaitShutdown(startedGame);
		fail(reason);
	}

	private PlayerSetting[] getDefaultPlayerSettings(int numberOfPlayers) {
		PlayerSetting[] playerSettings = new PlayerSetting[numberOfPlayers];
		Arrays.fill(playerSettings, 0, numberOfPlayers, new PlayerSetting());
		return playerSettings;
	}
}
