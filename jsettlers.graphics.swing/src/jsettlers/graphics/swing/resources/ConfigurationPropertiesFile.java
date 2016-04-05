/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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

	private final File configFile;
	private final Properties properties;

	private final boolean loadedFromFile;

	public ConfigurationPropertiesFile(File file) throws FileNotFoundException, IOException {
		this(file, null);
	}

	public ConfigurationPropertiesFile(File file, File templateFile) throws FileNotFoundException, IOException {
		this.configFile = file;

		Properties defaultProperties = new Properties();
		defaultProperties.load(ConfigurationPropertiesFile.class.getResourceAsStream("defaultConfig.prp"));
		this.properties = new Properties(defaultProperties);

		boolean loaded = false;
		if (templateFile != null && templateFile.exists()) {
			this.properties.load(new FileInputStream(templateFile));
			loaded = true;
		}

		if (file.exists()) {
			this.properties.load(new FileInputStream(file));
			loaded = true;
		}
		this.loadedFromFile = loaded;
	}

	public File getResourcesDirectory() {
		String property = properties.getProperty("resources-folder");
		File dir = new File(property);
		if (!dir.isAbsolute()) {
			String parent = configFile.getParent();
			dir = new File((parent == null ? "" : parent + File.separator)
					+ property);
		}
		return dir.getAbsoluteFile();
	}

	public String getSettlersFolderValue() {
		return properties.getProperty(SETTLERS_FOLDER);
	}

	public File getOriginalSettlersDirectory() {
		return new File(getSettlersFolderValue());
	}

	public boolean isValidSettlersFolderSet() {
		String settlersFolder = getSettlersFolderValue();
		return SettlersFolderChecker.checkSettlersFolder(settlersFolder).isValidSettlersFolder();
	}

	public void setSettlersFolder(File newSettlersFolder) throws FileNotFoundException, IOException {
		properties.setProperty(SETTLERS_FOLDER, newSettlersFolder.getAbsolutePath());
		saveConfigFile("Updated settlers-folder with dialog.");
	}

	private void saveConfigFile(String updateMessage) throws IOException, FileNotFoundException {
		properties.store(new FileOutputStream(configFile), updateMessage);
	}

	public File getOriginalMapsDirectory() {
		return new File(getOriginalSettlersDirectory(), "Map");
	}

	/**
	 * @return <code>true</code> if one of the settings or the template files has been used.
	 */
	public boolean isLoadedFromFile() {
		return loadedFromFile;
	}
}
