package jsettlers.graphics.swing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class loads the jogl libs
 * 
 * @author michael
 */
public class JoglLoader {
	private static final String[] LIBNAMES = new String[] { "gluegen-rt", "jogl_desktop", "jogl_es1", "jogl_es2", "nativewindow_awt", "newt" };
	private static Queue<File> loadFiles = null;

	public static void loadLibs() {
		if (loadFiles == null) {
			loadFiles = new ConcurrentLinkedQueue<File>();

			for (String libname : LIBNAMES) {
				try {
					loadSingleLib(libname);
				} catch (IOException e) {
					System.err.println("Problems loading lib " + libname);
				}
			}
		}
	}

	private static void loadSingleLib(String file) throws IOException {
		File temporary = File.createTempFile("jogl-" + file, "." + getExtension());

		String filename = getLibDir() + System.mapLibraryName(file);
		InputStream in = JoglLoader.class.getResourceAsStream(filename);

		if (in == null) {
			throw new IOException("could not load lib: not found");
		}

		FileOutputStream out = new FileOutputStream(temporary);

		int n;
		byte buffer[] = new byte[4096];
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}

		in.close();
		out.close();

		System.load(temporary.getAbsolutePath());
		temporary.deleteOnExit();
	}

	private static String getLibDir() {
		return "../../../libs/lib/";
	}

	private static String getExtension() {
		return ".so";
	}

	// private static void addUnloadTask() {
	// Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	// public void run() {
	// }
	// }, "jogl cleanup task"));
	// }

}
