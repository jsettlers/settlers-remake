package jsettlers.graphics.swing.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.resources.IResourceProvider;

public class SwingResourceProvider implements IResourceProvider {
	private final String resourcesFolder;

	public SwingResourceProvider(File file) {
		this.resourcesFolder = file.getPath() + "/";
	}

	@Override
	public InputStream getFile(String name) throws IOException {
		File file = new File(resourcesFolder + name);

		return new FileInputStream(file);
	}

	@Override
	public OutputStream writeFile(String name) throws IOException {
		File file = new File(resourcesFolder + name);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	@Override
	public File getSaveDirectory() {
		return new File(resourcesFolder);
	}

	@Override
	public File getTempDirectory() {
		return new File(resourcesFolder);
	}
}
