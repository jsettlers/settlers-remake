package jsettlers.graphics.swing.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ConfigurationPropertiesFile {
	private static final String SPLIT_CHARACTER = ";";

	private final Properties properties = new Properties();

	private final File configFile;

	public ConfigurationPropertiesFile(File file) throws FileNotFoundException, IOException {
		this.configFile = file;
		properties.load(new FileInputStream(file));
	}

	public File getResourcesFolder() {
		String property = properties.getProperty("resources-folder");
		File dir = new File(property);
		if (!dir.isAbsolute()) {
			String parent = configFile.getParent();
			dir = new File((parent == null ? "" : parent + File.separator) + property);
		}
		return dir.getAbsoluteFile();
	}

	public String[] getGfxFolders() {
		return getFolders("GFX", "gfx", "Gfx");
	}

	private String[] getFolders(String... subfolders) {
		String[] settlersFolder = properties.getProperty("settlers-folder").split(SPLIT_CHARACTER);
		String[] result = new String[settlersFolder.length * subfolders.length];

		int resultIdx = 0;
		for (int subfolderIdx = 0; subfolderIdx < subfolders.length; subfolderIdx++) {
			for (int folderIdx = 0; folderIdx < settlersFolder.length; folderIdx++) {
				result[resultIdx++] = settlersFolder[folderIdx].replaceFirst("/?$", "/" + subfolders[subfolderIdx]);
			}
		}
		return result;
	}

	public String[] getSndFolders() {
		return getFolders("SND", "snd", "Snd");
	}
}
