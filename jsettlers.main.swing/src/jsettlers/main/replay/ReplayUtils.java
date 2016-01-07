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
package jsettlers.main.replay;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.FileUtils.IFileVisitor;
import jsettlers.common.utils.MutableInt;
import jsettlers.common.utils.Tuple;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.JSettlersGame.GameRunner;
import jsettlers.main.ReplayStartInformation;
import jsettlers.network.NetworkConstants;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.INetworkConnector;

/**
 * 
 * @author Andreas Eberle
 *
 */
public class ReplayUtils {

	public static MapLoader replayAndCreateSavegame(File replayFile, float targetGameTimeMinutes, String newReplayFile) throws IOException {
		OfflineNetworkConnector networkConnector = createPausingOfflineNetworkConnector();
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		JSettlersGame game = loadGameFromReplay(replayFile, networkConnector, replayStartInformation);

		IStartedGame startedGame = startGame(game); // before we can save the clock reference, the game must be started
		IGameClock gameClock = MatchConstants.clock(); // after the game, the clock cannot be accessed any more => save reference before the game
		MapLoader newSavegame = playGameToTargetTimeAndGetSavegames(startedGame, networkConnector, targetGameTimeMinutes)[0];

		// create a replay basing on the savegame and containing the remaining tasks.
		createReplayOfRemainingTasks(newSavegame, replayStartInformation, newReplayFile, gameClock);

		System.out.println("Replayed: " + replayFile + " and created savegame: " + newSavegame);

		return newSavegame;
	}

	public static OfflineNetworkConnector createPausingOfflineNetworkConnector() {
		OfflineNetworkConnector networkConnector = new OfflineNetworkConnector();
		networkConnector.getGameClock().setPausing(true);
		return networkConnector;
	}

	public static MapLoader[] playGameToTargetTimeAndGetSavegames(JSettlersGame game, OfflineNetworkConnector networkConnector,
			final float... targetGameTimesMinutes) throws IOException {
		IStartedGame startedGame = startGame(game);
		return playGameToTargetTimeAndGetSavegames(startedGame, networkConnector, targetGameTimesMinutes);
	}

	private static MapLoader[] playGameToTargetTimeAndGetSavegames(IStartedGame startedGame, OfflineNetworkConnector networkConnector,
			final float... targetGameTimesMinutes) {
		final int[] targetGameTimesMs = getGameTimeMsFromMinutes(targetGameTimesMinutes);

		// schedule the save task and run the game to the target game time
		MapLoader[] savegames = new MapLoader[targetGameTimesMs.length];
		for (int i = 0; i < targetGameTimesMs.length; i++) {
			final int targetGameTimeMs = targetGameTimesMs[i];

			networkConnector.scheduleTaskAt(targetGameTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD,
					new SimpleGuiTask(EGuiAction.QUICK_SAVE, (byte) 0));
			MatchConstants.clock().fastForwardTo(targetGameTimeMs);
			savegames[i] = getNewestSavegame();
		}

		awaitShutdown(startedGame);

		return savegames;
	}

	private static int[] getGameTimeMsFromMinutes(final float... targetGameTimesMinutes) {
		final int[] targetGameTimesMs = new int[targetGameTimesMinutes.length];
		for (int i = 0; i < targetGameTimesMinutes.length; i++) {
			targetGameTimesMs[i] = (int) (targetGameTimesMinutes[i] * 60 * 1000);
		}
		Arrays.sort(targetGameTimesMs);
		return targetGameTimesMs;
	}

	public static MapLoader getNewestSavegame() {
		List<? extends MapLoader> savedMaps = MapList.getDefaultList().getSavedMaps().getItems();
		if (savedMaps.isEmpty()) {
			return null;
		}

		MapLoader newest = savedMaps.get(0);
		for (MapLoader map : savedMaps) {
			if (newest.getCreationDate().before(map.getCreationDate())) {
				newest = map;
			}
		}
		return newest;
	}

	public static void awaitShutdown(IStartedGame startedGame) {
		final MutableInt gameStopped = new MutableInt(0);

		startedGame.setGameExitListener(new IGameExitListener() {
			@Override
			public void gameExited(IStartedGame game) {
				gameStopped.value = 1;
				synchronized (gameStopped) {
					gameStopped.notifyAll();
				}
			}
		});

		((GameRunner) startedGame).stopGame();

		synchronized (gameStopped) {
			while (gameStopped.value == 0 && !startedGame.isShutdownFinished()) {
				try {
					gameStopped.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static IStartedGame startGame(JSettlersGame game) {
		IStartingGame startingGame = game.start();
		IStartedGame startedGame = waitForGameStartup(startingGame);
		return startedGame;
	}

	public static IStartedGame waitForGameStartup(IStartingGame game) {
		DummyStartingGameListener startingGameListener = new DummyStartingGameListener();
		game.setListener(startingGameListener);
		return startingGameListener.waitForGameStartup();
	}

	private static JSettlersGame loadGameFromReplay(File replayFile, INetworkConnector networkConnector,
			ReplayStartInformation replayStartInformation) throws IOException {
		System.out.println("Found loadable replay file. Started loading it: " + replayFile);
		return JSettlersGame.loadFromReplayFile(replayFile, networkConnector, replayStartInformation);
	}

	private static void createReplayOfRemainingTasks(MapLoader newSavegame, ReplayStartInformation replayStartInformation, String newReplayFile,
			IGameClock gameClock) throws IOException {
		System.out.println("Creating new replay file (" + newReplayFile + ")...");
		new File(newReplayFile).getAbsoluteFile().getParentFile().mkdirs();

		ReplayStartInformation replayInfo = new ReplayStartInformation(0, newSavegame.getMapName(),
				newSavegame.getMapId(), replayStartInformation.getPlayerId(), replayStartInformation.getPlayerSettings());

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(newReplayFile));
		replayInfo.serialize(dos);
		gameClock.saveRemainingTasks(dos);

		dos.close();

		System.out.println("New replay file successfully created!");
	}

	public static ReplayAndSavegames playMapToTargetTimes(final String mapName, final float... targetTimeMinutes) throws IOException {
		OfflineNetworkConnector networkConnector = ReplayUtils.createPausingOfflineNetworkConnector();
		MapLoader map = MapList.getDefaultList().getMapByName(mapName);
		byte playerId = (byte) 0;
		JSettlersGame game = new JSettlersGame(map, 0L, networkConnector, playerId,
				PlayerSetting.createDefaultSettings(playerId, (byte) map.getMaxPlayers()));

		MapLoader[] savegames = ReplayUtils.playGameToTargetTimeAndGetSavegames(game, networkConnector, targetTimeMinutes);
		File replay = findNewestReplayFile().getCanonicalFile();
		System.out.println("Found replay file for savegame: " + replay);

		ReplayAndSavegames directSavegameReplay = new ReplayAndSavegames(replay, savegames);
		return directSavegameReplay;
	}

	private static File findNewestReplayFile() throws IOException {
		final File[] newestReplay = new File[1];

		FileUtils.walkFileTree(new File(ResourceManager.getResourcesDirectory(), "logs"), new IFileVisitor() {
			private long newestModificationTime;

			@Override
			public void visitFile(File file) throws IOException {
				if (file.isDirectory() || !file.getName().endsWith("replay.log")) {
					return;
				}

				if (newestModificationTime < file.lastModified()) {
					newestModificationTime = file.lastModified();
					newestReplay[0] = file;
				}
			}
		});

		return newestReplay[0];
	}

	public static class ReplayAndSavegames extends Tuple<File, MapLoader[]> {
		private static final long serialVersionUID = -334532778493138737L;

		public ReplayAndSavegames(File replayFile, MapLoader[] savegames) {
			super(replayFile, savegames);
		}

		public File getReplayFile() {
			return e1;
		}

		public MapLoader[] getSavegames() {
			return e2;
		}
	}
}
