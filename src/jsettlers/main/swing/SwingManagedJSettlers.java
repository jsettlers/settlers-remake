package jsettlers.main.swing;

import java.awt.Dimension;
import java.io.FileNotFoundException;

import go.graphics.swing.AreaContainer;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.main.ManagedJSettlers;
import jsettlers.main.ManagedJSettlers.IGuiStarter;

import javax.swing.JFrame;

public class SwingManagedJSettlers implements IGuiStarter {

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
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
		ResourceManager.setProvider(new ResourceProvider());
		ManagedJSettlers game = new ManagedJSettlers();

		game.start(new SwingManagedJSettlers());

		// NetworkTimer.loadLogging("logs/2011_11_02-11_39_44.log");
		// NetworkTimer.activateLogging("logs");
	}

	public void startGui(JOGLPanel content) {
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
}
