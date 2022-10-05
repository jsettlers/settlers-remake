package go.graphics.swing.vulkan;

import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.VK10;

import go.graphics.BufferHandle;
import go.graphics.GLDrawContext;

public class VulkanBufferHandle extends BufferHandle {

	private long bfr, allocation, event;
	private int size, type;

	public VulkanBufferHandle(GLDrawContext dc, int type, long bfr, long allocation, long event, int size) {
		super(dc, -1);
		this.allocation = allocation;
		this.event = event;
		this.type = type;
		this.size = size;
		this.bfr = bfr;
	}

	public int getType() {
		return type;
	}

	public long getEvent() {
		return event;
	}

	public long getBufferIdVk() {
		return bfr;
	}

	public long getAllocation() {
		return allocation;
	}

	public int getSize() {
		return size;
	}

	public void destroy() {
		Vma.vmaDestroyBuffer(((VulkanDrawContext)dc).allocators[type], bfr, allocation);
		VK10.vkDestroyEvent(((VulkanDrawContext) dc).device, event, null);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [dc=" + dc + ", allocation=" + allocation + ", bfr=" + bfr + "]";
	}
}
