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
import org.lwjgl.system.Platform;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JComboBox;

import go.graphics.swing.AreaContainer;

public class BackendSelector extends JComboBox<BackendSelector.BackendItem> {


	public static final ArrayList<BackendItem> backends = new ArrayList<>();
	public static final BackendItem DEFAULT_BACKEND = new BackendItem(null, "default");
	public static final BackendItem GLFW_BACKEND = new BackendItem(GLFWContextCreator.class, "glfw");
	public static final BackendItem GLX_BACKEND = new BackendItem(GLXContextCreator.class, "glx");
	public static final BackendItem WGL_BACKEND = new BackendItem(WGLContextCreator.class, "wgl");

	public BackendSelector() {
		setEditable(false);

		addItem(DEFAULT_BACKEND);
		addItem(GLFW_BACKEND);
		addItem(GLX_BACKEND);
		addItem(WGL_BACKEND);
	}

	public static BackendItem getBackendByName(String name) {
		for(BackendItem backend : backends) {
			if(backend.cc_name.equals(name)) return backend;
		}

		return DEFAULT_BACKEND;
	}

	public static ContextCreator createBackend(AreaContainer ac, BackendItem backend) {
		BackendItem real_backend = backend;

		if(backend == null || backend.cc_class == null) {
			Platform platform = Platform.get();
			if(platform == Platform.WINDOWS) {
				real_backend = WGL_BACKEND;
			} else if(platform == Platform.LINUX) {
				real_backend = GLX_BACKEND;
			} else {
				real_backend = GLFW_BACKEND;
			}
		}

		try {
			return real_backend.cc_class.getConstructor(AreaContainer.class).newInstance(ac);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return  null;
	}

	public static class BackendItem {

		public BackendItem(Class<? extends ContextCreator> cc_class, String cc_name) {
			this.cc_class = cc_class;
			this.cc_name = cc_name;

			backends.add(this);
		}

		public Class<? extends ContextCreator> cc_class;
		public String cc_name;

		@Override
		public String toString() {
			return cc_name;
		}
	}
}
