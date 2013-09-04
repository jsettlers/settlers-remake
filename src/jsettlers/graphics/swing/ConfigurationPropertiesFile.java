package jsettlers.graphics.swing;

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
