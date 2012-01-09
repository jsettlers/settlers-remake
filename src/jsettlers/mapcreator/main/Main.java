package jsettlers.mapcreator.main;

import java.io.File;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.swing.ResourceProvider;

public class Main {
	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File(
		        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		ResourceManager.setProvider(new ResourceProvider());
	}

	public static void main(String[] args) {
		new EditorWindow(300, 300);
    }
}
