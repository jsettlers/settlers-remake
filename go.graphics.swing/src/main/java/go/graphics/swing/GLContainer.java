package go.graphics.swing;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.event.GOEventHandlerProvider;
import go.graphics.swing.contextcreator.BackendSelector;
import go.graphics.swing.contextcreator.ContextCreator;
import go.graphics.swing.contextcreator.EBackendType;
import go.graphics.swing.contextcreator.JAWTContextCreator;
import go.graphics.swing.contextcreator.GLContextException;
import go.graphics.swing.opengl.LWJGLDrawContext;

public abstract class GLContainer extends JPanel implements GOEventHandlerProvider {


	protected ContextCreator cc;
	protected LWJGLDrawContext context;
	private boolean debug;
	protected float guiScale = 0;

	public GLContainer(EBackendType backend, LayoutManager layout, boolean debug) {
		setLayout(layout);
		this.debug = debug;

		try {
			cc = BackendSelector.createBackend(this, backend, debug);
			cc.init();
		} catch (Exception ex) {
			ex.printStackTrace();
			fatal("Could not create opengl context through " + backend.cc_name);
		}
	}

	public void fatal(String message) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, message+ "\nPress ok to exit", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		});
		System.err.println(message);
	}

	public void resizeContext(int width, int height) throws GLContextException {
		if(context == null) throw new GLContextException();
		context.resize(width, height);
	}

	public void finishFrame() {
		context.finishFrame();
	}

	public void wrapNewContext() {
		if(cc instanceof JAWTContextCreator) ((JAWTContextCreator)cc).makeCurrent(true);
		if(context != null) context.invalidate();

		GLCapabilities caps = GL.createCapabilities();

		try {
			if(caps.OpenGL20) {
				context = new LWJGLDrawContext(caps, cc::getScale, debug, guiScale);
			} else {
				errorGLVersion();
			}
		} catch(Throwable thrown) {
			fatal(thrown.getLocalizedMessage());
		}
	}

	private void errorGLVersion() {
		fatal("JSettlers needs at least OpenGL 2.0");
	}

	/**
	 * Disposes all textures / buffers that were allocated by this context.
	 */
	public void disposeAll() {
		cc.stop();
		if (context != null) {
			context.invalidate();
		}
		context = null;
	}

	public void draw() throws GLContextException {
		if(context == null) throw new GLContextException();
		context.startFrame();
	}

	public void requestRedraw() {
		if(cc != null) cc.repaint();
	}

	/**
	 * Forward the focus call to the Input canvas
	 */
	@Override
	public void requestFocus() {
		cc.requestFocus();
	}

	public void addCanvas(Component canvas) {
		add(canvas);
	}

	public void updateFPSLimit(int fpsLimit) {
		if(cc != null) cc.updateFPSLimit(fpsLimit);
	}
}
