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

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLX;
import org.lwjgl.opengl.GLX13;
import org.lwjgl.opengl.GLXARBCreateContext;
import org.lwjgl.opengl.GLXCapabilities;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.linux.XVisualInfo;

import go.graphics.swing.GLContainer;


public class GLXContextCreator extends JAWTContextCreator {
	private long context = 0;

	public GLXContextCreator(GLContainer container, boolean debug) {
		super(container, debug);
		// do we have xlib support ?
		X11.getLibrary().getName();
	}


	@Override
	protected void onInit() {
		int screen = X11.XDefaultScreen(windowConnection);

		int[] xvi_attrs = new int[]{
				GLX.GLX_RGBA,
				GLX.GLX_DOUBLEBUFFER,
				GLX.GLX_STENCIL_SIZE, 1,
				0};

		if(debug) {
			GLXCapabilities glxcaps = GL.createCapabilitiesGLX(windowConnection, screen);
			if(!glxcaps.GLX13 || !glxcaps.GLX_ARB_create_context) throw new Error("GLX could not create a debug context!");

			PointerBuffer fbc = GLX13.glXChooseFBConfig(windowConnection, screen, new int[] {0});
			if(fbc == null || fbc.capacity() < 1) throw new Error("GLX could not find any FBConfig!");

			context = GLXARBCreateContext.glXCreateContextAttribsARB(windowConnection, fbc.get(), 0, true, new int[] {
					GLXARBCreateContext.GLX_CONTEXT_FLAGS_ARB, GLXARBCreateContext.GLX_CONTEXT_DEBUG_BIT_ARB, 0
			});
		} else {
			XVisualInfo xvi = GLX.glXChooseVisual(windowConnection, screen, xvi_attrs);
			context = GLX.glXCreateContext(windowConnection, xvi, 0, true);
		}
		if (context == 0) throw new Error("Could not create GLX context!");
		parent.wrapNewContext();
	}

	@Override
	public void stop() {
		GLX.glXDestroyContext(windowConnection, context);
	}

	@Override
	protected void swapBuffers() {
		GLX.glXSwapBuffers(windowConnection, windowDrawable);
	}

	@Override
	public void makeCurrent(boolean draw) {
		if(draw) {
			GLX.glXMakeCurrent(windowConnection, windowDrawable, context);
		} else {
			GLX.glXMakeCurrent(windowConnection, 0, 0);
		}
	}
}
