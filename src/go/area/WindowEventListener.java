package go.area;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class WindowEventListener implements GLEventListener {
	
	
	private final GLAreaDrawPane panel;

	public WindowEventListener(GLAreaDrawPane panel) {
		this.panel = panel;
	}

	@Override
	public void reshape(GLAutoDrawable gl, int x, int y, int width,
	        int height) {
		this.panel.resizeArea(gl.getGL().getGL2(), x, y, width, height);
	}

	@Override
	public void init(GLAutoDrawable arg0) {
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		this.panel.draw(glDrawable.getGL().getGL2());
	}

}
