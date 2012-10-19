package jsettlers.graphics.swing;

import java.io.File;

import jsettlers.common.CommonConstants;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class just loads the resources and sets up paths needed for jsettlers.
 * @author michael
 *
 */
public class SwingResourceLoader {

	public static void setupSwingPaths() {
	    JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.jsettlers/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));

		SoundManager.addLookupPath(new File("/home/michael/.jsettlers/Snd"));
		SoundManager.addLookupPath(new File("D:/Games/Siedler3/Snd"));
		SoundManager.addLookupPath(new File("C:/Program Files/siedler 3/Snd"));

		CommonConstants.ENABLE_DEBUG_COLORS = true;
    }

}
