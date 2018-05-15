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

public enum EBackendType implements Comparable<EBackendType> {

	GLX(GLXContextCreator.class, "glx", null, Platform.LINUX),
	WGL(WGLContextCreator.class, "wgl", Platform.WINDOWS, Platform.WINDOWS),
	JOGL(JOGLContextCreator.class, "jogl", Platform.MACOSX, Platform.MACOSX),

	GLFW(GLFWContextCreator.class, "glfw", null, null),
	DEFAULT(null, "default", null, null);

	EBackendType(Class<? extends ContextCreator> cc_class, String cc_name, Platform platform, Platform default_for) {
		this.cc_class = cc_class;
		this.cc_name = cc_name;
		this.platform = platform;
		this.default_for = default_for;
	}

	public Class<? extends ContextCreator> cc_class;
	public Platform platform, default_for;
	public String cc_name;

	@Override
	public String toString() {
		return cc_name;
	}
}
