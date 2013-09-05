package jsettlers.graphics.swing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class just loads the resources and sets up paths needed for jsettlers when used with a swing UI.
 * 
 * @author michael
 * @author Andreas Eberle
 * 
 */
public class SwingResourceLoader {

	public static void setupResourceManagersByConfigFile(File file) throws FileNotFoundException, IOException {
		ConfigurationPropertiesFile configFile = new ConfigurationPropertiesFile(file);
		setupResourceManagersByConfigFile(configFile);
	}

	public static void setupResourceManagersByConfigFile(ConfigurationPropertiesFile configFile) throws IOException {
		testConfig(configFile);

		ImageProvider provider = ImageProvider.getInstance();
		for (String gfxFolder : configFile.getGfxFolders()) {
			provider.addLookupPath(new File(gfxFolder));
		}

		for (String sndFolder : configFile.getSndFolders()) {
			SoundManager.addLookupPath(new File(sndFolder));
		}

		ResourceManager.setProvider(new SwingResourceProvider(configFile.getResourcesFolder()));
	}

	private static void testConfig(ConfigurationPropertiesFile cf)
			throws IOException {
		if (!isResourceDir(cf.getResourcesFolder())) {
			throw new IOException("Not a resources folder: " + cf.getResourcesFolder() + " in " + new File("").getAbsolutePath());
		}

		boolean hasSndDir = false;
		boolean hasGfxDir = false;

		for (String sndFolder : cf.getSndFolders()) {
			hasSndDir = hasSndDir || new File(sndFolder, "Siedler3_00.dat").exists();
		}

		for (String gfxFolder : cf.getGfxFolders()) {
			hasGfxDir = hasGfxDir || new File(gfxFolder, "siedler3_00.7c003e01f.dat").exists();
		}

		if (!hasSndDir || !hasGfxDir) {
			throw new InvalidSettlersDirectoryException(cf.getSndFolders(), cf.getGfxFolders(), true);
		}
	}

	private static boolean isResourceDir(File dir) {
		return new File(new File(dir, "images"), "movables.txt").exists();
	}
}
