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

import go.graphics.RedrawListener;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;
import go.graphics.swing.opengl.JOGLDrawContext;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

/**
 * This class lets you embed areas into swing components.
 * 
 * @author michael
 */
public class AreaContainer extends JPanel implements RedrawListener, GOEventHandlerProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204496712425576430L;
	private final Area area;

	private Component canvas;
	private JOGLDrawContext context;
	private int glContextCounter = 1;

	/**
	 * creates a new area conaainer
	 * 
	 * @param area
	 *            The area to display
	 */
	public AreaContainer(Area area) {
		this(area, false);
	}

	/**
	 * creates a new area conaainer
	 * 
	 * @param area
	 *            The area to display
	 */
	public AreaContainer(Area area, boolean forceLightweight) {
		this.area = area;
		this.setLayout(new BorderLayout());

		GLProfile profile = GLProfile.getDefault();
		GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(1);

		GLEventListener glEventListener = new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable gl, int x, int y, int width, int height) {
				resizeArea(gl.getGL().getGL2(), x, y, width, height);
			}

			@Override
			public void init(GLAutoDrawable arg0) {
				arg0.getGL().setSwapInterval(0);
			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
				disposeAll();
			}

			@Override
			public void display(GLAutoDrawable glDrawable) {
				draw(glDrawable.getGL().getGL2());
			}
		};

		if (forceLightweight) {
			GLJPanel panel = new GLJPanel(cap);
			panel.addGLEventListener(glEventListener);
			canvas = panel;
		} else {
			GLCanvas glCanvas = new GLCanvas(cap);
			glCanvas.addGLEventListener(glEventListener);
			canvas = glCanvas;
		}

		new GOSwingEventConverter(canvas, this);

		area.addRedrawListener(this);
		this.add(canvas);
	}

	/**
	 * Resizes the area.
	 * 
	 * @param gl2
	 *            The GL object
	 * @param x
	 *            unused
	 * @param y
	 *            unused
	 * @param width
	 *            The width
	 * @param height
	 *            The hieght
	 */
	protected void resizeArea(GL2 gl2, int x, int y, int width, int height) {
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		GLU glu = new GLU();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl2.glViewport(0, 0, width, height);

		area.setWidth(width);
		area.setHeight(height);
	}

	/**
	 * Draws the content area on the OpenGl object.
	 * 
	 * @param gl2
	 *            Where to draw on.
	 */
	protected void draw(GL2 gl2) {
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);

		gl2.glLoadIdentity();

		if (context == null || context.getGl2() != gl2) {
			context = new JOGLDrawContext(gl2, glContextCounter);
			glContextCounter++;
		}
		context.startFrame();
		area.drawArea(context);
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

	@Override
	public void handleEvent(GOEvent event) {
		area.handleEvent(event);
	}
}
