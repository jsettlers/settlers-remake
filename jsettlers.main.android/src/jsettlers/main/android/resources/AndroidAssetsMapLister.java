package jsettlers.main.android.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.IMapLister;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;

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

	}

	private AssetManager manager;
	private String prefix;

	public AndroidAssetsMapLister(AssetManager manager, String prefix) {
		this.manager = manager;
		this.prefix = prefix;
	}

	@Override
	public void getMaps(IMapListerCallable callable) {
		try {
			for (String s : manager.list(prefix)) {
				if (s.endsWith(MapList.MAP_EXTENSION)) {
					callable.foundMap(new AndroidAssetMap(manager, prefix + "/" + s));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
