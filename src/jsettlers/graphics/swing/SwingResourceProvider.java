package jsettlers.graphics.swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.resources.IResourceProvider;

public class SwingResourceProvider implements IResourceProvider {
	private final String path;
	// we may want to use this in the future.
	//private File userPath;

	public SwingResourceProvider() {
		this(new File(new File("").getAbsolutePath()).getParent().replace(
		        '\\', '/')+ "/jsettlers.common");
	}
	
	public SwingResourceProvider(String commonsProjectDirecotry) {
		path = commonsProjectDirecotry + "/resources/";
		//userPath = new File(System.getProperty("user.home"), ".jsettlers");
		//userPath.mkdirs();
	}

	@Override
	public InputStream getFile(String name) throws IOException {
//		File file = new File(userPath.getAbsolutePath() + "/" + name);
//		if (!file.exists()) {
			File file = new File(path + name);
//		}
		return new FileInputStream(file);
	}

	@Override
	public OutputStream writeFile(String name) throws IOException {
		//In the future: save to user path
		File file = new File(path + name);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	@Override
	public File getSaveDirectory() {
		return new File(path);
	}

	@Override
    public File getTempDirectory() {
	    return new File(path);
    }

}
