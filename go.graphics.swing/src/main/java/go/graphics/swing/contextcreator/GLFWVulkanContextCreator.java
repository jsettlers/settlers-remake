package go.graphics.swing.contextcreator;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;
import java.util.List;

import go.graphics.swing.ContextContainer;
import go.graphics.swing.vulkan.VulkanUtils;

import static org.lwjgl.vulkan.KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME;

public class GLFWVulkanContextCreator extends GLFWContextCreator {
	public GLFWVulkanContextCreator(ContextContainer container, boolean debug) {
		super(container, debug);
	}

	@Override
	protected void configureWindow() {
		GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
	}

	@Override
	protected void setupContext() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			List<String> extensions = VulkanUtils.defaultExtensionArray(debug);
			if(Platform.get() == Platform.LINUX) extensions.add("VK_KHR_xcb_surface");
			if(Platform.get() == Platform.WINDOWS) extensions.add(VK_KHR_WIN32_SURFACE_EXTENSION_NAME);

			VkInstance instance = VulkanUtils.createInstance(stack, extensions, debug);
			if(debug) VulkanUtils.setupDebugging(instance);

			LongBuffer surfaceBfr = stack.callocLong(1);
			GLFWVulkan.glfwCreateWindowSurface(instance, glfw_wnd, null, surfaceBfr);

			parent.wrapNewVkContext(instance, surfaceBfr.get(0));
		}
	}

	@Override
	public void async_swapbuffers() {}

}
