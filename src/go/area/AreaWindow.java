package go.area;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

public class AreaWindow implements GLAreaDrawPane {
	private GLWindow window;
	private final Area area;
	private FPSAnimator animator;

	public AreaWindow(Area area, boolean fullscreen) {
		this.area = area;
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		this.window = GLWindow.create(capabilities);
		this.window.addGLEventListener(new WindowEventListener(this));
		this.window.setSize(800, 600);
		this.window.setFullscreen(true);
		this.window.setAutoSwapBufferMode(true);
//		this.window.setAnimator(new FPSAnimator(50));
		this.window.setVisible(true);
		this.window.setTitle("");

		this.window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyNotify(WindowEvent arg0) {
				exit();
			}

		});

		this.animator = new FPSAnimator(this.window, 100);
		this.animator.start();
	}

	private void exit() {
		System.exit(0);
		this.animator.stop();
	};

	public void setTitle(String title) {
		this.window.setTitle(title);
	}

	@Override
	public void draw(GL2 gl2) {
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		gl2.glLoadIdentity();

		this.area.drawArea(gl2);
	}

	@Override
	public void resizeArea(GL2 gl2, int x, int y, int width, int height) {

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
}
