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

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.WGL;
import org.lwjgl.opengl.WGLARBCreateContext;
import org.lwjgl.opengl.WGLARBCreateContextProfile;
import org.lwjgl.opengl.WGLCapabilities;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;

import go.graphics.swing.GLContainer;

public class WGLContextCreator extends JAWTContextCreator {
	private long context;

	public WGLContextCreator(GLContainer container, boolean debug) {
		super(container, debug);
		// do we have gdi and wgl support ?
		GDI32.getLibrary().getName();
	}


	private static final int[][][] ctx_attrs = new int[][][]{
		{
			{ // GL3.2+ with debugging
					WGLARBCreateContext.WGL_CONTEXT_MAJOR_VERSION_ARB, 3,
					WGLARBCreateContext.WGL_CONTEXT_MINOR_VERSION_ARB, 2,
					WGLARBCreateContextProfile.WGL_CONTEXT_PROFILE_MASK_ARB, WGLARBCreateContextProfile.WGL_CONTEXT_CORE_PROFILE_BIT_ARB,
					WGLARBCreateContext.WGL_CONTEXT_FLAGS_ARB, WGLARBCreateContext.WGL_CONTEXT_DEBUG_BIT_ARB,
					0
			},
			{ // GL3.2+
					WGLARBCreateContext.WGL_CONTEXT_MAJOR_VERSION_ARB, 3,
					WGLARBCreateContext.WGL_CONTEXT_MINOR_VERSION_ARB, 2,
					WGLARBCreateContextProfile.WGL_CONTEXT_PROFILE_MASK_ARB, WGLARBCreateContextProfile.WGL_CONTEXT_CORE_PROFILE_BIT_ARB,
					0
			}
		},

		{
			{// GL1.1+ with debugging
				WGLARBCreateContext.WGL_CONTEXT_FLAGS_ARB, WGLARBCreateContext.WGL_CONTEXT_DEBUG_BIT_ARB,
				0
			},
			{// GL1.1+
				0
			}
		},
	};

	@Override
	public void stop() {
		WGL.wglDeleteContext(context);
	}

	@Override
	protected void swapBuffers() {
		GDI32.SwapBuffers(windowDrawable);
	}

	@Override
	public void makeCurrent(boolean draw) {
		if(draw) {
			WGL.wglMakeCurrent(windowDrawable, context);
		} else {
			WGL.wglMakeCurrent(0, 0);
		}
	}

	@Override
	protected void onNewConnection() throws GLContextException {
		PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc();
		pfd.dwFlags(GDI32.PFD_DRAW_TO_WINDOW | GDI32.PFD_SUPPORT_OPENGL | GDI32.PFD_DOUBLEBUFFER);
		pfd.iPixelType(GDI32.PFD_TYPE_RGBA);
		pfd.cColorBits((byte) 32);
		pfd.cStencilBits((byte) 1);

		pfd.cDepthBits((byte) 24);

		int pixel_format = GDI32.ChoosePixelFormat(windowDrawable, pfd);
		if(pixel_format == 0) error("Could not find pixel format!");
		GDI32.SetPixelFormat(windowDrawable, pixel_format, pfd);

		pfd.free();


		context = WGL.wglCreateContext(windowDrawable);
		WGL.wglMakeCurrent(windowDrawable, context);
		WGLCapabilities caps = GL.createCapabilitiesWGL();
		if(caps.WGL_ARB_create_context && caps.WGL_ARB_create_context_profile) {
			WGL.wglDeleteContext(context);
			context = 0;

			int i = 0;
			while(context == 0 && ctx_attrs.length > i) {
				context = WGLARBCreateContext.wglCreateContextAttribsARB(windowDrawable, 0, ctx_attrs[i++][debug?0:1]);
			}
		} else if(debug) {
			WGL.wglDeleteContext(context);
			error("WGL could not create a debug context!");
		}

		if(context == 0) error("Could not create WGL context!");
		parent.wrapNewContext();
	}
}
