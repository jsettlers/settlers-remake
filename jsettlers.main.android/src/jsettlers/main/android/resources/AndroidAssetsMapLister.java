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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.IMapLister;
import jsettlers.logic.map.save.MapFileHeader;

public class AndroidAssetsMapLister implements IMapLister {
	public static final class AndroidAssetMap implements IListedMap {

		private AssetManager manager;
		private String path;

		public AndroidAssetMap(AssetManager manager, String path) {
			this.manager = manager;
			this.path = path;
		}

		@Override
		public String getFileName() {
			return path.replaceFirst(".*/", "");
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return manager.open(path);
		}

		@Override
		public void delete() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isCompressed() {
			return path.endsWith(MapLoader.MAP_EXTENSION_COMPRESSED);
		}

		@Override
		public File getFile() {
			throw new UnsupportedOperationException();
		}
	}

	private AssetManager manager;
	private String prefix;

	public AndroidAssetsMapLister(AssetManager manager, String prefix) {
		this.manager = manager;
		this.prefix = prefix;
	}

	@Override
	public void listMaps(IMapListerCallable callable) {
		try {
			for (String s : manager.list(prefix)) {
				if (s.endsWith(MapLoader.MAP_EXTENSION)) {
					callable.foundMap(new AndroidAssetMap(manager, prefix + "/" + s));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header) throws IOException {
		throw new UnsupportedOperationException();
	}
}
