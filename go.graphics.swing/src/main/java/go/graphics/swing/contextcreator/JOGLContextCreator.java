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

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import go.graphics.swing.GLContainer;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;

public class JOGLContextCreator extends ContextCreator implements GLEventListener{

	public JOGLContextCreator(GLContainer container, boolean debug) {
		super(container, debug);
	}

	@Override
	public void stop() {

	}

	@Override
	public void initSpecific() {
		if(debug) System.setProperty("jogl.debug.GLProfile", "true");
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		caps.setStencilBits(1);

		canvas = new GLJPanel(caps);
		((GLJPanel)canvas).addGLEventListener(this);

		new GOSwingEventConverter(canvas, parent);
	}

	@Override
	public void repaint() {
		canvas.repaint();
	}

	@Override
	public void requestFocus() {
		canvas.requestFocus();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.getGL().setSwapInterval(0);
		parent.wrapNewContext();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		parent.disposeAll();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		parent.draw();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		parent.resize_gl(width, height);
	}
}
