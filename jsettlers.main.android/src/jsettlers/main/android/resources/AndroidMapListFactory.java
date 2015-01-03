package jsettlers.main.android.resources;

import java.io.File;

import android.content.res.AssetManager;
import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.IMapListFactory;
import jsettlers.logic.map.save.MapList;

public class AndroidMapListFactory implements IMapListFactory {

	private final AssetManager manager;
	private final File writeableDir;
	

	public AndroidMapListFactory(AssetManager manager, File writeableDir) {
		super();
		this.manager = manager;
		this.writeableDir = writeableDir;
	}


	@Override
	public MapList getMapList() {
		return new MapList(new AndroidAssetsMapLister(manager, "maps"),
				new DirectoryMapLister(new File(writeableDir, "save"), true));
	}

}
