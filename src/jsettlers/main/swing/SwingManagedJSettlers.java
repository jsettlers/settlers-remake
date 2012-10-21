package jsettlers.main.swing;

import go.graphics.nativegl.NativeAreaWindow;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

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
	public static void main(String[] args) {
		ResourceManager.setProvider(new SwingResourceProvider());
		ManagedJSettlers game = new ManagedJSettlers();
		game.start(getGui());

		ImageProvider.getInstance().startPreloading();

		// NetworkTimer.loadLogging("logs/2011_11_02-11_39_44.log");
		// NetworkTimer.activateLogging("logs");
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
		        new JSettlersGame(getGui(), new MapDataMapCreator(data),
		                123456L, new NetworkManager(), (byte) 0);
		game.start();
	}

	private static ISettlersGameDisplay getGui() {
		JOGLPanel content = new JOGLPanel(new SwingSoundPlayer());

		try {
			new NativeAreaWindow(content.getArea());
		} catch (Throwable t) {
			t.printStackTrace();
			
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
		return content;
	}
}
