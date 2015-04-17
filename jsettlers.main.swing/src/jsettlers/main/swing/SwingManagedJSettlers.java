package jsettlers.main.swing;

import go.graphics.area.Area;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import jsettlers.common.CommitInfo;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ReplayStartInformation;
import jsettlers.main.StartScreenConnector;
import jsettlers.network.client.OfflineNetworkConnector;

/**
 * 
 * @author Andreas Eberle
 * @author michael
 */
public class SwingManagedJSettlers {

	private static final String BUILD = "commit: " + CommitInfo.COMMIT_HASH_SHORT;

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MapLoadException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, MapLoadException {
		HashMap<String, String> argsMap = MainUtils.createArgumentsMap(args);

		setupResourceManagers(argsMap, "config.prp");
		loadDebugSettings(argsMap);

		JSettlersScreen content = startGui();
		generateContent(argsMap, content);
	}

	/**
	 * Sets up the {@link ResourceManager} by using a configuration file. <br>
	 * First it is checked, if the given argsMap contains a "configFile" parameter. If so, the path specified for this parameter is used to get the
	 * file. <br>
	 * If the parameter is not given, the defaultConfigFile is used.
	 * 
	 * @param argsMap
	 * @param defaultConfigFileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void setupResourceManagers(HashMap<String, String> argsMap, String defaultConfigFileName) throws FileNotFoundException, IOException {
		File configFile = getConfigFile(argsMap, defaultConfigFileName);
		ConfigurationPropertiesFile config = new ConfigurationPropertiesFile(configFile);

		if (!config.isSettlersFolderSet()) {
			JFileChooser fileDialog = new JFileChooser();
			fileDialog.setAcceptAllFileFilterUsed(false);
			fileDialog.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return null;
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}
			});
			fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			fileDialog.setMultiSelectionEnabled(false);
			fileDialog.showOpenDialog(null);

			File selectedFolder = fileDialog.getSelectedFile();
			System.out.println(selectedFolder);
			try {
				config.setSettlersFolder(selectedFolder);
			} catch (IOException ex) {
				System.err.println("Wasn't able to save settings.");
				ex.printStackTrace();
			}
		}
		SwingResourceLoader.setupSwingResources(config);
	}

	public static File getConfigFile(HashMap<String, String> argsMap, String defaultConfigFileName) {
		String configFileName = defaultConfigFileName;
		if (argsMap.containsKey("config")) {
			configFileName = argsMap.get("config");
		}
		File configFil = new File(configFileName);
		return configFil;
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
		if (argsMap.containsKey("console-output")) {
			CommonConstants.ENABLE_CONSOLE_LOGGING = true;
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
	public static JSettlersScreen startGui() {
		Area area = new Area();
		JSettlersScreen content = new JSettlersScreen(new StartScreenConnector(), new SwingSoundPlayer(), BUILD);
		area.add(content.getRegion());

		startJogl(area);

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
				MapLoader mapLoader = MapLoader.getLoaderForFile(new DirectoryMapLister.ListedMapFile(new File(mapfile), false));
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
		JFrame jsettlersWnd = new JFrame("JSettlers - " + BUILD);

		// StartMenuPanel panel = new StartMenuPanel(new StartScreenConnector());
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
}
