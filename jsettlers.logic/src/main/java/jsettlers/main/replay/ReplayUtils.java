/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.mutables.MutableInt;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.JSettlersGame.GameRunner;
import jsettlers.main.ReplayStartInformation;
import jsettlers.network.NetworkConstants;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.INetworkConnector;

import static java8.util.J8Arrays.stream;

/**
 *
 * @author Andreas Eberle
 *
 */
public class ReplayUtils {

	public static MapLoader replayAndCreateSavegame(IReplayStreamProvider replayFile, int targetGameTimeMinutes, String newReplayFile) throws MapLoadException, IOException {
		OfflineNetworkConnector networkConnector = createPausingOfflineNetworkConnector();
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		JSettlersGame game = loadGameFromReplay(replayFile, networkConnector, replayStartInformation);

		IStartedGame startedGame = startGame(game); // before we can save the clock reference, the game must be started
		IGameClock gameClock = MatchConstants.clock(); // after the game, the clock cannot be accessed any more => save reference before the game
		MapLoader newSavegame = playGameToTargetTimeAndGetSavegames(startedGame, networkConnector, targetGameTimeMinutes)[0];

		// create a jsettlers.integration.replay basing on the savegame and containing the remaining tasks.
		createReplayOfRemainingTasks(newSavegame, replayStartInformation, newReplayFile, gameClock);

		System.out.println("Replayed: " + replayFile + " and created savegame: " + newSavegame);

		return newSavegame;
	}

	public static MapLoader[] replayAndCreateSavegames(IReplayStreamProvider replayFile, int[] targetGameTimeMinutes) throws MapLoadException {
		OfflineNetworkConnector networkConnector = createPausingOfflineNetworkConnector();
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		JSettlersGame game = loadGameFromReplay(replayFile, networkConnector, replayStartInformation);

		MapLoader[] newSavegame = playGameToTargetTimeAndGetSavegames(game, networkConnector, targetGameTimeMinutes);

		System.out.println("Replayed: " + replayFile + " and created savegames: " + Arrays.asList(newSavegame));

		return newSavegame;
	}

	private static OfflineNetworkConnector createPausingOfflineNetworkConnector() {
		OfflineNetworkConnector networkConnector = new OfflineNetworkConnector();
		networkConnector.getGameClock().setPausing(true);
		return networkConnector;
	}

	private static MapLoader[] playGameToTargetTimeAndGetSavegames(JSettlersGame game, OfflineNetworkConnector networkConnector, final int... targetGameTimesMinutes) {
		IStartedGame startedGame = startGame(game);
		return playGameToTargetTimeAndGetSavegames(startedGame, networkConnector, targetGameTimesMinutes);
	}

	private static MapLoader[] playGameToTargetTimeAndGetSavegames(IStartedGame startedGame, OfflineNetworkConnector networkConnector, final int... targetGameTimesMinutes) {
		final int[] targetGameTimesMs = getGameTimeMsFromMinutes(targetGameTimesMinutes);

		// schedule the save task and run the game to the target game time
		MapLoader[] savegames = new MapLoader[targetGameTimesMs.length];
		for (int i = 0; i < targetGameTimesMs.length; i++) {
			final int targetGameTimeMs = targetGameTimesMs[i];

			networkConnector.scheduleTaskAt(targetGameTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD,
				new SimpleGuiTask(EGuiAction.QUICK_SAVE, (byte) 0)
			);
			MatchConstants.clock().fastForwardTo(targetGameTimeMs + 1000);
			savegames[i] = getNewestSavegame();
		}

		awaitShutdown(startedGame);

		return savegames;
	}

	private static int[] getGameTimeMsFromMinutes(final int... targetGameTimesMinutes) {
		return stream(targetGameTimesMinutes).map(minute -> minute * 60 * 1000).sorted().toArray();
	}

	public static MapLoader getNewestSavegame() {
		List<? extends MapLoader> savedMaps = MapList.getDefaultList().getSavedMaps().getItems();
		if (savedMaps.isEmpty()) {
			throw new RuntimeException("No saved games found.");
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

		startedGame.setGameExitListener(game -> {
			gameStopped.value = 1;
			synchronized (gameStopped) {
				gameStopped.notifyAll();
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
		return waitForGameStartup(startingGame);
	}

	public static IStartedGame waitForGameStartup(IStartingGame game) {
		DummyStartingGameListener startingGameListener = new DummyStartingGameListener();
		game.setListener(startingGameListener);
		return startingGameListener.waitForGameStartup();
	}

	private static JSettlersGame loadGameFromReplay(IReplayStreamProvider replayFile, INetworkConnector networkConnector, ReplayStartInformation replayStartInformation) throws MapLoadException {
		System.out.println("Found loadable jsettlers.integration.replay file. Started loading it: " + replayFile);
		return JSettlersGame.loadFromReplayFile(replayFile, networkConnector, replayStartInformation);
	}

	private static void createReplayOfRemainingTasks(MapLoader newSavegame, ReplayStartInformation replayStartInformation, String newReplayFile, IGameClock gameClock) throws IOException {
		System.out.println("Creating new jsettlers.integration.replay file (" + newReplayFile + ")...");

		ReplayStartInformation replayInfo = new ReplayStartInformation(0, newSavegame.getMapName(), newSavegame.getMapId(), replayStartInformation.getPlayerId(),
			replayStartInformation.getPlayerSettings()
		);

		DataOutputStream dos = new DataOutputStream(ResourceManager.writeUserFile(newReplayFile));
		replayInfo.serialize(dos);
		gameClock.saveRemainingTasks(dos);

		dos.close();

		System.out.println("New jsettlers.integration.replay file successfully created!");
	}

	public static PlayMapResult playMapToTargetTimes(MapLoader map, byte playerId, final int... targetTimeMinutes) {
		OfflineNetworkConnector networkConnector = ReplayUtils.createPausingOfflineNetworkConnector();
		JSettlersGame game = new JSettlersGame(map, 0L, networkConnector, playerId, PlayerSetting.createDefaultSettings(playerId, (byte) map.getMaxPlayers())) {
			@Override
			protected OutputStream createReplayWriteStream() throws IOException {
				return ResourceManager.writeConfigurationFile("jsettlers.integration.replay");
			}
		};

		final MapLoader[] savegames = ReplayUtils.playGameToTargetTimeAndGetSavegames(game, networkConnector, targetTimeMinutes);

		return new PlayMapResult(map, savegames);
	}

	public interface IReplayStreamProvider {
		InputStream openStream() throws IOException;

		MapLoader getMap(ReplayStartInformation replayStartInformation) throws MapLoadException;
	}

	/**
	 * A jsettlers.integration.replay file using the default list.
	 *
	 * @see MapList#defaultList
	 */
	public static class ReplayFile implements IReplayStreamProvider {
		private final File file;

		public ReplayFile(File file) {
			this.file = file;
		}

		@Override
		public InputStream openStream() throws IOException {
			return new FileInputStream(file);
		}

		@Override
		public MapLoader getMap(ReplayStartInformation replayStartInformation) {
			return MapList.getDefaultList().getMapById(replayStartInformation.getMapId());
		}
	}

	public static class PlayMapResult implements IReplayStreamProvider {
		private final MapLoader   map;
		private final MapLoader[] savegames;

		PlayMapResult(MapLoader map, MapLoader[] savegames) {
			this.map = map;
			this.savegames = savegames;
		}

		@Override
		public InputStream openStream() throws IOException {
			return ResourceManager.getResourcesFileStream("jsettlers.integration.replay");
		}

		@Override
		public MapLoader getMap(ReplayStartInformation replayStartInformation) throws MapLoadException {
			if (map.getMapId().equals(replayStartInformation.getMapId())) {
				return map;
			}
			throw new MapLoadException("No file found for " + replayStartInformation);
		}

		public MapLoader[] getSavegames() {
			return savegames;
		}
	}
}
