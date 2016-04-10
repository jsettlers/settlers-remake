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
import java.util.Arrays;

import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;

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
	 * @throws ResourceDirectoryInvalidException
	 */
	public static void setup(OptionableProperties options) throws ResourceSetupException {
		String originalGamePath = options.getProperty("original");
		if (originalGamePath == null) {
			originalGamePath = loadGamePathFromConfig(options);
		}
		if (originalGamePath == null) {
			throw new ResourceSetupException("Original game path not set.");
		}

		OriginalSettlersDirectory dir = new OriginalSettlersDirectory(new File(originalGamePath));
		dir.check();

		try {
			ImageProvider imageProvider = ImageProvider.getInstance();
			imageProvider.addLookupPath(dir.getDirectory("gfx"));
			SoundManager.addLookupPath(dir.getDirectory("snd"));

			imageProvider.startPreloading();
		} catch (FileNotFoundException e) {
			throw new ResourceSetupException("Could not find gfx/snd.");
		}

		// Set the resources directory.
		File resources = options.getAppHome();
		SwingResourceProvider provider = new SwingResourceProvider(resources);

		ResourceManager.setProvider(provider);

		// Set map load paths
		MapList.DefaultMapListFactory mapList = new MapList.DefaultMapListFactory();
		mapList.addResources(resources);
		// now add original maps
		try {
			mapList.addDirectory(dir.getDirectory("map").getAbsolutePath(), false);
		} catch (FileNotFoundException e) {
		}
		String additionalMaps = options.getProperty("maps");
		if (additionalMaps != null) {
			mapList.addDirectory(additionalMaps, false);
		}
		MapList.setDefaultListFactory(mapList);
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

	/**
	 * Original game directory missing.
	 */
	public static class ResourceDirectoryInvalidException extends ResourceSetupException {
		public ResourceDirectoryInvalidException() {
		}

		public ResourceDirectoryInvalidException(String message) {
			super(message);
		}

		public ResourceDirectoryInvalidException(String message, Throwable cause) {
			super(message, cause);
		}

		public ResourceDirectoryInvalidException(Throwable cause) {
			super(cause);
		}
	}

	private static class OriginalSettlersDirectory {
		private final File dir;

		OriginalSettlersDirectory(File dir) {
			this.dir = dir;
		}

		void check() throws ResourceDirectoryInvalidException {
			try {
				File gfx = getDirectory("gfx");
				if (!Arrays.stream(DatFileType.values()).anyMatch(t -> new File(gfx, "siedler3_00" + t.getFileSuffix()).exists())) {
					throw new ResourceDirectoryInvalidException("Graphic files are missing.");
				}
				if (!new File(getDirectory("snd"), "Siedler3_00.dat").exists()) {
					throw new ResourceDirectoryInvalidException("Sound files are missing.");
				}
			} catch (FileNotFoundException e) {
				throw new ResourceDirectoryInvalidException(e);
			}
		}

		File getDirectory(String name) throws FileNotFoundException {
			if (!dir.isDirectory()) {
				throw new FileNotFoundException("Not a directory: " + dir.getAbsolutePath());
			}
			for (String found : dir.list()) {
				if (found.equalsIgnoreCase(name)) {
					return new File(dir, found);
				}
			}
			throw new FileNotFoundException("Could not find " + name + " in " + dir.getAbsolutePath());
		}
	}
}
