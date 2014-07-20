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
		return getFolders("GFX");
	}

	private String[] getFolders(String string) {
		String[] result = properties.getProperty("settlers-folder").split(SPLIT_CHARACTER);
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].replaceFirst("/?$", "/" + string);
		}
		return result;
	}

	public String[] getSndFolders() {
		return getFolders("Snd");
	}
}
