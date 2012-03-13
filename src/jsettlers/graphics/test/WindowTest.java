package jsettlers.graphics.test;

import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;
import go.graphics.swing.AreaContainer;

import java.io.File;

import javax.swing.JFrame;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.input.SelectionSet;

public class WindowTest {

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}

	private WindowTest() {
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));

		TestMap map = new TestMap();

		JFrame window = new JFrame("window test");

		JOGLPanel content = new JOGLPanel(new SoundPlayer() {

			@Override
			public void playSound(int sound, float lvolume, float rvolume) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setSoundDataRetriever(ISoundDataRetriever soundDataRetriever) {
				// TODO Auto-generated method stub

			}
		});
		window.add(new AreaContainer(content.getArea()));

		window.pack();
		window.setSize(500, 500);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Window content = new Window();

		MapInterfaceConnector connector = content.showGameMap(map, null);

		connector.addListener(new IMapInterfaceListener() {

			@Override
			public void action(Action action) {
				System.out.println("Action preformed: " + action.getActionType());
			}
		});

		connector.setSelection(new SelectionSet(map.getAllSettlers()));
	}

	public static void main(String[] args) {
		new WindowTest();
	}
}
