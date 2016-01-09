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
	private static final String SPLIT_CHARACTER = ";";

	private final File configFile;
	private final Properties properties;

	public ConfigurationPropertiesFile(File file) throws FileNotFoundException, IOException {
		this.configFile = file;

		Properties defaultProperties = new Properties();
		defaultProperties.load(ConfigurationPropertiesFile.class.getResourceAsStream("defaultConfig.prp"));
		this.properties = new Properties(defaultProperties);

		if (file.exists()) {
			this.properties.load(new FileInputStream(file));
		}
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

	private String getSettlersFolderValue() {
		return properties.getProperty(SETTLERS_FOLDER);
	}

	public File getOriginalSettlersDirectory() {
		return new File(getSettlersFolderValue());
	}

	public String[] getSndFolders() {
		return getFolders("SND", "snd", "Snd");
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

	public File getOriginalMapsDirectory() {
		return new File(getOriginalSettlersDirectory(), "Map");
	}
}
