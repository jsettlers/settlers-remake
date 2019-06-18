package go.graphics.swing;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
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
import go.graphics.swing.opengl.LWJGL15DrawContext;
import go.graphics.swing.opengl.LWJGL20DrawContext;
import go.graphics.swing.opengl.LWJGL31DrawContext;

public abstract class GLContainer extends JPanel implements GOEventHandlerProvider {


	protected ContextCreator cc;
	protected LWJGL15DrawContext context;
	private boolean debug;

	public GLContainer(EBackendType backend, LayoutManager layout, boolean debug) {
		setLayout(layout);
		this.debug = debug;

		try {
			cc = BackendSelector.createBackend(this, backend, debug);
			cc.init();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not create opengl context through " + backend.cc_name + "\nPress ok to exit");
			System.exit(1);
		}
	}

	public void resizeContext(int width, int height) {
		context.resize(width, height);
	}

	public void finishFrame() {
		context.finishFrame();
	}

	public void wrapNewContext() {
		if(cc instanceof JAWTContextCreator) ((JAWTContextCreator)cc).makeCurrent(true);
		if(context != null) context.disposeAll();

		GLCapabilities caps = GL.createCapabilities();

		if(caps.OpenGL31 && caps.GL_EXT_geometry_shader4) {
			context = new LWJGL31DrawContext(caps, debug);
		} else if(caps.OpenGL20) {
			context = new LWJGL20DrawContext(caps, debug);
		} else if(caps.OpenGL15 && caps.GL_ARB_texture_non_power_of_two) {
			context = new LWJGL15DrawContext(caps, debug);
		} else {
			context = null;
			errorGLVersion();
		}
	}

	private void errorGLVersion() {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, "JSettlers needs at least OpenGL 1.5 with GL_ARB_texture_non_power_of_two\nPress ok to exit");
			System.exit(1);
		});
	}

	/**
	 * Disposes all textures / buffers that were allocated by this context.
	 */
	public void disposeAll() {
		cc.stop();
		if (context != null) {
			context.disposeAll();
		}
		context = null;
	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void requestRedraw() {
		cc.repaint();
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
		cc.updateFPSLimit(fpsLimit);
	}
}
