package go.graphics.nativegl;

import go.graphics.GLDrawContext;
import go.graphics.area.Area;

import java.io.File;
import java.lang.reflect.Field;

public class NativeAreaWindow {

	private Area content;

	private GLDrawContext glWrapper = new IndirectBufferLoadableGlWrapper();

	private final NativeEventConverter eventConverter;

	private int height;

	private final class OpenGLThread implements Runnable {
		@Override
		public void run() {
			openWindow_native();
		}
	}

	public NativeAreaWindow(Area content) {
		this.content = content;
		eventConverter = new NativeEventConverter(content);
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
		content.drawArea(glWrapper);
	}

	private void resizeTo(int width, int height) {
		this.height = height;
		content.setWidth(width);
		content.setHeight(height);
	}

	private void mouseStateChanged(int button, boolean down, int x, int y) {
		if (down) {
			eventConverter.mouseDown(button, x, height - y);
		} else {
			eventConverter.mouseUp(button, x, height - y);
		}
	}

	private void keyPressed(String key, boolean up) {
		if (up) {
			eventConverter.keyReleased(key);
		} else {
			eventConverter.keyPressed(key);
		}
	}

	private void mousePositionChanged(int x, int y) {
		eventConverter.mousePositionChanged(x, height - y);
	}

	private void mouseInsideWindow(boolean inside) {
		eventConverter.mouseInsideWindow(inside);
	}

	public static void main(String[] args) {
		new NativeAreaWindow(new Area());
	}
}
