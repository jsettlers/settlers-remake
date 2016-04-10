/*******************************************************************************
 * Copyright (c) 2016
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A special resource loader that looks in the jar file as well.
 * 
 * @author Michael Zangl
 */
public class JarSwingResourceProvider extends SwingResourceProvider {
	public JarSwingResourceProvider() {
		super(getDefaultResourceDirectory());
	}

	@Override
	public InputStream getResourcesFileStream(String name) throws IOException {
		String packedName = "/jsettlers/resources/" + name;
		InputStream res = getClass().getResourceAsStream(packedName);
		if (res != null) {
			return res;
		}
		return super.getResourcesFileStream(name);
	}

	private static File getDefaultResourceDirectory() {
		File dir = new File(System.getProperty("user.home"), ".jsettlers");
		dir.mkdirs();
		return dir;
	}

	public static SwingResourceProvider getBestAvailable(File fallbackResources) throws IOException {
		boolean available = JarSwingResourceProvider.class
				.getResource("/jsettlers/resources/use") != null;
		if (available) {
			return new JarSwingResourceProvider();
		} else {
			System.out.println("Falling back to resource directory " + fallbackResources.getAbsolutePath());
			fallbackResources.mkdirs();
			return new SwingResourceProvider(fallbackResources);
		}
	}
}
