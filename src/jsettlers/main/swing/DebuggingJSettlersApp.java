package jsettlers.main.swing;

import go.graphics.swing.AreaContainer;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;

public class DebuggingJSettlersApp {
	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ResourceManager.setProvider(new ResourceProvider());
		new Thread(new SettlersGame()).start();
		// NetworkTimer.loadLogging("logs/2011_11_01-21_54_48.log");
		// NetworkTimer.activateLogging("logs");
	}

	private static class SettlersGame extends jsettlers.main.JSettlersApp {
		public SettlersGame() {
			super("single", "", false);
		}

		@Override
		protected void startGui(JOGLPanel content) {
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
}
