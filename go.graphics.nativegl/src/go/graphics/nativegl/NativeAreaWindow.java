/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
