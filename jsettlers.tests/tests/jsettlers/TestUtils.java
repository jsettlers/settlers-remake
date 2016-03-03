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

import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
import jsettlers.graphics.swing.resources.SwingResourceLoader;

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
			setupResourcesManager();
			SwingResourceLoader.setupGraphicsAndSoundResources(getDefaultConfigFile());
		} catch (IOException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	public static synchronized void setupResourcesManager() {
		try {
			SwingResourceLoader.setupResourcesManager(getDefaultConfigFile());
		} catch (IOException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	private static ConfigurationPropertiesFile getDefaultConfigFile() throws IOException {
		File configFile = new File("../jsettlers.main.swing/config.prp");
		if (!configFile.exists()) {
			throw new IOException("Default config file not found at " + configFile.getAbsolutePath());
		}
		return new ConfigurationPropertiesFile(configFile);
	}
}
