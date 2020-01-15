package go.graphics.swing.vulkan;

import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.VK10;

import go.graphics.ETextureType;
import go.graphics.GLDrawContext;
import go.graphics.TextureHandle;

public class VulkanTextureHandle extends TextureHandle {

	private long bfr, imageView, allocation;
	private boolean installed = false;
	private boolean shouldDestroy = false;

	public VulkanTextureHandle(GLDrawContext dc, int id, long bfr, long allocation, long imageView) {
		super(dc, id);
		this.allocation = allocation;
		this.imageView = imageView;
		this.bfr = bfr;
	}

	public long getImageViewId() {
		return imageView;
	}

	public long getTextureIdVk() {
		return bfr;
	}

	public long getAllocation() {
		return allocation;
	}

	public void destroy() {
		if(imageView == VK10.VK_NULL_HANDLE) return;

		VK10.vkDestroyImageView(((VulkanDrawContext)dc).device, imageView, null);
		Vma.vmaDestroyImage(((VulkanDrawContext)dc).allocators[VulkanDrawContext.TEXTUREDATA_BUFFER], bfr, allocation);
		imageView = VK10.VK_NULL_HANDLE;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [dc=" + dc + ", allocation=" + allocation + ", bfr=" + bfr + "]";
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled() {
		installed = true;
	}

	public boolean shouldBeDestroyed()  {
		return shouldDestroy;
	}

	public void setDestroy() {
		shouldDestroy = true;
	}

	@Override
	public void setType(ETextureType type) {
		super.setType(type);
		installed = false;
	}

	@Override
	public boolean isValid() {
		return imageView != VK10.VK_NULL_HANDLE && super.isValid();
	}


	public void replace(TextureHandle textureIndex) {
		id = textureIndex.getTextureId();
	}
}
