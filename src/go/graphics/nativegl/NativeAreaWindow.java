package go.graphics.nativegl;

import go.graphics.GLDrawContext;
import go.graphics.area.Area;

import java.io.File;
import java.lang.reflect.Field;

public class NativeAreaWindow {

	private Area content;

	private GLDrawContext glWrapper = new NativeGLWrapper();

	private final class OpenGLThread implements Runnable {
		@Override
		public void run() {
			openWindow_native();
		}
	}

	public NativeAreaWindow(Area content) {
		this.content = content;
		loadLibrary();
		new Thread(new OpenGLThread(), "gl").start();
	}

	private void loadLibrary() {
		String currentParentFolder =
		        new File(new File("").getAbsolutePath()).getParent().replace(
		                '\\', '/');
		String path = currentParentFolder + "/go.graphics.nativegl/nativegl/";

		System.setProperty("java.library.path", path);

		Field fieldSysPath;
		try {
			fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.loadLibrary("go.graphics.nativegl");
	}

	private native void openWindow_native();

	private void drawContent() {
		System.out.println("drawing");
		
		content.drawArea(glWrapper);
	}

	private void resizeTo(int width, int height) {
		System.out.println("resized");
		content.setWidth(width);
		content.setHeight(height);
	}

	public static void main(String[] args) {
		new NativeAreaWindow(new Area());
	}
}
