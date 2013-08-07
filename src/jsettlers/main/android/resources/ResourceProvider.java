package jsettlers.main.android.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.resources.IResourceProvider;
import android.content.Context;

public class ResourceProvider implements IResourceProvider {
	private final File[] dirs;
	private final ResourceUpdater updater;

	public ResourceProvider(Context context, File[] dirs) {
		this.dirs = dirs;
		this.updater = new ResourceUpdater(context, dirs[0]);
		new Thread(updater, "resource updater").start();
	}

	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	public void startUpdate(final UpdateListener listener) {
		updater.startUpdate(listener);
	}

	@Override
	public InputStream getFile(String name) throws IOException {
		try {
			this.updater.waitUntilUpdateFinished();
		} catch (InterruptedException e) {
		}
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
		File outFile = new File(dirs[0].getAbsolutePath() + "/" + name);
		System.err.println("--------------------------------"
		        + outFile.getAbsolutePath());
		outFile.getParentFile().mkdirs();
		return new FileOutputStream(outFile);
	}

	@Override
	public File getSaveDirectory() {
		return dirs[0];
	}

	@Override
	public File getTempDirectory() {
		return dirs[0];
	}

}
