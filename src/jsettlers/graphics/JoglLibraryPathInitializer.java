package jsettlers.graphics;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Class to set the java library path to the system dependent jogl libraries
 * 
 * @author Andreas Eberle
 */
public final class JoglLibraryPathInitializer {
	/**
	 * no instances of this class can be created.
	 */
	private JoglLibraryPathInitializer() {
	}

	/**
	 * sets the java library path to the jogl native libraries.
	 */
	public static void initLibraryPath() {
		String arch = System.getProperty("sun.arch.data.model");
		String currentParentFolder =
		        new File(new File("").getAbsolutePath()).getParent().replace(
		                '\\', '/');
		String path =
		        currentParentFolder + "/jsettlers.deploy/libs/lib" + "/x"
		                + arch + "/";

		System.setProperty("java.library.path", path);

		Field fieldSysPath;
		try {
			fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);

			/*
			 * Explanation: At first the system property is updated with the new
			 * value. This might be a relative path or maybe you want to
			 * create that path dynamically. The Classloader has a static field
			 * (sys_paths) that contains the paths. If that field is set to
			 * null, it is initialized automatically. Therefore forcing that
			 * field to null will result into the reevaluation of the library
			 * path as soon as loadLibrary() is called.
			 */
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
