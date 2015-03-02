package jsettlers.main.replay;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ReplayStartInformation;
import networklib.NetworkConstants;
import networklib.client.OfflineNetworkConnector;
import networklib.client.interfaces.INetworkConnector;

public class ReplayTool {
	public static void replayAndCreateSavegame(File replayFile, int targetGameTime) throws IOException {
		OfflineNetworkConnector networkConnector = new OfflineNetworkConnector();
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		JSettlersGame game = loadGameFromReplay(replayFile, networkConnector, replayStartInformation);
		IStartingGame startingGame = game.start();
		waitForGameStartup(startingGame);

		// schedule the save task and run the game to the target game time
		networkConnector.scheduleTaskAt(targetGameTime / NetworkConstants.Client.LOCKSTEP_PERIOD, new SimpleGuiTask(EGuiAction.QUICK_SAVE, (byte) 0));
		MatchConstants.clock.fastForwardTo(targetGameTime);

		// create a replay basing on the savegame and containing the remaining tasks.
		MapLoader newSavegame = MapList.getDefaultList().getSavedMaps().get(0);
		createReplayOfRemainingTasks(newSavegame, replayStartInformation, "replayForSavegame.log");
	}

	private static void waitForGameStartup(IStartingGame game) {
		DummyStartingGameListener startingGameListener = new DummyStartingGameListener();
		game.setListener(startingGameListener);
		startingGameListener.waitForGameStartup();
	}

	private static JSettlersGame loadGameFromReplay(File replayFile, INetworkConnector networkConnector, ReplayStartInformation replayStartInformation)
			throws IOException {
		File loadableReplayFile = replayFile;
		System.out.println("Found loadable replay file. Started loading it: " + loadableReplayFile);

		return JSettlersGame.loadFromReplayFile(loadableReplayFile, networkConnector, replayStartInformation);
	}

	private static void createReplayOfRemainingTasks(MapLoader newSavegame, ReplayStartInformation replayStartInformation, String newReplayFile)
			throws IOException {
		System.out.println("Creating new replay file (" + newReplayFile + ")...");

		ReplayStartInformation replayInfo = new ReplayStartInformation(0, newSavegame.getMapName(),
				newSavegame.getMapID(), replayStartInformation.getPlayerId(), replayStartInformation.getAvailablePlayers());

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(newReplayFile));
		replayInfo.serialize(dos);
		MatchConstants.clock.saveRemainingTasks(dos);

		dos.close();

		System.out.println("New replay file successfully created!");
	}
}
