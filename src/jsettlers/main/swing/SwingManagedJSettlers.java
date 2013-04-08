package jsettlers.main.swing;

import go.graphics.nativegl.NativeAreaWindow;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.IMapDataProvider;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.JOGLPanel;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.main.JSettlersGame;
import jsettlers.main.ManagedJSettlers;
import jsettlers.main.MapDataMapCreator;
import network.NetworkManager;

public class SwingManagedJSettlers {

	static { // sets the native library path for the system dependent jogl libs
		SwingResourceLoader.setupSwingPaths();
	}

	/**
	 * @param args
	 *            args can have no entries or <br>
	 *            args[0] must be "host" or "client"
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
	        IOException, ClassNotFoundException {
		List<String> argsList = Arrays.asList(args);

		loadDebugSettings(argsList);

		ResourceManager.setProvider(new SwingResourceProvider());
		ManagedJSettlers game = new ManagedJSettlers();
		game.start(getGui(argsList));

		ImageProvider.getInstance().startPreloading();

		// NetworkTimer.loadLogging("logs/2012_12_19-07_44_01.log");
		// NetworkTimer.activateLogging("logs");
	}

	private static void loadDebugSettings(List<String> argsList) {
		if (argsList.contains("--control-all")) {
			CommonConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR = true;
			CommonConstants.ENABLE_ALL_PLAYER_SELECTION = true;
		}
	}

	/**
	 * Directly starts a map window
	 * 
	 * @param mapname
	 */
	public static void startMap(IMapDataProvider data) {
		ResourceManager.setProvider(new SwingResourceProvider());
		// TODO: detect exit
		JSettlersGame game =
		        new JSettlersGame(getGui(Collections.<String> emptyList()),
		                new MapDataMapCreator(data), 123456L,
		                new NetworkManager(), (byte) 0);
		game.start();
	}

	/**
	 * Creates a new SWING GUI for the game.
	 * 
	 * @param argsList
	 * @return
	 */
	public static ISettlersGameDisplay getGui(List<String> argsList) {
		JOGLPanel content = new JOGLPanel(new SwingSoundPlayer());

		if (argsList.contains("--force-jogl")) {
			startJogl(content);
		} else if (argsList.contains("--force-native")) {
			startNative(content);
		} else {
			try {
				startNative(content);
			} catch (Throwable t) {
				startJogl(content);
			}
		}
		return content;
	}

	private static void startJogl(JOGLPanel content) {
		SwingResourceLoader.setupSwingPaths();

		JFrame jsettlersWnd = new JFrame("jsettlers");
		AreaContainer panel = new AreaContainer(content.getArea());
		panel.setPreferredSize(new Dimension(640, 480));
		jsettlersWnd.add(panel);
		panel.requestFocusInWindow();

		jsettlersWnd.pack();
		jsettlersWnd.setSize(1200, 800);
		jsettlersWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jsettlersWnd.setVisible(true);
		jsettlersWnd.setLocationRelativeTo(null);
	}

	private static void startNative(JOGLPanel content) {
		new NativeAreaWindow(content.getArea());
	}
}
