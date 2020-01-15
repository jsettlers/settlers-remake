/*******************************************************************************
 * Copyright (c) 2019
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

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.egl.EGL;
import org.lwjgl.egl.EGL10;
import org.lwjgl.egl.EGL12;
import org.lwjgl.egl.EGL13;
import org.lwjgl.egl.EGL14;
import org.lwjgl.egl.EGL15;
import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.egl.EGLDebugMessageKHRCallback;
import org.lwjgl.egl.KHRCreateContext;
import org.lwjgl.egl.KHRDebug;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import go.graphics.swing.ContextContainer;

public class EGLContextCreator extends JAWTContextCreator {

	private static long egl_config;
	private static long egl_display = 0;
	private long egl_surface;
	private static long egl_context;

	public EGLContextCreator(ContextContainer container, boolean debug) {
		super(container, debug);
		initStatic();
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
	public void makeCurrent(boolean draw) {
		if(draw) {
			EGL10.eglMakeCurrent(egl_display, egl_surface, egl_surface, egl_context);
		} else {
			EGL10.eglMakeCurrent(egl_display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		}
	}

	private static final int[][][] ctx_attrs = new int[][][] {
		{
			{// GL3.2+ with debugging
				EGL15.EGL_CONTEXT_MAJOR_VERSION, 3,
				EGL15.EGL_CONTEXT_MINOR_VERSION, 2,
				EGL15.EGL_CONTEXT_OPENGL_PROFILE_MASK, EGL15.EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT,
				KHRCreateContext.EGL_CONTEXT_FLAGS_KHR, KHRCreateContext.EGL_CONTEXT_OPENGL_DEBUG_BIT_KHR,
				EGL10.EGL_NONE
			},
			{// GL3.2+
				EGL15.EGL_CONTEXT_MAJOR_VERSION, 3,
				EGL15.EGL_CONTEXT_MINOR_VERSION, 2,
				EGL15.EGL_CONTEXT_OPENGL_PROFILE_MASK, EGL15.EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT,
				EGL10.EGL_NONE
			}
		},

		{
			{// GL1.1+ with debugging
				KHRCreateContext.EGL_CONTEXT_FLAGS_KHR, KHRCreateContext.EGL_CONTEXT_OPENGL_DEBUG_BIT_KHR,
				EGL10.EGL_NONE
			},
			{// GL1.1+
				EGL10.EGL_NONE
			}
		},
	};

	private void setEGLDebugFunction(boolean info, boolean warning, boolean error_arg, boolean critical) {
		try(MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer debug = stack.ints(
					KHRDebug.EGL_DEBUG_MSG_CRITICAL_KHR, critical ? EGL10.EGL_TRUE : EGL10.EGL_FALSE,
					KHRDebug.EGL_DEBUG_MSG_ERROR_KHR, error_arg ? EGL10.EGL_TRUE : EGL10.EGL_FALSE,
					KHRDebug.EGL_DEBUG_MSG_WARN_KHR, warning ? EGL10.EGL_TRUE : EGL10.EGL_FALSE,
					KHRDebug.EGL_DEBUG_MSG_INFO_KHR, info ? EGL10.EGL_TRUE : EGL10.EGL_FALSE,
					EGL10.EGL_NONE
			);

			PointerBuffer bfr = stack.pointers(MemoryUtil.memAddress(debug), EGL10.EGL_NONE);


			try {
				KHRDebug.eglDebugMessageControlKHR(
						(error, command, messageType, threadLabel, objectLabel, message) -> {
							String command_str = EGLDebugMessageKHRCallback.getCommand(command);
							String message_str = EGLDebugMessageKHRCallback.getMessage(message);

							System.out.println("[EGL] Debug Message");
							System.out.println("    error: " + error);
							System.out.println("    command: " + command_str);
							System.out.println("    messageType: " + messageType);
							System.out.println("    threadLabel: " + threadLabel);
							System.out.println("    objectLabel: " + objectLabel);
							System.out.println("    message: " + message_str);
						}, bfr);
			} catch(Throwable thrown) {}
		}
	}

	private void initStatic() {
		if(egl_display != 0) return;

		if(debug) setEGLDebugFunction(true, true, true, true);

		egl_display = EGL10.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
		EGL10.eglInitialize(egl_display, new int[] {1}, new int[] {1});
		EGLCapabilities caps = EGL.createDisplayCapabilities(egl_display);
		if(!caps.EGL14 || !EGL12.eglBindAPI(EGL14.EGL_OPENGL_API)) throw new Error("could not bind OpenGL");

		int[] attrs = {EGL13.EGL_CONFORMANT, EGL14.EGL_OPENGL_BIT,
				EGL10.EGL_STENCIL_SIZE, 1,
				EGL10.EGL_NONE};
		PointerBuffer cfgs = BufferUtils.createPointerBuffer(1);
		int[] num_config = new int[1];

		EGL10.eglChooseConfig(egl_display, attrs, cfgs, num_config);
		if(num_config[0] == 0) throw new Error("could not found egl configs!");
		egl_config = cfgs.get(0);

	}

	@Override
	protected void onInit() throws ContextException {
		int i = 0;
		while(egl_context == 0 && ctx_attrs.length > i) {
			egl_context = EGL10.eglCreateContext(egl_display, egl_config, 0, ctx_attrs[i++][debug?0:1]);
			if(egl_context != 0 && EGL10.eglGetError() != EGL10.EGL_SUCCESS) {
				EGL10.eglDestroyContext(egl_display, egl_context);
				egl_context = 0;
			}
		}
		if(egl_context == 0) error("could not create context");

		parent.wrapNewGLContext();
	}

	@Override
	protected void onNewDrawable() throws ContextException {
		egl_surface = EGL10.eglCreateWindowSurface(egl_display, egl_config, windowDrawable, (IntBuffer)null);
		if(EGL10.eglGetError() != EGL10.EGL_SUCCESS) error("could not create new drawable");
	}
}
