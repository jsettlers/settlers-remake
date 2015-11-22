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
package jsettlers.graphics.swing.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.resources.IResourceProvider;

public class SwingResourceProvider implements IResourceProvider {
	private final String resourcesFolder;

	public SwingResourceProvider(File file) {
		this.resourcesFolder = file.getPath() + file.separator;
	}

	@Override
	public InputStream getFile(String name) throws IOException {
		File file = new File(resourcesFolder + name);

		return new FileInputStream(file);
	}

	@Override
	public OutputStream writeFile(String name) throws IOException {
		File file = new File(resourcesFolder + name);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	@Override
	public File getSaveDirectory() {
		return new File(resourcesFolder);
	}

	@Override
	public File getTempDirectory() {
		return new File(resourcesFolder);
	}
}
