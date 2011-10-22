package jsettlers.main.swing;

import go.graphics.swing.AreaContainer;

import java.awt.Dimension;

import javax.swing.JFrame;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;

public class JSettlersApp {
	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}
	
	public static void main(String[] args) {
		new Thread(new SettlersGame()).start();
	}

	private static class SettlersGame extends jsettlers.main.JSettlersApp {
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
