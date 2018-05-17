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

import org.lwjgl.egl.EGL;
import org.lwjgl.opengl.GLX;
import org.lwjgl.opengl.WGL;
import org.lwjgl.system.Platform;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.windows.GDI32;


public enum EBackendType implements Comparable<EBackendType> {

	DEFAULT(null, "default", null, null, null, null),

	GLX(GLXContextCreator.class, "glx", null, Platform.LINUX, X11.class, null),
	EGL(EGLContextCreator.class, "egl", null, null, org.lwjgl.egl.EGL.class, "getFunctionProvider"),
	WGL(WGLContextCreator.class, "wgl", null, Platform.WINDOWS, GDI32.class, null),
	JOGL(JOGLContextCreator.class, "jogl", Platform.MACOSX, Platform.MACOSX, null, null),

	GLFW(GLFWContextCreator.class, "glfw", null, null, null, null);

	EBackendType(Class<? extends ContextCreator> cc_class, String cc_name, Platform platform, Platform default_for, Class<?> probe_class, String probe_method) {
		this.cc_class = cc_class;
		this.cc_name = cc_name;
		this.platform = platform;
		this.default_for = default_for;
		this.probe_class = probe_class;
		this.probe_method = probe_method;
	}

	public Class<? extends ContextCreator> cc_class;
	public Platform platform, default_for;
	public String cc_name;
	private Class<?> probe_class;
	private String probe_method;

	@Override
	public String toString() {
		return cc_name;
	}

	public boolean available(Platform platform) {
		if(probe_class != null) {
			try {
				probe_class.getDeclaredMethod(probe_method != null ? probe_method : "getLibrary").invoke(null);
			} catch (Throwable thrown) {
				return false;
			}
		} else if(this.platform != null){
			return this.platform == platform;
		}

		return true;
	}
}
