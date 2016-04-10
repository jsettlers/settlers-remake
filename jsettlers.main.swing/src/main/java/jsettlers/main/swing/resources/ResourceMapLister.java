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
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.DirectoryMapLister;
import jsettlers.logic.map.loading.list.IMapLister;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.list.MapList.DefaultMapListFactory;
import jsettlers.logic.map.loading.list.MapList.ListedResourceMap;

public class ResourceMapLister extends DefaultMapListFactory {

	public ResourceMapLister() {
		CodeSource source = getClass().getProtectionDomain().getCodeSource();
		System.out.println("Source: " + source);
		if (source != null) {
			URL jar = source.getLocation();
			boolean hasJarExt = jar.getFile().endsWith(".jar");
			if (hasJarExt || "jar".equalsIgnoreCase(jar.getProtocol())) {
				directories.add(createJarList(jar));
			} else if ("file".equalsIgnoreCase(jar.getProtocol())) {
				directories.add(createDevList(jar));
			}
		}
	}

	private IMapLister createJarList(URL jar) {
		return new IMapLister() {
			@Override
			public void listMaps(IMapListerCallable callable) {
				try {
					ZipInputStream zip = new ZipInputStream(jar.openStream());
					while (true) {
						ZipEntry e = zip.getNextEntry();
						if (e == null) {
							break;
						}
						String path = "/" + e.getName();
						System.out.println("Entry: " + path);
						if (path.startsWith("/jsettlers/resources/maps") && path.endsWith(MapLoader.MAP_EXTENSION)) {
							callable.foundMap(new ListedResourceMap(path));
						}
					}
				} catch (IOException e) {
					// currently silently ignored. TODO: Error management.
					System.out.println("Ignore map list error: " + e.getMessage());
				}
			}

			@Override
			public OutputStream getOutputStream(MapFileHeader header) throws IOException {
				throw new UnsupportedOperationException();
			}
		};
	}

	private IMapLister createDevList(URL jar) {
		try {
			String dir = new File(jar.toURI()).getAbsolutePath() + "/../../resources/main/jsettlers/resources/maps";
			System.out.println("Attempt to use default maps: " + dir);
			return new DirectoryMapLister(new File(dir), false);
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
