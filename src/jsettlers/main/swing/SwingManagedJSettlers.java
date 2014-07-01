package jsettlers.main.swing;

import go.graphics.area.Area;
import go.graphics.nativegl.NativeAreaWindow;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.logic.LogicRevision;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ReplayStartInformation;
import jsettlers.main.StartScreenConnector;
import networklib.client.OfflineNetworkConnector;

/**
 * 
 * @author Andreas Eberle
 * @author michael
 */
public class SwingManagedJSettlers {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MapLoadException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, MapLoadException {
		HashMap<String, String> argsMap = MainUtils.createArgumentsMap(args);

		setupResourceManagers(argsMap, new File("config.prp"));
		loadDebugSettings(argsMap);

		JSettlersScreen content = startGui(argsMap);
		generateContent(argsMap, content);
	}

	/**
	 * Sets up the {@link ResourceManager} by using a configuration file. <br>
	 * First it is checked, if the given argsMap contains a "configFile" parameter. If so, the path specified for this parameter is used to get the
	 * file. <br>
	 * If the parameter is not given, the defaultConfigFile is used.
	 * 
	 * @param argsMap
	 * @param defaultConfigFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void setupResourceManagers(HashMap<String, String> argsMap, File defaultConfigFile) throws FileNotFoundException, IOException {
		File configFile;
		if (argsMap.containsKey("configFile")) {
			configFile = new File(argsMap.get("configFile"));
		} else {
			configFile = defaultConfigFile;
		}

		SwingResourceLoader.setupResourceManagersByConfigFile(configFile);
	}

	public static void loadDebugSettings(HashMap<String, String> argsMap) {
		if (argsMap.containsKey("control-all")) {
			CommonConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR = true;
			CommonConstants.ENABLE_ALL_PLAYER_SELECTION = true;
			CommonConstants.ENABLE_FOG_OF_WAR_DISABLING = true;
		}
		if (argsMap.containsKey("localhost")) {
			CommonConstants.DEFAULT_SERVER_ADDRESS = "localhost";
		}
		if (argsMap.containsKey("activate-all-players")) {
			CommonConstants.ACTIVATE_ALL_PLAYERS = true;
		}
	}

	/**
	 * Creates a new SWING GUI for the game.
	 * 
	 * @param argsList
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static JSettlersScreen startGui(HashMap<String, String> argsMap) {
		Area area = new Area();
		JSettlersScreen content = new JSettlersScreen(new StartScreenConnector(), new SwingSoundPlayer(), "r" + Revision.REVISION + " / r"
				+ LogicRevision.REVISION);
		area.add(content.getRegion());

		if (argsMap.containsKey("force-jogl")) {
			startJogl(area);
		} else if (argsMap.containsKey("force-native")) {
			startNative(area);
		} else {
			try {
				startNative(area);
			} catch (Throwable t) {
				startJogl(area);
			}
		}

		startRedrawTimer(content);
		return content;
	}

	private static void generateContent(HashMap<String, String> argsMap, JSettlersScreen content) throws IOException, MapLoadException {
		String mapfile = null;
		long randomSeed = 0;
		File loadableReplayFile = null;
		int targetGameTime = 0;

		if (argsMap.containsKey("mapfile")) {
			mapfile = argsMap.get("mapfile");
		}
		if (argsMap.containsKey("random")) {
			randomSeed = Long.parseLong(argsMap.get("random"));
		}
		if (argsMap.containsKey("replayFile")) {
			String loadableReplayFileString = argsMap.get("replayFile");
			File replayFile = new File(loadableReplayFileString);
			if (replayFile.exists()) {
				loadableReplayFile = replayFile;
				System.out.println("Found loadable replay file and loading it: " + loadableReplayFile);
			} else {
				System.err.println("Found replayFile parameter, but file can not be found!");
			}
		}
		if (argsMap.containsKey("targetTime")) {
			targetGameTime = Integer.valueOf(argsMap.get("targetTime")) * 60 * 1000;
		}

		if (mapfile != null || loadableReplayFile != null) {
			IStartingGame game;
			if (loadableReplayFile == null) {
				MapLoader mapLoader = MapLoader.getLoaderForFile(new File(mapfile));
				byte playerId = 0;
				boolean[] availablePlayers = new boolean[mapLoader.getMaxPlayers()];
				availablePlayers[playerId] = true;
				game = new JSettlersGame(mapLoader, randomSeed, playerId, availablePlayers).start();
			} else {
				game = JSettlersGame.loadFromReplayFile(loadableReplayFile, new OfflineNetworkConnector(), new ReplayStartInformation()).start();
			}
			StartingGamePanel toDisplay = new StartingGamePanel(game, content);
			content.setContent(toDisplay);

			if (targetGameTime > 0) {
				while (!game.isStartupFinished()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				MatchConstants.clock.fastForwardTo(targetGameTime);
			}
		} else {
			content.goToStartScreen("");
		}
	}

	private static void startRedrawTimer(final JSettlersScreen content) {
		new Timer("opengl-redraw").schedule(new TimerTask() {
			@Override
			public void run() {
				content.getRegion().requestRedraw();
			}
		}, 100, 25);
	}

	private static void startJogl(Area area) {
		JFrame jsettlersWnd = new JFrame("JSettlers - Revision: " + Revision.REVISION + "/" + LogicRevision.REVISION);
		AreaContainer panel = new AreaContainer(area);
		panel.setPreferredSize(new Dimension(640, 480));
		jsettlersWnd.add(panel);
		panel.requestFocusInWindow();

		jsettlersWnd.pack();
		jsettlersWnd.setSize(1200, 800);
		jsettlersWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jsettlersWnd.setVisible(true);
		jsettlersWnd.setLocationRelativeTo(null);
	}

	private static void startNative(Area area) {
		new NativeAreaWindow(area);
	}
}
