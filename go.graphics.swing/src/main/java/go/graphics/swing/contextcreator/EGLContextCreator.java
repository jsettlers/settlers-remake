package go.graphics.swing.contextcreator;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.egl.EGL10;
import org.lwjgl.egl.EGL12;
import org.lwjgl.egl.EGL13;
import org.lwjgl.egl.EGL14;
import org.lwjgl.system.Platform;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTX11DrawingSurfaceInfo;

import java.nio.IntBuffer;

import go.graphics.swing.GLContainer;

public class EGLContextCreator extends JAWTContextCreator {

	private static long egl_config;
	private static long egl_display = 0;
	private long egl_surface;
	private static long egl_context;

	private long native_drawable = 0;

	public EGLContextCreator(GLContainer container, boolean debug) {
		super(container, debug);
	}

	@Override
	public void stop() {
		EGL10.eglDestroySurface(egl_display, egl_surface);
	}

	@Override
	protected void swapBuffers() {
		EGL10.eglSwapBuffers(egl_display, egl_surface);
	}

	@Override
	protected void makeCurrent(boolean draw) {
		if(draw) {
			EGL10.eglMakeCurrent(egl_display, egl_surface, egl_surface, egl_context);
		} else {
			EGL10.eglMakeCurrent(egl_display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		}
	}

	@Override
	protected void initContext() {}

	private void initStatic() {
		egl_display = EGL10.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

		int[] egl_major = new int[1];
		int[] egl_minor = new int[1];

		EGL10.eglInitialize(egl_display, egl_major, egl_minor);

		if(egl_major[0] == 1 && egl_minor[0] < 4) throw new Error("EGL version is too low (" +egl_major[0]+"."+egl_minor[0] + ")");

		if(!EGL12.eglBindAPI(EGL14.EGL_OPENGL_API)) throw new Error("could not bind OpenGL");

		int[] attrs = {EGL13.EGL_CONFORMANT, EGL14.EGL_OPENGL_BIT,
				EGL10.EGL_STENCIL_SIZE, 1,
				EGL10.EGL_NONE};
		PointerBuffer cfgs = BufferUtils.createPointerBuffer(1);
		int[] num_config = new int[1];

		EGL10.eglChooseConfig(egl_display, attrs, cfgs, num_config);
		if(num_config[0] == 0) throw new Error("could not found egl configs!");
		egl_config = cfgs.get(0);

		int[] ctx_attrs = new int[] {EGL10.EGL_NONE};

		egl_context = EGL10.eglCreateContext(egl_display, egl_config, 0, ctx_attrs);
	}

	@Override
	protected void createNewSurfaceInfo() {
		long new_native_drawable = 0;

		if(Platform.get() == Platform.LINUX) {
			JAWTX11DrawingSurfaceInfo x11surfaceInfo = JAWTX11DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
			new_native_drawable = x11surfaceInfo.drawable();
		} else if(Platform.get() == Platform.WINDOWS) {
			JAWTWin32DrawingSurfaceInfo win32surfaceInfo = JAWTWin32DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
			new_native_drawable = win32surfaceInfo.hwnd();
		}

		if(native_drawable != new_native_drawable) {
			if(egl_display == 0) initStatic();

			if(native_drawable != 0) stop();
			egl_surface = EGL10.eglCreateWindowSurface(egl_display, egl_config, native_drawable = new_native_drawable, (IntBuffer)null);
		}
	}
}
