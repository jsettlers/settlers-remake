package jsettlers.common.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	/**
	 * Gets the file. Throws a io exception if the file does not exist.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static InputStream getFile(String filename) throws IOException {
		if (provider != null) {
			return provider.getFile(filename);
		} else {
			throw new IOException("No resource provider set.");
		}
	}

	public static OutputStream writeFile(String filename) throws IOException {
		if (provider != null) {
			return provider.writeFile(filename);
		} else {
			throw new IOException("No resource provider set.");
		}
	}

	/**
	 * Gets a directory where all the content that is being created should be
	 * saved.
	 * 
	 * @return The directory.
	 */
	public static File getSaveDirectory() {
		if (provider != null) {
			return provider.getSaveDirectory();
		} else {
			return new File("");
		}
	}

	public static File getTempDirectory() {
		if (provider != null) {
			return provider.getTempDirectory();
		} else {
			return new File("");
		}
    }
}
