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
import java.io.IOException;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.swing.resources.SettlersFolderChecker.SettlersFoldersResult;

/**
 * This class just loads the resources and sets up paths needed for jsettlers when used with a swing UI.
 * 
 * @author michael
 * @author Andreas Eberle
 * 
 */
public class SwingResourceLoader {

	public static void setupResourcesByConfigFile(ConfigurationPropertiesFile configFile) throws IOException {
		setupGraphicsAndSoundResources(configFile);
		setupResourcesManager(configFile);
	}

	public static void setupGraphicsAndSoundResources(ConfigurationPropertiesFile configFile) throws IOException {
		SettlersFoldersResult settlersFoldersResult = testSettlersFolderConfig(configFile);

		ImageProvider imageProvider = ImageProvider.getInstance();
		imageProvider.addLookupPath(settlersFoldersResult.gfxFolder);

		SoundManager.addLookupPath(settlersFoldersResult.sndFolder);

		imageProvider.startPreloading();
	}

	public static void setupResourcesManager(ConfigurationPropertiesFile configFile) {
		ResourceManager.setProvider(new SwingResourceProvider(configFile.getResourcesDirectory(), configFile.getOriginalMapsDirectory()));
	}

	private static SettlersFoldersResult testSettlersFolderConfig(ConfigurationPropertiesFile configFile)
			throws IOException {
		if (!isResourceDir(configFile.getResourcesDirectory())) {
			throw new IOException("Not a resources folder: " + configFile.getResourcesDirectory() + " in " + new File("").getAbsolutePath());
		}

		SettlersFoldersResult settlersFoldersResult = SettlersFolderChecker.checkSettlersFolder(configFile.getSettlersFolderValue());
		if (!settlersFoldersResult.isValidSettlersFolder()) {
			StringBuilder err = new StringBuilder();
			if (settlersFoldersResult.gfxFolder == null) {
				err.append("graphics (GFX) folder not found / empty");
			}

			if (settlersFoldersResult.sndFolder != null) {
				if (err.length() != 0) {
					err.append(" and ");
				}

				err.append("sound (SND) folder not found / empty");
			}

			throw new InvalidSettlersDirectoryException(err.toString());
		}

		return settlersFoldersResult;
	}

	private static boolean isResourceDir(File dir) {
		return new File(new File(dir, "images"), "movables.txt").exists();
	}
}
