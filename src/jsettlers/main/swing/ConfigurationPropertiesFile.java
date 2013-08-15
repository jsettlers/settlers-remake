package jsettlers.main.swing;

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

	public ConfigurationPropertiesFile(File file) throws FileNotFoundException, IOException {
		properties.load(new FileInputStream(file));
	}

	public String getResourcesFolder() {
		return properties.getProperty("resources-folder");
	}

	public String[] getGfxFolders() {
		return properties.getProperty("gfx-folder").split(SPLIT_CHARACTER);
	}

	public String[] getSndFolders() {
		return properties.getProperty("snd-folder").split(SPLIT_CHARACTER);
	}
}
