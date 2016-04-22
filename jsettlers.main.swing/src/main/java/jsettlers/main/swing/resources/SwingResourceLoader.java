/*******************************************************************************
 * Copyright (c) 2015
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.list.MapList.DefaultMapListFactory;
import jsettlers.main.swing.resources.SettlersFolderChecker.SettlersFolderInfo;

/**
 * This class just loads the resources and sets up paths needed for jsettlers when used with a swing UI.
 *
 * @author michael
 * @author Andreas Eberle
 *
 */
public class SwingResourceLoader {

	/**
	 * Attempts to set up the settlers environment.
	 * <p>
	 * After return from this method, you can be sure that the paths to the original graphics have been set and all map loaders are set uo.
	 * </p>
	 *
	 * @param options
	 *            The command line options.
	 * @throws ResourceSetupException
	 */
	public static void setup(OptionableProperties options) throws ResourceSetupException {
		SettlersFolderInfo settlersFolderInfo = getPathOfOriginalSettlers(options);

		// setup image and sound provider
		ImageProvider.getInstance().addLookupPath(settlersFolderInfo.gfxFolder).startPreloading();
		SoundManager.addLookupPath(settlersFolderInfo.sndFolder);

		// Set the resources directory.
		File resources = options.getAppHome();
		ResourceManager.setProvider(new SwingResourceProvider(resources));

		// Setup map load paths
		setupMapListFactory(options, settlersFolderInfo);
	}

	private static SettlersFolderInfo getPathOfOriginalSettlers(OptionableProperties options) throws ResourceSetupException {
		String originalGamePath = options.getProperty("original");
		if (originalGamePath == null) {
			originalGamePath = loadGamePathFromConfig(options);
		}

		SettlersFolderInfo settlersFolderInfo = SettlersFolderChecker.checkSettlersFolder(originalGamePath);
		if(!settlersFolderInfo.isValidSettlersFolder()){
			throw new ResourceSetupException("Path to original Settlers III installation not valid.");
		}

		return settlersFolderInfo;
	}

	private static void setupMapListFactory(OptionableProperties options, SettlersFolderInfo settlersFolderInfo) {
		DefaultMapListFactory mapListFactory = new DefaultMapListFactory();
		loadDefaultMapFolders(mapListFactory);

		// now add original maps
		if(settlersFolderInfo.mapsFolder != null 	){
			mapListFactory.addMapDirectory(settlersFolderInfo.mapsFolder.getAbsolutePath(), false);
		}

		String additionalMaps = options.getProperty("maps");
		if (additionalMaps != null) {
			mapListFactory.addMapDirectory(additionalMaps, false);
		}

		MapList.setDefaultListFactory(mapListFactory);
	}

	private static void loadDefaultMapFolders(DefaultMapListFactory mapList) {
		mapList.addResourcesDirectory(new File("."));

		// Maps contained in jar file?
		ResourceMapLister resourceLister = ResourceMapLister.getDefaultLister();
		if (resourceLister != null) {
			mapList.addMapDirectory(resourceLister);
		}
	}

	private static String loadGamePathFromConfig(OptionableProperties options) throws ResourceSetupException {
		try {
			ConfigurationPropertiesFile config = new ConfigurationPropertiesFile(options);
			return config.getSettlersFolderValue();
		} catch (IOException e) {
			throw new ResourceSetupException("Could not load config file.");
		}
	}

	public static class ResourceSetupException extends Exception {
		public ResourceSetupException() {
		}

		public ResourceSetupException(String message) {
			super(message);
		}

		public ResourceSetupException(String message, Throwable cause) {
			super(message, cause);
		}

		public ResourceSetupException(Throwable cause) {
			super(cause);
		}
	}
}
