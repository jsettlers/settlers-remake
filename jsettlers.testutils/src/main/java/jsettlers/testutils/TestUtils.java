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
package jsettlers.testutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jsettlers.common.resources.IResourceProvider;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.map.loading.list.IMapLister;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.newmap.MapFileHeader;

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

	public static synchronized void setupResourcesManager() {
		try {
			SwingResourceLoader.setup(new OptionableProperties());
		} catch (SwingResourceLoader.ResourceSetupException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	/**
	 * Sets up a resource manager that only uses memory stored files.
	 */
	public static void setupMemoryResourceManager() {
		final MemoryResourceProvider resourceProvider = new MemoryResourceProvider();
		ResourceManager.setProvider(resourceProvider);

		MapList.DefaultMapListFactory d = new MapList.DefaultMapListFactory();
		d.addSaveDirectory(resourceProvider);
		MapList.setDefaultListFactory(d);
	}

	private static class MemoryResourceProvider implements IResourceProvider, IMapLister {
		private Map<String, ByteArrayOutputStream> files = new HashMap<>();
		private int savegame = 0;

		@Override
		public InputStream getResourcesFileStream(String name) throws IOException {
			ByteArrayOutputStream out = files.get(name);
			if (out != null) {
				return new ByteArrayInputStream(out.toByteArray());
			} else {
				return null;
			}
		}

		@Override
		public OutputStream writeFile(String name) throws IOException {
			System.out.println("Writing file " + name);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			files.put(name, out);
			return out;
		}

		@Override
		public File getResourcesDirectory() {
			return null;
		}

		@Override
		public void listMaps(IMapListerCallable callable) {
			System.out.println("Scanning maps... ");
			for (Map.Entry<String, ByteArrayOutputStream> e : files.entrySet()) {
				System.out.println("Scanning map " + e.getKey());
				findMap(callable, e.getKey());
			}
		}

		private void findMap(IMapListerCallable callable, final String key) {
			if (MapLoader.isExtensionKnown(key)) {
				callable.foundMap(new IListedMap() {
					@Override
					public String getFileName() {
						return key;
					}

					@Override
					public InputStream getInputStream() throws IOException {
						return getResourcesFileStream(getFileName());
					}

					@Override
					public void delete() {
						files.remove(key);
					}

					@Override
					public boolean isCompressed() {
						return getFileName().endsWith(MapLoader.MAP_EXTENSION_COMPRESSED);
					}

					@Override
					public File getFile() {
						throw new UnsupportedOperationException();
					}
				});
			}
		}

		@Override
		public OutputStream getOutputStream(MapFileHeader header) throws IOException {
			savegame++;
			return writeFile("savegame-" + savegame + MapLoader.MAP_EXTENSION);
		}
	}
}
