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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import java.awt.Window;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import go.graphics.UIPoint;
import go.graphics.event.interpreter.AbstractEventConverter;
import go.graphics.swing.GLContextContainer;

public class GLFWContextCreator extends AsyncContextCreator {

	private final GLFWEventConverter event_converter;

	public GLFWContextCreator(GLContainer container, boolean debug) {
		super(container, debug);
		event_converter = new GLFWEventConverter();
	}

	private long glfw_wnd;
	private GLFWErrorCallback ec;

	public void async_init() {
		if(debug) {
			ec = GLFWErrorCallback.createPrint(System.err);
			GLFW.glfwSetErrorCallback(ec);
		}

		if(!GLFW.glfwInit()) throw new Error("glfwInit() failed!");

		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, debug ? GLFW.GLFW_TRUE : GLFW.GLFW_DONT_CARE);
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 1);
		synchronized (wnd_lock) {
			glfw_wnd = GLFW.glfwCreateWindow(width, height, "lwjgl-offscreen", 0, 0);
			GLFW.glfwMakeContextCurrent(glfw_wnd);
			GLFW.glfwSwapInterval(0);
			parent.wrapNewContext();
			try {
				parent.resizeContext(width, height);
			} catch (GLContextException e) {
				e.printStackTrace();
			}
		}
	}

		event_converter.registerCallbacks();
	}

	public void async_set_size(int width, int height) {
		GLFW.glfwSetWindowSize(glfw_wnd, width, height);
	}

	private long glfw_resize_time = -1;
	private int glfw_width, glfw_height;

	public void async_refresh() {
		if(glfw_resize_time != -1) {
			if(glfw_resize_time+10 <= System.currentTimeMillis()) {
				Window wnd = SwingUtilities.windowForComponent(canvas);
				int dw = wnd.getWidth()-canvas.getWidth();
				int dh = wnd.getHeight()-canvas.getHeight();

				wnd.setSize(glfw_width+dw, glfw_height+dh);

				glfw_resize_time = -1;
			}
		}
		GLFW.glfwPollEvents();
	}

	public void async_swapbuffers() {
		GLFW.glfwSwapBuffers(glfw_wnd);
	}

	public void async_stop() {
		GLFW.glfwTerminate();
	}

	private static final HashMap<Integer, String> keys = new HashMap<>();

	static {
		keys.put(GLFW.GLFW_KEY_LEFT, "LEFT");
		keys.put(GLFW.GLFW_KEY_RIGHT, "RIGHT");
		keys.put(GLFW.GLFW_KEY_UP, "UP");
		keys.put(GLFW.GLFW_KEY_DOWN, "DOWN");
		keys.put(GLFW.GLFW_KEY_PAUSE, "PAUSE");

		keys.put(GLFW.GLFW_KEY_F1, "F1");
		keys.put(GLFW.GLFW_KEY_F2, "F2");
		keys.put(GLFW.GLFW_KEY_F3, "F3");
		keys.put(GLFW.GLFW_KEY_F4, "F4");
		keys.put(GLFW.GLFW_KEY_F5, "F5");
		keys.put(GLFW.GLFW_KEY_F6, "F6");
		keys.put(GLFW.GLFW_KEY_F7, "F7");
		keys.put(GLFW.GLFW_KEY_F8, "F8");
		keys.put(GLFW.GLFW_KEY_F9, "F9");
		keys.put(GLFW.GLFW_KEY_F10, "F10");
		keys.put(GLFW.GLFW_KEY_F11, "F11");
		keys.put(GLFW.GLFW_KEY_F12, "F12");

		keys.put(GLFW.GLFW_KEY_DELETE, "DELETE");
		keys.put(GLFW.GLFW_KEY_ESCAPE, "ESCAPE");
		keys.put(GLFW.GLFW_KEY_BACKSPACE, "BACK_SPACE");
		keys.put(GLFW.GLFW_KEY_SPACE, " ");
	}

	private class GLFWEventConverter extends  AbstractEventConverter {

		private UIPoint last_point = new UIPoint(0, 0);

		private GLFWKeyCallback key_callback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				String name = GLFW.glfwGetKeyName(key, scancode);
				if(name == null) {
					name = keys.get(key);
				}

				if(action == GLFW.GLFW_PRESS) {
					startKeyEvent(name);
				} else if(action == GLFW.GLFW_RELEASE){
					endKeyEvent(name);
				}
			}
		};

		private double presstime = -1;

		private GLFWMouseButtonCallback mouse_callback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {

				UIPoint point = last_point;

				if(action == GLFW.GLFW_PRESS) {
					switch(button) {
						case GLFW.GLFW_MOUSE_BUTTON_1:
							startDraw(point);
							break;
						case GLFW.GLFW_MOUSE_BUTTON_2:
							presstime = GLFW.glfwGetTime();
						case GLFW.GLFW_MOUSE_BUTTON_3:
							startPan(point);
							break;
					}
				} else {
					switch(button) {
						case GLFW.GLFW_MOUSE_BUTTON_1:
							endDraw(point);
							break;
						case GLFW.GLFW_MOUSE_BUTTON_2:
							presstime = -1;
						case GLFW.GLFW_MOUSE_BUTTON_3:
							endPan(point);
							break;
					}
				}
			}
		};

		private GLFWCursorEnterCallback cursorenter_callback = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				if(entered) {
					startHover(last_point);
				} else {
					endHover(last_point);
				}
			}
		};

		private GLFWCursorPosCallback cursorpos_callback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				last_point = new UIPoint(xpos, height - ypos);
				updateHoverPosition(last_point);

				if(presstime+0.1<GLFW.glfwGetTime()) {
					updatePanPosition(last_point);
					updateDrawPosition(last_point);
				}
			}
		};

		private GLFWScrollCallback scroll_callback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				float factor = (float) Math.exp(yoffset / 20.0);

				startZoom();
				endZoomEvent(factor, last_point);
			}
		};

		private GLFWWindowSizeCallback size_callback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				synchronized (wnd_lock) {
					if(GLFWContextCreator.this.width == width && GLFWContextCreator.this.height == height) {
						glfw_resize_time = -1;
						return;
					}

					glfw_resize_time = System.currentTimeMillis();
					glfw_width = width;
					glfw_height = height;
				}
			}
		};

		private GLFWEventConverter() {
			super(parent);

			addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW, Replacement.COMMAND_SELECT, 5, 10));
			addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN, Replacement.COMMAND_ACTION, 5, 10));
		}

		private void registerCallbacks() {
			GLFW.glfwSetKeyCallback(glfw_wnd, key_callback);
			GLFW.glfwSetMouseButtonCallback(glfw_wnd, mouse_callback);
			GLFW.glfwSetScrollCallback(glfw_wnd, scroll_callback);
			GLFW.glfwSetCursorEnterCallback(glfw_wnd, cursorenter_callback);
			GLFW.glfwSetCursorPosCallback(glfw_wnd, cursorpos_callback);
			GLFW.glfwSetWindowSizeCallback(glfw_wnd, size_callback);
		}
	}
}
