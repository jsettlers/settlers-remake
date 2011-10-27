package jsettlers.common.resources;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the resource manager. It gives you resources.
 * 
 * @author michael
 */
public class ResourceManager {
	private static IResourceProvider provider = null;

	public static void setProvider(IResourceProvider provider) {
		ResourceManager.provider = provider;
	}

	public static InputStream getFile(String filename) throws IOException {
		if (provider != null) {
			return provider.getFile(filename);
		} else {
			throw new IOException("No resource provider set.");
		}
	}
}
