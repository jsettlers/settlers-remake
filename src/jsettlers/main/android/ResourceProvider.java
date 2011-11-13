package jsettlers.main.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jsettlers.common.resources.IResourceProvider;

public class ResourceProvider implements IResourceProvider {
	private final File[] dirs;

	public ResourceProvider(File[] dirs) {
		this.dirs = dirs;
	}

	@Override
	public InputStream getFile(String name) throws IOException {
		String[] parts = name.split("/");
		for (File dir : dirs) {
			File found = searchFileIn(dir, parts);
			if (found != null) {
				System.out.println("Found file in " + dir.getAbsolutePath());
				return new FileInputStream(found);
			}
		}
		System.err.println("File " + name
		        + " not found. Place it in JSettlers dir!");
		throw new IOException("File " + name
		        + " not found. Place it in JSettlers dir!");
	}

	private File searchFileIn(File dir, String[] parts) {
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

}
