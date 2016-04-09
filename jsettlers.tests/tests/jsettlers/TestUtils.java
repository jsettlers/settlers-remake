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
package jsettlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.list.MapList.ListedResourceMap;
import jsettlers.logic.map.loading.newmap.RemakeMapLoader;
import jsettlers.main.replay.ReplayUtils.ReplayMapFileProvider;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.main.swing.resources.ResourceMapLister;

/**
 * Utility class holding methods needed by serveral test classes.
 * 
 * @author Andreas Eberle
 * 
 */
public class TestUtils {

	public static <T> T serializeAndDeserialize(T object) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteOutStream);

		oos.writeObject(object);
		oos.close();

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteOutStream.toByteArray()));

		@SuppressWarnings("unchecked")
		T readList = (T) ois.readObject();
		ois.close();

		return readList;
	}

	public static synchronized void setupSwingResources() {
		try {
			SwingResourceLoader.setupResourcesByConfigFile(getDefaultConfigFile());
			MapList.setDefaultListFactory(new ResourceMapLister());
		} catch (IOException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	public static synchronized void setupResourcesManager() {
		try {
			SwingResourceLoader.setupResourcesManager(getDefaultConfigFile());
			MapList.setDefaultListFactory(new ResourceMapLister());
		} catch (IOException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	private static ConfigurationPropertiesFile getDefaultConfigFile() throws IOException {
		File directory = new File("../jsettlers.main.swing");
		ConfigurationPropertiesFile configFile = SwingManagedJSettlers.createDefaultConfigFile(directory);
		if (!configFile.isLoadedFromFile()) {
			throw new IOException("Default config file not found at " + directory);
		}
		return configFile;
	}

	public static RemakeMapLoader getMap(String idWithoutExtensio) throws MapLoadException {
		String name = "/jsettlers/tests/maps/" + idWithoutExtensio + MapLoader.MAP_EXTENSION_COMPRESSED;
		Object compressed = TestUtils.class.getResource(name);
		if (compressed == null) {
			name = "/jsettlers/tests/maps/" + idWithoutExtensio + MapLoader.MAP_EXTENSION;
			Object uncompressed = TestUtils.class.getResource(name);
			if (uncompressed == null) {
				throw new MapLoadException("Could not find the map " + idWithoutExtensio);
			}
		}

		ListedResourceMap file = new ListedResourceMap(name);
		try {
			return (RemakeMapLoader) MapLoader
					.getLoaderForListedMap(file);
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
	}

	public static ReplayMapFileProvider getMapProvider() {
		return new ReplayMapFileProvider() {
			@Override
			public MapLoader getMap(String id) {
				// try {
				return new ResourceMapLister().getMapList().getMapById(id);
				// } catch (MapLoadException e) {
				// throw new RuntimeException(e);
				// }
			}

		};
	}
}
