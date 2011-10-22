package jsettlers.graphics.test;

import go.graphics.swing.AreaContainer;

import java.io.File;

import javax.swing.JFrame;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.map.selection.SettlerSelection;

public class WindowTest {

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}

	private WindowTest() {
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File(
		        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));

		provider.preload(0);
		provider.preload(10);
		provider.preload(1);

		TestMap map = new TestMap();

		JFrame window = new JFrame("window test");

		JOGLPanel content = new JOGLPanel();
		window.add(new AreaContainer(content.getArea()));

		window.pack();
		window.setSize(500, 500);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Window content = new Window();

		MapInterfaceConnector connector = content.showHexMap(map, null);

		connector.addListener(new IMapInterfaceListener() {

			@Override
			public void action(Action action) {
				System.out.println("Action preformed: "
				        + action.getActionType());
			}
		});
		connector.setSelection(new SettlerSelection(map.getAllSettlers()));
	}

	public static void main(String[] args) {
		new WindowTest();
	}
}
