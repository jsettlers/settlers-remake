package jsettlers.main.swing;

import go.graphics.area.Area;
import go.graphics.nativegl.NativeAreaWindow;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import jsettlers.common.CommonConstants;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.progress.StartingGamePanel;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.LogicRevision;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.main.JSettlersGame;
import jsettlers.main.StartScreenConnector;

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
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		List<String> argsList = Arrays.asList(args);

		loadDebugSettings(argsList);

		setupResourceManagersByConfigFile();

		JSettlersScreen content = startGui(argsList);
		generateContent(argsList, content);

		ImageProvider.getInstance().startPreloading();
	}

	public static void setupResourceManagersByConfigFile()
			throws FileNotFoundException, IOException {
		ConfigurationPropertiesFile configFile = new ConfigurationPropertiesFile(new File("config.prp"));

		ImageProvider provider = ImageProvider.getInstance();
		for (String gfxFolder : configFile.getGfxFolders()) {
			provider.addLookupPath(new File(gfxFolder));
		}

		for (String sndFolder : configFile.getSndFolders()) {
			SoundManager.addLookupPath(new File(sndFolder));
		}

		ResourceManager.setProvider(new SwingResourceProvider(configFile.getResourcesFolder()));
	}

	private static void loadDebugSettings(List<String> argsList) {
		if (argsList.contains("--control-all")) {
			CommonConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR = true;
			CommonConstants.ENABLE_ALL_PLAYER_SELECTION = true;
			CommonConstants.ENABLE_FOG_OF_WAR_DISABLING = true;
		}
		if (argsList.contains("-localhost") || argsList.contains("--localhost")) {
			CommonConstants.DEFAULT_SERVER_ADDRESS = "localhost";
		}
	}

	/**
	 * Creates a new SWING GUI for the game.
	 * 
	 * @param argsList
	 * @return
	 */
	public static JSettlersScreen startGui(List<String> argsList) {
		Area area = new Area();
		JSettlersScreen content = new JSettlersScreen(new StartScreenConnector(), new SwingSoundPlayer());
		area.add(content.getRegion());

		if (argsList.contains("--force-jogl")) {
			startJogl(area);
		} else if (argsList.contains("--force-native")) {
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

	private static void generateContent(List<String> argsList, JSettlersScreen content) throws IOException {
		String mapfile = null;
		long randomSeed = 0;
		File loadableReplayFile = null;

		for (String s : argsList) {
			if (s.startsWith("--mapfile=")) {
				mapfile = s.replaceFirst("--mapfile=", "");
			}
			if (s.startsWith("--random=")) {
				randomSeed = Long.parseLong(s.replaceFirst("--random=", ""));
			}

			if (s.startsWith("--replayFile=")) {
				String loadableReplayFileString = s.replaceFirst("--replayFile=", "");
				File replayFile = new File(loadableReplayFileString);
				if (replayFile.exists()) {
					loadableReplayFile = replayFile;
					System.out.println("Found loadable replay file and loading it: " + loadableReplayFile);
				} else {
					System.err.println("Found replayFile parameter, but file can not be found!");
				}
			}
		}

		if (mapfile != null || loadableReplayFile != null) {
			IStartingGame game;
			if (loadableReplayFile == null) {
				MapLoader mapLoader = new MapLoader(new File(mapfile));
				game = new JSettlersGame(mapLoader, randomSeed, (byte) 0).start();
			} else {
				game = JSettlersGame.loadFromReplayFile(loadableReplayFile).start();
			}
			StartingGamePanel toDisplay = new StartingGamePanel(game, content);
			content.setContent(toDisplay);
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
