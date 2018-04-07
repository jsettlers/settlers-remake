/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.list.MapList.DefaultMapListFactory;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.main.swing.resources.SwingResourceProvider;
import jsettlers.testutils.map.DebugMapLister;

/**
 * Utility class holding methods needed by serveral test classes.
 *
 * @author Andreas Eberle
 *
 */
public class TestUtils {

	public static <T> T serializeAndDeserialize(T object) throws IOException, ClassNotFoundException {
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

	public static synchronized void setupResourceManager() {
		try {
			SwingResourceLoader.setup(new OptionableProperties());
		} catch (SwingResourceLoader.ResourceSetupException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	public static void setupTempResourceManager() {
		try {
			File tempDirectory = Files.createTempDirectory("saves").toFile();
			tempDirectory.deleteOnExit();
			System.out.println("Using temp resource manager with directory: " + tempDirectory);

			ResourceManager.setProvider(new SwingResourceProvider(tempDirectory));

			DefaultMapListFactory mapListFactory = new DefaultMapListFactory();
			mapListFactory.addResourcesDirectory(new File(tempDirectory, "save"), new File(tempDirectory, "maps"));
			mapListFactory.addSaveDirectory(new DebugMapLister(new File(tempDirectory, "save"), true));
			MapList.setDefaultListFactory(mapListFactory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
