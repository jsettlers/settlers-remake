package jsettlers.graphics.swing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class just loads the resources and sets up paths needed for jsettlers.
 * 
 * @author michael
 * @author Andreas Eberle
 * 
 */
public class SwingResourceLoader {

	static final String[] commonSettlersPaths = new String[] { "APPDIR",
			"C:/Program Files/siedler 3", "D:/Program Files/siedler 3", "." };

	static final String[] commonResourcePaths = new String[] {
			"APPDIR/resources/", "resources/", "../jsettlers.common/resources/" };

	public static void setupSwingPaths() {
		try {
			setupDefaultPaths();
		} catch (SettlersDirectoryException e) {
			String label;
			if (e.isBadVersion()) {
				label = Labels.getString("bad-folders-version");
			} else {
				File hintFile = new File(e.getExpectedSnd().getParentFile(),
						Labels.getString("place-folders-hint"));
				try {
					hintFile.createNewFile();
				} catch (Throwable t) {
				}
				label = Labels.getString("place-folders-at");
			}
			String message = String.format(label, e.getExpectedGfx(),
					e.getExpectedSnd());
			JOptionPane.showMessageDialog(null, message);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
		}
	}

	public static void setupDefaultPaths() throws IOException {
		File appdir = new File(System.getProperty("user.home"), ".jsettlers");
		if (!appdir.exists()) {
			appdir.mkdirs();
		}
		File config = new File(appdir, "config.prp");
		if (!config.exists()) {
			generateConfig(appdir, config);
		}
		ConfigurationPropertiesFile cf = new ConfigurationPropertiesFile(config);
		setupResourceManagersByConfigFile(cf);
		testConfig(cf);
	}

	private static void testConfig(ConfigurationPropertiesFile cf)
			throws IOException {
		if (!isResourceDir(new File(cf.getResourcesFolder()))) {
			throw new IOException("Not a resources folder: " + cf.getResourcesFolder() + " in " + new File("").getAbsolutePath());
		}
		File gfxDir = new File(cf.getGfxFolders()[0]);
		File sndDir = new File(cf.getSndFolders()[0]);
		if (!new File(gfxDir, "siedler3_00.7c003e01f.dat").exists()) {
			throw new SettlersDirectoryException(sndDir, gfxDir, false);
		}
		if (!new File(sndDir, "Siedler3_00.dat").exists()) {
			throw new SettlersDirectoryException(sndDir, gfxDir, false);
		}
		if (!new File(gfxDir, "siedler3_40.7c003e01f.dat").exists()) {
			throw new SettlersDirectoryException(sndDir, gfxDir, true);
		}

	}

	private static void generateConfig(File appdir, File config)
			throws IOException {
		File settlersDir = findSettlersDir(appdir).getAbsoluteFile();
		File resourcesDir = findResourcesDir(appdir).getAbsoluteFile();

		PrintWriter out = new PrintWriter(config);
		out.println("resources-folder=" + resourcesDir.toString());
		out.println("settlers-folder=" + settlersDir.toString());
		out.close();
	}

	private static File findResourcesDir(File appdir) {
		for (String path : commonResourcePaths) {
			File dir = new File(
					path.replace("APPDIR", appdir.getAbsolutePath()));
			if (isResourceDir(dir)) {
				return dir;
			}
		}

		return new File(appdir, "resources");
	}

	private static boolean isResourceDir(File dir) {
		return new File(new File(dir, "images"), "movables.txt").exists();
	}

	private static File findSettlersDir(File appdir) {
		for (String path : commonSettlersPaths) {
			File dir = new File(
					path.replace("APPDIR", appdir.getAbsolutePath()));
			if (isSettlersDir(dir)) {
				return dir;
			}
		}

		return appdir;
	}

	private static boolean isSettlersDir(File dir) {
		return new File(new File(dir, "GFX"), "siedler3_00.7c003e01f.dat")
				.exists();
	}

	public static void setupResourceManagersByConfigFile(File file)
			throws FileNotFoundException, IOException {
		ConfigurationPropertiesFile configFile = new ConfigurationPropertiesFile(
				file);
		setupResourceManagersByConfigFile(configFile);
	}

	public static void setupResourceManagersByConfigFile(
			ConfigurationPropertiesFile configFile)
			throws FileNotFoundException, IOException {

		ImageProvider provider = ImageProvider.getInstance();
		for (String gfxFolder : configFile.getGfxFolders()) {
			provider.addLookupPath(new File(gfxFolder));
		}

		for (String sndFolder : configFile.getSndFolders()) {
			SoundManager.addLookupPath(new File(sndFolder));
		}

		ResourceManager.setProvider(new SwingResourceProvider(configFile
				.getResourcesFolder()));
	}

}
