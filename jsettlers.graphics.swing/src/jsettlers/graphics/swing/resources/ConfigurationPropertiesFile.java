package jsettlers.graphics.swing.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ConfigurationPropertiesFile {
	private static final String SETTLERS_FOLDER = "settlers-folder";
	private static final String SPLIT_CHARACTER = ";";

	private final File configFile;
	private final Properties properties;

	public ConfigurationPropertiesFile(File file) throws FileNotFoundException,
			IOException {
		this.configFile = file;

		Properties defaultProperties = new Properties();
		defaultProperties.load(ConfigurationPropertiesFile.class.getResourceAsStream("defaultConfig.prp"));
		this.properties = new Properties(defaultProperties);

		if (file.exists()) {
			this.properties.load(new FileInputStream(file));
		}
	}

	public File getResourcesFolder() {
		String property = properties.getProperty("resources-folder");
		File dir = new File(property);
		if (!dir.isAbsolute()) {
			String parent = configFile.getParent();
			dir = new File((parent == null ? "" : parent + File.separator)
					+ property);
		}
		return dir.getAbsoluteFile();
	}

	public String[] getGfxFolders() {
		return getFolders("GFX");
	}

	private String[] getFolders(String string) {
		String[] result = getSettlersFolderValue().split(SPLIT_CHARACTER);
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].replaceFirst("/?$", "/" + string);
		}
		return result;
	}

	private String getSettlersFolderValue() {
		return properties.getProperty(SETTLERS_FOLDER);
	}

	public String[] getSndFolders() {
		return getFolders("SND");
	}

	public boolean isSettlersFolderSet() {
		String settlersFolder = getSettlersFolderValue();
		return settlersFolder != null && settlersFolder.length() > 0 && oneExists(getGfxFolders()) && oneExists(getSndFolders());
	}

	private boolean oneExists(String[] gfxFolders) {
		for (String folder : gfxFolders) {
			if (new File(folder).exists()) {
				return true;
			}
		}
		return false;
	}

	public void setSettlersFolder(File newSettlersFolder) throws FileNotFoundException, IOException {
		properties.setProperty(SETTLERS_FOLDER, newSettlersFolder.getAbsolutePath());
		saveConfigFile("Updated settlers-folder with dialog.");
	}

	private void saveConfigFile(String updateMessage) throws IOException, FileNotFoundException {
		properties.store(new FileOutputStream(configFile), updateMessage);
	}
}
