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
package jsettlers.main.swing.resources;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import jsettlers.common.resources.IResourceProvider;

public class SwingResourceProvider implements IResourceProvider {
	private final File resourcesDirectory;

	public SwingResourceProvider() {
		this.resourcesDirectory = getAppHome();
	}

	private static File getAppHome() {
		String os = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);

		String settlersHomeDirectory;
		if (os.startsWith("windows")) {
			settlersHomeDirectory = System.getenv("APPDATA") + File.separator + ".jsettlers";
		} else {
			String home = System.getProperty("user.home");
			settlersHomeDirectory = home + File.separator + ".jsettlers";
		}

		File dirFile = new File(settlersHomeDirectory);
		dirFile.mkdirs();
		return dirFile;
	}

	@Override
	public InputStream getResourcesFileStream(String name) throws IOException {
		File file = new File(resourcesDirectory, name);
		if (file.exists()) {
			return new FileInputStream(file);
		} else {
			return new ByteArrayInputStream(new byte[0]);
		}
	}

	@Override
	public OutputStream writeConfigurationFile(String name) throws IOException {
		File file = new File(resourcesDirectory, name);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	@Override
	public OutputStream writeUserFile(String name) throws IOException {
		File file = new File("." + File.separator + name);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	@Override
	public File getResourcesDirectory() {
		return resourcesDirectory;
	}
}
