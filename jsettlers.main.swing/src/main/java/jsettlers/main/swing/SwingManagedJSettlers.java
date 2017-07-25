/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.MainUtils;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.localization.AbstractLabels;
import jsettlers.graphics.localization.Labels;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.DirectoryMapLister;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ReplayStartInformation;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.main.swing.foldertree.SelectSettlersFolderDialog;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeel;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;
import jsettlers.main.swing.resources.ConfigurationPropertiesFile;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.main.swing.resources.SwingResourceLoader.ResourceSetupException;
import jsettlers.network.client.OfflineNetworkConnector;

/**
 * @author codingberlin
 * @author Andreas Eberle
 */
public class SwingManagedJSettlers {
	static {
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
	}

	public static void main(String[] args) throws IOException, MapLoadException, JSettlersLookAndFeelExecption, ResourceSetupException {
		OptionableProperties optionableProperties = MainUtils.loadOptions(args);
		loadOptionalSettings(optionableProperties);
		setupResourceManagers(optionableProperties);

		JSettlersFrame settlersFrame = createJSettlersFrame();
		handleStartOptions(optionableProperties, settlersFrame);
	}

	public static void loadOptionalSettings(OptionableProperties options) {
		CommonConstants.CONTROL_ALL = options.isOptionSet("control-all");
		CommonConstants.ACTIVATE_ALL_PLAYERS = options.isOptionSet("activate-all-players");
		CommonConstants.ENABLE_CONSOLE_LOGGING = options.isOptionSet("console-output");
		CommonConstants.ENABLE_AI = !options.isOptionSet("disable-ai");
		CommonConstants.ALL_AI = options.isOptionSet("all-ai");
		CommonConstants.DISABLE_ORIGINAL_MAPS = options.isOptionSet("disable-original-maps");

		if (options.containsKey("fixed-ai-type")) {
			CommonConstants.FIXED_AI_TYPE = EPlayerType.valueOf(options.getProperty("fixed-ai-type"));
		}

		if (options.containsKey("server")) {
			CommonConstants.DEFAULT_SERVER_ADDRESS = options.getProperty("server");
		}

		if (options.containsKey("locale")) {
			String localeString = options.getProperty("locale");
			String[] localeParts = localeString.split("_");
			if (localeParts.length == 2) {
				AbstractLabels.setPreferredLocale(new Locale(localeParts[0], localeParts[1]));
			} else {
				System.err.println("Please specify the locale with language and country. (For example: de_de or en_us)");
			}
		}
	}

	/**
	 * Sets up the {@link ResourceManager} by using a configuration file. <br>
	 * First it is checked, if the given argsMap contains a "configFile" parameter. If so, the path specified for this parameter is used to get the file. <br>
	 * If the parameter is not given, the defaultConfigFile is used.
	 *
	 * @param options
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void setupResourceManagers(OptionableProperties options) throws ResourceSetupException {
		boolean wasSuccessful = false;
		boolean firstRun = true;
		while (!wasSuccessful) {
			try {
				SwingResourceLoader.setup(options);
				wasSuccessful = true;
			} catch (ResourceSetupException e) {
				// ask the user if we should continue...
				askUserToSetResources(firstRun, options);
			}
			firstRun = false;
		}
	}

	private static void askUserToSetResources(boolean firstRun, OptionableProperties options) throws ResourceSetupException {
		if (!firstRun) {
			JOptionPane.showMessageDialog(null, Labels.getString("settlers-folder-still-invalid"));
		}

		final SelectSettlersFolderDialog folderChooser = new SelectSettlersFolderDialog();
		SwingUtilities.invokeLater(() -> folderChooser.setVisible(true));

		File selectedFolder = folderChooser.waitForUserInput();
		if (selectedFolder == null) {
			String noFolderSelctedMessage = Labels.getString("error-no-settlers-3-folder-selected");
			JOptionPane.showMessageDialog(null, noFolderSelctedMessage);
			System.err.println(noFolderSelctedMessage);
			System.exit(1);
		}

		System.out.println(selectedFolder);
		try {
			ConfigurationPropertiesFile config = new ConfigurationPropertiesFile(options);
			config.setSettlersFolder(selectedFolder);
		} catch (IOException ex) {
			String errorSavingSettingsMessage = Labels.getString("error-settings-not-saveable");
			System.err.println(errorSavingSettingsMessage);
			JOptionPane.showMessageDialog(null, errorSavingSettingsMessage);
			ex.printStackTrace();
		}
		options.put("original", selectedFolder.getAbsolutePath());
	}

	private static void handleStartOptions(OptionableProperties options, JSettlersFrame settlersFrame) throws IOException, MapLoadException {
		long randomSeed = 0;
		ReplayUtils.ReplayFile loadableReplayFile = null;
		int targetGameTime = 0;

		String mapFile = options.getProperty("mapfile");
		if (options.containsKey("random")) {
			randomSeed = Long.parseLong(options.getProperty("random"));
		}
		if (options.containsKey("replayFile")) {
			String loadableReplayFileString = options.getProperty("replayFile");
			File replayFile = new File(loadableReplayFileString);
			if (replayFile.exists()) {
				loadableReplayFile = new ReplayUtils.ReplayFile(replayFile);
				System.out.println("Found loadable jsettlers.integration.replay file and loading it: " + loadableReplayFile);
			} else {
				System.err.println("Found replayFile parameter, but file can not be found!");
			}
		}
		if (options.containsKey("targetTime")) {
			targetGameTime = Integer.valueOf(options.getProperty("targetTime")) * 60 * 1000;
		}

		if (mapFile != null || loadableReplayFile != null) {
			IStartingGame game;
			if (loadableReplayFile == null) {
				MapLoader mapLoader = MapLoader.getLoaderForListedMap(new DirectoryMapLister.ListedMapFile(new File(mapFile)));
				if (mapLoader.getFileHeader().getType() == MapFileHeader.MapType.NORMAL) {
					byte playerId = 0;
					PlayerSetting[] playerSettings = PlayerSetting.createDefaultSettings(playerId, (byte) mapLoader.getMaxPlayers());
					game = new JSettlersGame(mapLoader, randomSeed, playerId, playerSettings).start();
				} else {
					MapFileHeader header = mapLoader.getFileHeader();
					game = new JSettlersGame(mapLoader, randomSeed, header.getPlayerId(), header.getPlayerSettings()).start();
				}
			} else if (options.isOptionSet("all-ai-replay")) {
				game = JSettlersGame.loadFromReplayFileAllAi(loadableReplayFile, new OfflineNetworkConnector(), new ReplayStartInformation()).start();
			} else {
				game = JSettlersGame.loadFromReplayFile(loadableReplayFile, new OfflineNetworkConnector(), new ReplayStartInformation()).start();
			}
			settlersFrame.showStartingGamePanel(game);

			if (targetGameTime > 0) {
				while (!game.isStartupFinished()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				MatchConstants.clock().fastForwardTo(targetGameTime);
			}
		}
	}

	public static IMapInterfaceConnector showJSettlers(IStartedGame startedGame) throws JSettlersLookAndFeelExecption {
		JSettlersFrame jSettlersFrame = createJSettlersFrame();
		return jSettlersFrame.showStartedGame(startedGame);
	}

	private static JSettlersFrame createJSettlersFrame() throws JSettlersLookAndFeelExecption {
		JSettlersLookAndFeel.install();
		return new JSettlersFrame();
	}
}
