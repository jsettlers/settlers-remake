package jsettlers.main.swing;

import go.graphics.area.Area;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import jsettlers.common.CommitInfo;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.localization.Labels;
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

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MapLoadException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, MapLoadException {
		HashMap<String, String> argsMap = MainUtils.createArgumentsMap(args);

		loadDebugSettings(argsMap);
		setupResourceManagers(argsMap, "config.prp");

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
		ConfigurationPropertiesFile configFile = getConfigFile(argsMap, defaultConfigFileName);
		SwingResourceLoader.setupResourcesManager(configFile);

		if (!configFile.isSettlersFolderSet()) {
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
			fileDialog.setDialogTitle(Labels.getString("select-settlers-3-folder"));
			fileDialog.showOpenDialog(null);

			File selectedFolder = fileDialog.getSelectedFile();
			if (selectedFolder == null) {
				String noFolderSelctedMessage = Labels.getString("error-no-settlers-3-folder-selected");
				JOptionPane.showMessageDialog(null, noFolderSelctedMessage);
				System.err.println(noFolderSelctedMessage);
				System.exit(1);
			}

			System.out.println(selectedFolder);
			try {
				configFile.setSettlersFolder(selectedFolder);
			} catch (IOException ex) {
				String errorSavingSettingsMessage = Labels.getString("error-settings-not-saveable");
				System.err.println(errorSavingSettingsMessage);
				JOptionPane.showMessageDialog(null, errorSavingSettingsMessage);
				ex.printStackTrace();
			}
		}
		SwingResourceLoader.setupGraphicsAndSoundResources(configFile);
	}

	public static ConfigurationPropertiesFile getConfigFile(HashMap<String, String> argsMap, String defaultConfigFileName) throws IOException {
		String configFileName = defaultConfigFileName;
		if (argsMap.containsKey("config")) {
			configFileName = argsMap.get("config");
		}
		return new ConfigurationPropertiesFile(new File(configFileName));
	}

	public static void loadDebugSettings(HashMap<String, String> argsMap) {
		CommonConstants.CONTROL_ALL = argsMap.containsKey("control-all");
		CommonConstants.ACTIVATE_ALL_PLAYERS = argsMap.containsKey("activate-all-players");
		CommonConstants.ENABLE_CONSOLE_LOGGING = argsMap.containsKey("console-output");

		if (argsMap.containsKey("localhost")) {
			CommonConstants.DEFAULT_SERVER_ADDRESS = "localhost";
		}

		if (argsMap.containsKey("locale")) {
			String localeString = argsMap.get("locale");
			String[] localeParts = localeString.split("_");
			if (localeParts.length == 2) {
				Labels.preferredLocale = new Locale(localeParts[0], localeParts[1]);
			} else {
				System.err.println("Please specify the locale with language and country. (For example: de_de or en_us)");
			}
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
		JSettlersScreen content = new JSettlersScreen(new StartScreenConnector(), new SwingSoundPlayer(), getBuild());
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
		JFrame jsettlersWnd = new JFrame("JSettlers - " + getBuild());

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

	private static String getBuild() {
		return Labels.getString("version-build", CommitInfo.COMMIT_HASH_SHORT);
	}
}
