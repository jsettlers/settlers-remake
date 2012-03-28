package jsettlers;

import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;
import go.graphics.swing.AreaContainer;

import java.io.File;

import javax.swing.JFrame;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.main.swing.ResourceProvider;

public class TestWindow {
	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));

		SoundManager.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/Snd"));
		SoundManager.addLookupPath(new File("D:/Games/Siedler3/Snd"));
		SoundManager.addLookupPath(new File("C:/Program Files/siedler 3/Snd"));
	}

	public static MapInterfaceConnector openTestWindow(IGraphicsGrid map) {
		ResourceManager.setProvider(new ResourceProvider());

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
		window.setSize(1300, 800);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MapInterfaceConnector connector = content.showGameMap(map, null);

		connector.addListener(new IMapInterfaceListener() {

			@Override
			public void action(Action action) {
				System.out.println("Action preformed: " + action.getActionType());
			}
		});

		return connector;
	}
}
