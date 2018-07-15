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

import org.lwjgl.opengl.WGL;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;

import go.graphics.swing.GLContainer;

public class WGLContextCreator extends JAWTContextCreator {

	private JAWTWin32DrawingSurfaceInfo win32surfaceinfo;
	private long hwnd;
	private long hdc;
	private long context;
	private int pixel_format;

	public WGLContextCreator(GLContainer container) {
		super(container);
		// do we have gdi and wgl support ?
		GDI32.getLibrary().getName();
	}


	@Override
	public void stop() {
		WGL.wglDeleteContext(context);
	}

	@Override
	protected void swapBuffers() {
		GDI32.SwapBuffers(hdc);
	}

	@Override
	protected void makeCurrent(boolean draw) {
		if(draw) {
			WGL.wglMakeCurrent(hdc, context);
		} else {
			WGL.wglMakeCurrent(0, 0);
		}
	}

	@Override
	protected void initContext() {
		hwnd = win32surfaceinfo.hwnd();
		hdc = win32surfaceinfo.hdc();


		PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc();
		pfd.dwFlags(GDI32.PFD_DRAW_TO_WINDOW | GDI32.PFD_SUPPORT_OPENGL | GDI32.PFD_DOUBLEBUFFER);
		pfd.iPixelType(GDI32.PFD_TYPE_RGBA);
		pfd.cColorBits((byte) 32);
		pfd.cStencilBits((byte) 1);

		pfd.cDepthBits((byte) 24);

		pixel_format = GDI32.ChoosePixelFormat(hdc, pfd);
		if(pixel_format == 0) throw new Error("Could not find pixel format!");
		GDI32.SetPixelFormat(hdc, pixel_format, pfd);

		pfd.free();

		context = WGL.wglCreateContext(hdc);
		if(context == 0) throw new Error("Could not create WGL context!");
	}

	@Override
	protected void createNewSurfaceInfo() {
		win32surfaceinfo = JAWTWin32DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
		hwnd = win32surfaceinfo.hwnd();
		hdc = win32surfaceinfo.hdc();
	}
}
