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

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WinBase;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;
import org.lwjgl.vulkan.VkXlibSurfaceCreateInfoKHR;

import java.nio.LongBuffer;
import java.util.List;

import go.graphics.swing.ContextContainer;
import go.graphics.swing.vulkan.VulkanUtils;

import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRWin32Surface.*;
import static org.lwjgl.vulkan.KHRXlibSurface.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanContextCreator extends JAWTContextCreator {
	public VulkanContextCreator(ContextContainer container, boolean debug) {
		super(container, debug);
	}

	private long surface;
	private VkInstance instance;
	private long debugCallback;

	@Override
	protected void onInit() throws ContextException {
		try(MemoryStack stack = MemoryStack.stackPush()) {

			// instance extensions
			List<String> extensions = VulkanUtils.defaultExtensionArray(debug);
			if(Platform.get() == Platform.LINUX) extensions.add(VK_KHR_XLIB_SURFACE_EXTENSION_NAME);
			if(Platform.get() == Platform.WINDOWS) extensions.add(VK_KHR_WIN32_SURFACE_EXTENSION_NAME);

			instance = VulkanUtils.createInstance(stack, extensions, debug);
			debugCallback = debug ? VulkanUtils.setupDebugging(instance) : 0;

			LongBuffer surfacePtr = stack.mallocLong(1);
			if(Platform.get() == Platform.WINDOWS) {
				if(!instance.getCapabilities().VK_KHR_win32_surface) error("VK_KHR_win32_surface is missing.");

				VkWin32SurfaceCreateInfoKHR surfaceCreateInfo = VkWin32SurfaceCreateInfoKHR.callocStack(stack)
						.sType(VK_STRUCTURE_TYPE_WIN32_SURFACE_CREATE_INFO_KHR)
						.hwnd(windowConnection)
						.hinstance(WinBase.GetModuleHandle((String)null));

				if(vkCreateWin32SurfaceKHR(instance, surfaceCreateInfo, null, surfacePtr) != VK_SUCCESS) {
					error("Could not create a surface via VK_KHR_win32_surface.");
				}
			} else if(Platform.get() == Platform.LINUX) {
				if(!instance.getCapabilities().VK_KHR_xlib_surface) error("VK_KHR_xlib_surface is missing.");

				VkXlibSurfaceCreateInfoKHR surfaceCreateInfo = VkXlibSurfaceCreateInfoKHR.callocStack(stack)
						.sType(VK_STRUCTURE_TYPE_XLIB_SURFACE_CREATE_INFO_KHR)
						.dpy(windowConnection)
						.window(windowDrawable);

				if(vkCreateXlibSurfaceKHR(instance, surfaceCreateInfo, null, surfacePtr) != VK_SUCCESS) {
					error("Could not create a surface via VK_KHR_xlib_surface.");
				}
			} else if(Platform.get() == Platform.MACOSX) {
				error("OSX support is not implemented.");
				//if(!instance.getCapabilities().VK_MVK_macos_surface) error("VK_MVK_macos_surface is missing.");
			}
			surface = surfacePtr.get(0);

			parent.wrapNewVkContext(instance, surface);
		}
	}

	@Override
	public void stop() {
		if(debug) vkDestroyDebugReportCallbackEXT(instance, debugCallback, null);
		vkDestroySurfaceKHR(instance, surface, null);
		vkDestroyInstance(instance, null);
	}

	@Override
	protected void swapBuffers() throws ContextException {
		parent.swapBuffersVk();
	}

	@Override
	public void makeCurrent(boolean draw) {
		// OpenGL business
	}

}
