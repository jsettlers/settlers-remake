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
package jsettlers.main.android.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import jsettlers.common.resources.IResourceProvider;

public class ResourceProvider implements IResourceProvider {
	private final File resourcesDirectory;
	private final AssetManager manager;

	/**
	 * Resource directories that the user cannot change files in.
	 */
	private static final String[] fixedResources = new String[] { "images",
			"localization", "buildings" };

	public ResourceProvider(Context context, File resourcesDirectory, File jsettlersDirectory) {
		this.resourcesDirectory = resourcesDirectory;
		manager = context.getAssets();
	}

	@Override
	public InputStream getResourcesFileStream(String name) throws IOException {
		String[] parts = name.split("/");
		boolean searchUserFile = true;
		for (int i = 0; i < fixedResources.length; i++) {
			if (fixedResources[i].equals(parts[0])) {
				searchUserFile = false;
			}
		}
		File file = searchUserFile ? searchFileIn(resourcesDirectory, parts) : null;

		if (searchUserFile) {
			file = searchFileIn(resourcesDirectory, parts);
			if (file != null) {
				return new FileInputStream(file);
			}
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
	public OutputStream writeFile(String name) throws IOException {
		File outFile = new File(resourcesDirectory.getAbsolutePath() + "/" + name);
		System.err.println("--------------------------------"
				+ outFile.getAbsolutePath());
		outFile.getParentFile().mkdirs();
		return new FileOutputStream(outFile);
	}

	@Override
	public File getResourcesDirectory() {
		return resourcesDirectory;
	}

	@Override
	public File getOriginalMapsDirectory() {
		// TODO Auto-generated method stub
		return null;
	}
}
