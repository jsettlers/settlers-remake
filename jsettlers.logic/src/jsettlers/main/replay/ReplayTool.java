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
import java.util.List;

import jsettlers.common.utils.MutableInt;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.JSettlersGame.GameRunner;
import jsettlers.main.ReplayStartInformation;
import jsettlers.network.NetworkConstants;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.network.client.interfaces.INetworkConnector;

public class ReplayTool {

	public static void replayAndCreateSavegame(File replayFile, int targetGameTimeMs, String newReplayFile) throws IOException {

		OfflineNetworkConnector networkConnector = new OfflineNetworkConnector();
		networkConnector.getGameClock().setPausing(true);
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		JSettlersGame game = loadGameFromReplay(replayFile, networkConnector, replayStartInformation);
		IStartingGame startingGame = game.start();
		IStartedGame startedGame = waitForGameStartup(startingGame);

		// schedule the save task and run the game to the target game time
		networkConnector.scheduleTaskAt(targetGameTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD,
				new SimpleGuiTask(EGuiAction.QUICK_SAVE, (byte) 0));
		MatchConstants.clock.fastForwardTo(targetGameTimeMs);

		// create a replay basing on the savegame and containing the remaining tasks.
		MapLoader newSavegame = getNewestSavegame();
		createReplayOfRemainingTasks(newSavegame, replayStartInformation, newReplayFile);

		awaitShutdown(startedGame);
	}

	private static MapLoader getNewestSavegame() {
		List<MapLoader> savedMaps = MapList.getDefaultList().getSavedMaps().getItems();
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

	private static void awaitShutdown(IStartedGame startedGame) {
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
			while (gameStopped.value == 0) {
				try {
					gameStopped.wait();
				} catch (InterruptedException e) {
				}
			}
		}

	}

	private static IStartedGame waitForGameStartup(IStartingGame game) {
		DummyStartingGameListener startingGameListener = new DummyStartingGameListener();
		game.setListener(startingGameListener);
		return startingGameListener.waitForGameStartup();
	}

	private static JSettlersGame loadGameFromReplay(File replayFile, INetworkConnector networkConnector,
			ReplayStartInformation replayStartInformation)
					throws IOException {
		File loadableReplayFile = replayFile;
		System.out.println("Found loadable replay file. Started loading it: " + loadableReplayFile);

		return JSettlersGame.loadFromReplayFile(loadableReplayFile, networkConnector, replayStartInformation);
	}

	private static void createReplayOfRemainingTasks(MapLoader newSavegame, ReplayStartInformation replayStartInformation, String newReplayFile)
			throws IOException {
		System.out.println("Creating new replay file (" + newReplayFile + ")...");
		new File(newReplayFile).getAbsoluteFile().getParentFile().mkdirs();

		ReplayStartInformation replayInfo = new ReplayStartInformation(0, newSavegame.getMapName(),
				newSavegame.getMapId(), replayStartInformation.getPlayerId(), replayStartInformation.getPlayerSettings());

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(newReplayFile));
		replayInfo.serialize(dos);
		MatchConstants.clock.saveRemainingTasks(dos);

		dos.close();

		System.out.println("New replay file successfully created!");
	}
}
