package go.area;

import go.RedrawListener;
import go.event.GOEvent;
import go.event.GOEventHandlerProvoder;
import go.event.swingInterpreter.GOSwingEventConverter;

import java.awt.BorderLayout;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import com.jogamp.opengl.util.FPSAnimator;


/**
 * This class lets you embed areas into swing components.
 * 
 * @author michael
 */
public class AreaContainer extends JPanel implements RedrawListener,
        GOEventHandlerProvoder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204496712425576430L;
	private final Area area;

	private GLCanvas canvas;
	private FPSAnimator animator;

	/**
	 * creates a new area conaainer
	 * 
	 * @param area
	 *            The area to display
	 */
	public AreaContainer(Area area) {
		this.area = area;
		this.setLayout(new BorderLayout());
		
		//GLProfile.initSingleton(true);
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(1);
		this.canvas = new GLCanvas(cap);

		new GOSwingEventConverter(this.canvas, this);

		this.canvas.addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable gl, int x, int y, int width,
			        int height) {
				resizeArea(gl.getGL().getGL2(), x, y, width, height);
			}

			@Override
			public void init(GLAutoDrawable arg0) {
			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
			}

			@Override
			public void display(GLAutoDrawable glDrawable) {
				draw(glDrawable.getGL().getGL2());
			}
		});

		this.animator = new FPSAnimator(this.canvas, 50);

		this.add(this.canvas);
		area.addRedrawListener(this);
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
		gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		GLU glu = new GLU();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);

		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl2.glViewport(0, 0, width, height);

		this.area.setWidth(width);
		this.area.setHeight(height);
	}

	/**
	 * Draws the content area on the OpenGl object.
	 * 
	 * @param gl2
	 *            Where to draw on.
	 */
	protected void draw(GL2 gl2) {
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		gl2.glLoadIdentity();

		this.area.drawArea(gl2);
	}

	@Override
	public void requestRedraw() {
		this.canvas.repaint();
	}

	@Override
	public void handleEvent(GOEvent event) {
		this.area.handleEvent(event);
	}

	public void setAutoAnimate(boolean animate) {
		if (animate) {
			this.animator.start();
		} else {
			this.animator.stop();
		}
	}
}
