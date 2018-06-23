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

import org.lwjgl.system.jawt.JAWT;
import org.lwjgl.system.jawt.JAWTDrawingSurface;
import org.lwjgl.system.jawt.JAWTDrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTFunctions;

import java.awt.Canvas;
import java.awt.Graphics;

import go.graphics.swing.GLContainer;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;

public abstract class JAWTContextCreator extends ContextCreator {

	protected JAWT jawt = JAWT.create();
	protected JAWTDrawingSurface surface;
	protected JAWTDrawingSurfaceInfo surfaceinfo;

	public JAWTContextCreator(GLContainer container) {
		super(container);

		jawt.version(JAWTFunctions.JAWT_VERSION_1_4);
		JAWTFunctions.JAWT_GetAWT(jawt);

	}

	@Override
	public abstract void stop();

	@Override
	public void initSpecific() {
		canvas = new Canvas() {
			@Override
			public void update(Graphics graphics) {
				if (isShowing()) paint(graphics);
			}

			public void paint(Graphics graphics) {
				surface = JAWTFunctions.JAWT_GetDrawingSurface(jawt.GetDrawingSurface(), canvas);

				JAWTFunctions.JAWT_DrawingSurface_Lock(surface.Lock(), surface);
				surfaceinfo = JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo(surface.GetDrawingSurfaceInfo(), surface);
				createNewSurfaceInfo();

				if (first_draw) {
					new GOSwingEventConverter(this, parent);

					initContext();
					makeCurrent(true);

					parent.init();

					first_draw = false;
				}
				makeCurrent(true);

				synchronized (wnd_lock) {
					if (change_res) {
						width = new_width;
						height = new_height;

						parent.resize_gl(width, height);
						change_res = false;
					}
				}

				parent.draw();

				swapBuffers();
				makeCurrent(false);
				JAWTFunctions.JAWT_DrawingSurface_Unlock(surface.Unlock(), surface);

				JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo(surface.FreeDrawingSurfaceInfo(), surfaceinfo);
				JAWTFunctions.JAWT_FreeDrawingSurface(jawt.FreeDrawingSurface(), surface);
			}
		};
	}

	protected abstract void swapBuffers();

	protected abstract void makeCurrent(boolean draw);

	protected abstract void initContext();

	protected abstract void createNewSurfaceInfo();

	@Override
	public void repaint() {
		canvas.repaint();
	}

	@Override
	public void requestFocus() {
		canvas.requestFocus();

	}
}
