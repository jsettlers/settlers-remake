package jsettlers.main.android.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.resources.IResourceProvider;
import android.content.Context;
import android.content.res.AssetManager;

public class ResourceProvider implements IResourceProvider {
	private final File saveDir;
	private final AssetManager manager;

	/**
	 * Resource directories that the user cannot change files in.
	 */
	private static final String[] fixedResources = new String[] { "images",
			"localization", "buildings" };

	public ResourceProvider(Context context, File saveDir) {
		this.saveDir = saveDir;
		manager = context.getAssets();
	}

	@Override
	public InputStream getFile(String name) throws IOException {
		String[] parts = name.split("/");
		boolean searchUserFile = true;
		for (int i = 0; i < fixedResources.length; i++) {
			if (fixedResources[i].equals(parts[0])) {
				searchUserFile = false;
			}
		}
		File file = searchUserFile ? searchFileIn(saveDir, parts) : null;

		if (searchUserFile) {
			file = searchFileIn(saveDir, parts);
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
		File outFile = new File(saveDir.getAbsolutePath() + "/" + name);
		System.err.println("--------------------------------"
				+ outFile.getAbsolutePath());
		outFile.getParentFile().mkdirs();
		return new FileOutputStream(outFile);
	}

	@Override
	public File getSaveDirectory() {
		return saveDir;
	}

	@Override
	public File getTempDirectory() {
		return saveDir;
	}

}
