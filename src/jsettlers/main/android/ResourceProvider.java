package jsettlers.main.android;

import java.io.IOException;
import java.io.InputStream;

import android.content.res;

import jsettlers.common.resources.IResourceProvider;

public class ResourceProvider implements IResourceProvider {

	public ResourceProvider() {
		assetmanager = new AssetManager();
	}
	
	@Override
    public InputStream getFile(String name) throws IOException {
	    return assetmanager.open(name);
    }

}
