/*******************************************************************************
 * Copyright (c) 2015 - 2018
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

import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.common.resources.SettlersFolderChecker.SettlersFolderInfo;
import jsettlers.graphics.image.reader.DatFileUtils;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.list.MapList.DefaultMapListFactory;
import jsettlers.main.swing.settings.SettingsManager;

import java.io.File;

/**
 * This class just loads the resources and sets up paths needed for jsettlers when used with a swing UI.
 *
 * @author michael
 * @author Andreas Eberle
 */
public class SwingResourceLoader {

	/**
	 * Attempts to set up the settlers environment.
	 * <p>
	 * After return from this method, you can be sure that the paths to the original graphics have been set and all map loaders are set uo.
	 * </p>
	 *
	 * @throws ResourceSetupException
	 * 		Is thrown if resources cannot be loaded.
	 */
	public static void setup() throws ResourceSetupException {
		SettlersFolderInfo settlersFolders = getPathOfOriginalSettlers();

		String settlersVersionId = SettingsManager.getInstance().getSettlersVersionId();
		if (settlersVersionId == null) {
			settlersVersionId = DatFileUtils.generateOriginalVersionId(settlersFolders.gfxFolder);
			SettingsManager.getInstance().setSettlersVersionId(settlersVersionId);
		}

		// setup image and sound provider
		ImageProvider.setLookupPath(settlersFolders.gfxFolder, settlersVersionId);
		SoundManager.setLookupPath(settlersFolders.sndFolder);

		// Setup map load paths
		setupMapListFactory(SettingsManager.getInstance().getAdditionalMapsDirectory(), settlersFolders.mapsFolder);
	}

	private static SettlersFolderInfo getPathOfOriginalSettlers() throws ResourceSetupException {
		String originalGamePath = SettingsManager.getInstance().getSettlersFolder();

		SettlersFolderInfo settlersFolderInfo = SettlersFolderChecker.checkSettlersFolder(originalGamePath);
		if (!settlersFolderInfo.isValidSettlersFolder()) {
			throw new ResourceSetupException("Path to original Settlers III installation not valid.");
		}

		return settlersFolderInfo;
	}

	public static void setupMapListFactory(String additionalMaps, File originalMapsFolder) {
		DefaultMapListFactory mapListFactory = new DefaultMapListFactory();
		loadDefaultMapFolders(mapListFactory);

		// now add original maps
		if (originalMapsFolder != null) {
			mapListFactory.addMapDirectory(originalMapsFolder.getAbsolutePath(), false);
		}

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
