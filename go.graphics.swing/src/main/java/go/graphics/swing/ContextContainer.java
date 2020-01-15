package go.graphics.swing;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.vulkan.VkInstance;

import java.awt.Component;
import java.awt.LayoutManager;
import java.nio.IntBuffer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.GLDrawContext;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.swing.contextcreator.BackendSelector;
import go.graphics.swing.contextcreator.ContextCreator;
import go.graphics.swing.contextcreator.EBackendType;
import go.graphics.swing.contextcreator.JAWTContextCreator;
import go.graphics.swing.contextcreator.ContextException;
import go.graphics.swing.opengl.LWJGLDrawContext;
import go.graphics.swing.vulkan.VulkanDrawContext;

public abstract class ContextContainer extends JPanel implements GOEventHandlerProvider {


	protected ContextCreator cc;
	protected GLDrawContext context;
	private boolean debug;
	protected float guiScale = 0;

	public ContextContainer(EBackendType backend, LayoutManager layout, boolean debug) {
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

	public void resizeContext(int width, int height) throws ContextException {
		if(context == null) throw new ContextException();
		context.resize(width, height);
	}

	public void finishFrame() {
		context.finishFrame();
	}


	public void wrapNewVkContext(VkInstance instance, long surface) {
		if(context != null) context.invalidate();

		try {
			context = new VulkanDrawContext(instance, surface, guiScale);
		} catch(Throwable thrown) {
			fatal(thrown.getLocalizedMessage());
			thrown.printStackTrace();
		}
	}
	public void wrapNewGLContext() {
		if(cc instanceof JAWTContextCreator) ((JAWTContextCreator)cc).makeCurrent(true);
		if(context != null) context.invalidate();

		GLCapabilities caps = GL.createCapabilities();

		try {
			if(caps.OpenGL20) {
				context = new LWJGLDrawContext(caps, debug, guiScale);
			} else {
				errorGLVersion();
			}
		} catch(Throwable thrown) {
			fatal(thrown.getLocalizedMessage());
			thrown.printStackTrace();
		}
	}

	private void errorGLVersion() {
		fatal("JSettlers needs at least OpenGL 1.5 with GL_ARB_texture_non_power_of_two");
	}

	/**
	 * Disposes all textures / buffers that were allocated by this context.
	 */
	public void disposeAll() {
		if (context != null) context.invalidate();
		context = null;

		if(cc != null) cc.stop();
		cc = null;
	}

	public void draw() throws ContextException {
		if(context == null) throw new ContextException();
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

	public void swapBuffersVk() throws ContextException {
		if(context == null) throw new ContextException();
		((VulkanDrawContext)context).endFrame();
	}

	public void readFramebuffer(IntBuffer pixels, int width, int height) {
		if(context instanceof VulkanDrawContext) {
			((VulkanDrawContext)context).readFramebuffer(pixels, width, height);
		} else {
			((LWJGLDrawContext)context).readFramebuffer(pixels, width, height);
		}
	}

	public void clearFramebuffer() {
		if(context instanceof VulkanDrawContext) {
			((VulkanDrawContext)context).clearFramebuffer();
		} else {
			((LWJGLDrawContext)context).clearFramebuffer();
		}
	}
}
