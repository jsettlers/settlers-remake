/*
 * Copyright (c) 2015 - 2017
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
 */
package jsettlers.main.android.core.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;

import jsettlers.common.resources.IResourceProvider;

public class AndroidResourceProvider implements IResourceProvider {
	private final File resourcesDirectory;
	private final AssetManager manager;

	public AndroidResourceProvider(Context context, File resourcesDirectory) {
		this.resourcesDirectory = resourcesDirectory;
		manager = context.getAssets();
	}

	@Override
	public InputStream getResourcesFileStream(String name) throws IOException {
		String[] parts = name.split("/");
		File file = searchFileIn(resourcesDirectory, parts);
		if (file != null) {
			return new FileInputStream(file);
		}
		return manager.open(name);
	}

	private static File searchFileIn(File dir, String[] parts) {
		File current = dir;
		for (String part : parts) {
			if (!part.isEmpty() && !part.startsWith(".")) {
				current = new File(current, part);
			}
		}
		if (current.exists()) {
			return current;
		} else {
			return null;
		}
	}

	@Override
	public OutputStream writeConfigurationFile(String name) throws IOException {
		File outFile = new File(resourcesDirectory.getAbsolutePath() + "/" + name);
		System.err.println("Writing to: " + outFile.getAbsolutePath());
		outFile.getParentFile().mkdirs();
		return new FileOutputStream(outFile);
	}

	@Override
	public OutputStream writeUserFile(String name) throws IOException {
		return writeConfigurationFile(name); // TODO write to a file the user can access for sending us logs
	}

	@Override
	public File getResourcesDirectory() {
		return resourcesDirectory;
	}
}
