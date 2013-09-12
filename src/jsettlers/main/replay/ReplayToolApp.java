package jsettlers.main.replay;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ReplayStartInformation;
import jsettlers.main.swing.SwingManagedJSettlers;
import networklib.NetworkConstants;
import networklib.client.OfflineNetworkConnector;
import networklib.client.interfaces.INetworkConnector;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReplayToolApp {

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		HashMap<String, String> argsMap = MainUtils.createArgumentsMap(args);

		SwingManagedJSettlers.setupResourceManagers(argsMap, new File("config.prp"));

		int targetGameTime = Integer.valueOf(argsMap.get("targetTime")) * 60 * 1000;

		OfflineNetworkConnector networkConnector = new OfflineNetworkConnector();
		JSettlersGame game = getReplayGame(argsMap, networkConnector);
		IStartingGame startingGame = game.start();
		ReplayToolApp replayTool = new ReplayToolApp();
		replayTool.waitForGameStartup(startingGame);

		// schedule the save task and run the game to the target game time
		networkConnector.scheduleTaskAt(targetGameTime / NetworkConstants.Client.LOCKSTEP_PERIOD, new SimpleGuiTask(EGuiAction.QUICK_SAVE));
		MatchConstants.clock.fastForwardTo(targetGameTime);

		// create a replay basing on the savegame and containing the remaining tasks.
		MapLoader newSavegame = MapList.getDefaultList().getSavedMaps().get(0);
		replayTool.createReplayOfRemainingTasks(newSavegame);

		Thread.sleep(2000);

		System.exit(0);
	}

	private final Object waitMutex = new Object();
	private IStartedGame startedGame = null;

	private void waitForGameStartup(IStartingGame game) {
		game.setListener(new IStartingGameListener() {
			@Override
			public void startProgressChanged(EProgressState state, float progress) {
			}

			@Override
			public IMapInterfaceConnector startFinished(IStartedGame game) {
				startedGame = game;
				synchronized (waitMutex) {
					waitMutex.notifyAll();
				}
				return new DummyMapInterfaceConnector();
			}

			@Override
			public void startFailed(EGameError errorType, Exception exception) {
				System.err.println("start failed due to: " + errorType);
				exception.printStackTrace();
				System.exit(1);
			}
		});

		while (startedGame == null) {
			synchronized (waitMutex) {
				try {
					waitMutex.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static JSettlersGame getReplayGame(HashMap<String, String> argsMap, INetworkConnector networkConnector) throws IOException {
		if (argsMap.containsKey("replayFile")) {
			String loadableReplayFileString = argsMap.get("replayFile");
			File replayFile = new File(loadableReplayFileString);
			if (replayFile.exists()) {
				File loadableReplayFile = replayFile;
				System.out.println("Found loadable replay file and loading it: " + loadableReplayFile);

				return JSettlersGame.loadFromReplayFile(loadableReplayFile, networkConnector);
			} else {
				throw new FileNotFoundException("Found replayFile parameter, but file can not be found: " + replayFile);
			}
		}

		throw new IllegalArgumentException("Replay file needs to be specified with --replayFile=<FILE>");
	}

	private void createReplayOfRemainingTasks(MapLoader newSavegame) throws IOException {
		System.out.println("Creating new replay file...");

		ReplayStartInformation replayInfo = new ReplayStartInformation(0, startedGame.getPlayer(), newSavegame.getMapName(), newSavegame.getMapID());

		DataOutputStream dos = new DataOutputStream(new FileOutputStream("replayForSavegame.log"));
		replayInfo.serialize(dos);
		MatchConstants.clock.saveRemainingTasks(dos);

		dos.close();

		System.out.println("New replay file successfully created!");
	}
}
