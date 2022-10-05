/*******************************************************************************
 * Copyright (c) 2018
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
package go.graphics.swing.contextcreator;

import org.lwjgl.system.Platform;
import org.lwjgl.system.jawt.JAWT;
import org.lwjgl.system.jawt.JAWTDrawingSurface;
import org.lwjgl.system.jawt.JAWTDrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTFunctions;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTX11DrawingSurfaceInfo;

import java.awt.Canvas;
import java.awt.Graphics;

import go.graphics.swing.ContextContainer;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;

public abstract class JAWTContextCreator extends ContextCreator {

	protected JAWT jawt = JAWT.create();
	protected JAWTDrawingSurface surface;
	protected JAWTDrawingSurfaceInfo surfaceinfo;
	protected long windowConnection;
	protected long windowDrawable;
	protected Platform currentPlatform = Platform.get();

	public JAWTContextCreator(ContextContainer container, boolean debug) {
		super(container, debug);

		jawt.version(JAWTFunctions.JAWT_VERSION_1_4);
		JAWTFunctions.JAWT_GetAWT(jawt);

	}

	@Override
	public abstract void stop();

	private void regenerateWindowInfo() throws ContextException {
		long oldWindowConnection = windowConnection;
		long oldWindowDrawable = windowDrawable;
		if(currentPlatform == Platform.LINUX) {
			JAWTX11DrawingSurfaceInfo dsi = JAWTX11DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
			windowConnection = dsi.display();
			windowDrawable = dsi.drawable();
		} else {
			JAWTWin32DrawingSurfaceInfo dsi = JAWTWin32DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
			windowConnection = dsi.hwnd();
			windowDrawable = dsi.hdc();
		}

		if(windowDrawable != oldWindowDrawable) onNewDrawable();
		if(windowConnection != oldWindowConnection) onNewConnection();
	}

	protected void onNewConnection() throws ContextException {}
	protected void onNewDrawable() throws ContextException {}

	protected void onInit() throws ContextException {}

	@Override
	public void initSpecific() {
		canvas = new Canvas() {
			@Override
			public void update(Graphics graphics) {
				if (isShowing()) paint(graphics);
			}

			public void paint(Graphics graphics) {
				surface = JAWTFunctions.JAWT_GetDrawingSurface(canvas, jawt.GetDrawingSurface());
				JAWTFunctions.JAWT_DrawingSurface_Lock(surface, surface.Lock());
				surfaceinfo = JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo(surface, surface.GetDrawingSurfaceInfo());
				try {
					regenerateWindowInfo();

					if (first_draw) {
						first_draw = false;
						new GOSwingEventConverter(this, parent);

						onInit();
					}
					makeCurrent(true);

					synchronized (wnd_lock) {
						if (change_res) {
							width = new_width;
							height = new_height;

							parent.resizeContext(width, height);
							change_res = false;
						}
					}

					try {
						parent.draw();
						parent.finishFrame();
					} finally {
						try {
							swapBuffers();
						} finally {
							makeCurrent(false);
						}
					}
				} catch(ContextException ignored) {
				} catch (Throwable thrown) {
					thrown.printStackTrace();
				}

				if (fpsLimit == 0) repaint();

				JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo(surfaceinfo, surface.FreeDrawingSurfaceInfo());
				JAWTFunctions.JAWT_DrawingSurface_Unlock(surface, surface.Unlock());
				JAWTFunctions.JAWT_FreeDrawingSurface(surface, jawt.FreeDrawingSurface());
			}
		};
	}

	protected abstract void swapBuffers() throws ContextException;

	public abstract void makeCurrent(boolean draw);
}
