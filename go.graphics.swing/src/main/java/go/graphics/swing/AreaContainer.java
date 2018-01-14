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
package go.graphics.swing;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.RedrawListener;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;
import go.graphics.swing.opengl.JOGLDrawContext;

/**
 * This class lets you embed areas into swing components.
 * 
 * @author michael
 */
public class AreaContainer extends JPanel implements RedrawListener, GOEventHandlerProvider, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204496712425576430L;
	private final Area area;

	private Component canvas;
	private JOGLDrawContext context;
	private boolean continue_run = true;

	private int width = 1, height = 1;
	private int new_width = 1, new_height = 1;
	private boolean change_res = true;
	private Object wnd_lock = new Object();

	private BufferedImage bi = null;
	private IntBuffer pixels;

	private Thread render_thread;
	private boolean listeners = false;

	/**
	 * creates a new area container
	 * 
	 * @param area
	 *            The area to display
	 */
	public AreaContainer(Area area) {
		this.area = area;
		this.setLayout(new BorderLayout());

		ComponentListener cl = new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				Component cmp = componentEvent.getComponent();
				synchronized (wnd_lock) {
					new_width = cmp.getWidth();
					new_height = cmp.getHeight();
					change_res = true;
				}
			}

			@Override
			public void componentMoved(ComponentEvent componentEvent) {
			}

			@Override
			public void componentShown(ComponentEvent componentEvent) {
			}

			@Override
			public void componentHidden(ComponentEvent componentEvent) {
			}
		};

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);

				if(!listeners) {
					new GOSwingEventConverter(SwingUtilities.windowForComponent(canvas), AreaContainer.this);
					listeners = true;
				}
				synchronized (wnd_lock) {
					graphics.drawImage(bi, 0, 0, null);
					graphics.dispose();
				}
			}
		};
		panel.addComponentListener(cl);
		canvas = panel;

		// Listener for Key-, Mouse- etc. events

		area.addRedrawListener(this);
		this.add(canvas);

		render_thread = new Thread(this);
		render_thread.start();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		continue_run = false;
	}

	@Override
	public void run() {
		GLFWErrorCallback ec = GLFWErrorCallback.createPrint(System.err);;
		GLFW.glfwSetErrorCallback(ec);

		GLFW.glfwInit();

		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 1);
		long glfw_wnd = GLFW.glfwCreateWindow(area.getWidth() + 1, area.getHeight() + 1, "lwjgl-offscreen", 0, 0);
		GLFW.glfwMakeContextCurrent(glfw_wnd);
		GLFW.glfwSwapInterval(0);

		context = new JOGLDrawContext(GL.createCapabilities());

		while (continue_run) {
			synchronized (wnd_lock) {
				if (change_res) {
					width = new_width;
					height = new_height;
					GLFW.glfwSetWindowSize(glfw_wnd, width, height);
					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glLoadIdentity();
					// coordinate system origin at lower left with width and height same as
					// the window
					GL11.glOrtho(0, width, 0, height, -1, 1);

					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glLoadIdentity();
					GL11.glViewport(0, 0, width, height);
					area.setWidth(width);
					area.setHeight(height);
					bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					pixels = BufferUtils.createIntBuffer(width*height);
					change_res = false;
				}
			}
			GLFW.glfwPollEvents();

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();

			context.startFrame();
			area.drawArea(context);

			synchronized (wnd_lock) {
				GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
				for(int x = 0;x != width;x++) {
					for(int y = 0; y!= height;y++) {
						bi.setRGB(x, height-y-1, pixels.get(y*width+x));
					}
				}
			}
			// uncomment to clear the offscreen buffer
			//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			// uncomment to draw the offscreen buffer to offscreen window
			//GLFW.glfwSwapBuffers(glfw_wnd);
		}

		GLFW.glfwTerminate();
		disposeAll();
	}


	/**
	 * Disposes all textures / buffers that were allocated by this context.
	 */
	protected void disposeAll() {
		if (context != null) {
			context.disposeAll();
		}
		context = null;
	}

	@Override
	public void requestRedraw() {
		canvas.repaint();
	}

	/**
	 * Forward the focus call to the Input canvas
	 */
	@Override
	public void requestFocus() {
		canvas.requestFocus();
	}

	@Override
	public void handleEvent(GOEvent event) {
		area.handleEvent(event);
	}
}
