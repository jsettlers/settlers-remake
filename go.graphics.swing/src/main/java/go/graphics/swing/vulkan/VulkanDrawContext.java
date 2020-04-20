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
package go.graphics.swing.vulkan;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkBufferMemoryBarrier;
import org.lwjgl.vulkan.VkClearAttachment;
import org.lwjgl.vulkan.VkClearRect;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;

import go.graphics.AbstractColor;
import go.graphics.BackgroundDrawHandle;
import go.graphics.BufferHandle;
import go.graphics.EPrimitiveType;
import go.graphics.ETextureType;
import go.graphics.GLDrawContext;
import go.graphics.ManagedHandle;
import go.graphics.MultiDrawHandle;
import go.graphics.TextureHandle;
import go.graphics.UnifiedDrawHandle;
import go.graphics.VkDrawContext;
import go.graphics.swing.text.LWJGLTextDrawer;

import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanDrawContext extends GLDrawContext implements VkDrawContext {

	private float nativeScale = 0;
	protected VkDevice device = null;
	private VkPhysicalDevice physicalDevice;

	private long surface;
	private int surfaceFormat;
	private VkInstance instance;

	private VkQueue presentQueue;
	private VkQueue graphicsQueue;

	protected long[] allocators = new long[] {0, 0, 0, 0};

	private int fbWidth;
	private int fbHeight;

	private long commandPool = VK_NULL_HANDLE;
	private VkCommandBuffer graphCommandBuffer = null;
	private VkCommandBuffer memCommandBuffer = null;
	private VkCommandBuffer fbCommandBuffer = null;

	private long renderPass;

	private long fetchFramebufferSemaphore = VK_NULL_HANDLE;
	private long presentFramebufferSemaphore = VK_NULL_HANDLE;

	private long descPool = VK_NULL_HANDLE;

	private VulkanPipeline backgroundPipeline = null;
	private VulkanPipeline lineUnifiedPipeline = null;
	private VulkanPipeline unifiedArrayPipeline = null;
	private VulkanPipeline unifiedMultiPipeline = null;
	private VulkanPipeline unifiedPipeline = null;

	private long[] samplers = new long[ETextureType.values().length];

	private final Semaphore resourceMutex = new Semaphore(1);
	private final Semaphore closeMutex = new Semaphore(1);

	private final BiFunction<VkQueueFamilyProperties, Integer, Boolean> graphicsQueueCond = (queue, index) -> (queue.queueFlags()&VK_QUEUE_GRAPHICS_BIT)>0;
	private final BiFunction<VkQueueFamilyProperties, Integer, Boolean> presentQueueCond = (queue, index) -> {
		int[] present = new int[1];
		vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, index, surface, present);
		return present[0]==1;
	};

	protected final List<VulkanMultiBufferHandle> multiBuffers = new ArrayList<>();
	protected final List<VulkanTextureHandle> textures = new ArrayList<>();
	protected final List<VulkanBufferHandle> buffers = new ArrayList<>();

	private float guiScale;

	public VulkanDrawContext(VkInstance instance, long surface, float guiScale) {
		this.instance = instance;
		this.guiScale = guiScale;
		this.surface = surface;


		try(MemoryStack stack = MemoryStack.stackPush()) {
			VkPhysicalDevice[] allPhysicalDevices = VulkanUtils.listPhysicalDevices(stack, instance);
			physicalDevice = VulkanUtils.findPhysicalDevice(allPhysicalDevices);

			VkQueueFamilyProperties.Buffer allQueueFamilies = VulkanUtils.listQueueFamilies(stack, physicalDevice);
			int universalQueueIndex = VulkanUtils.findQueue(allQueueFamilies, (queue, index) -> graphicsQueueCond.apply(queue, index)&&presentQueueCond.apply(queue, index));
			int graphicsQueueIndex = universalQueueIndex!=-1? universalQueueIndex : VulkanUtils.findQueue(allQueueFamilies, graphicsQueueCond);
			int presentQueueIndex = universalQueueIndex!=-1? universalQueueIndex : VulkanUtils.findQueue(allQueueFamilies, presentQueueCond);

			if(graphicsQueueIndex == -1) throw new Error("Could not find any graphics queue.");
			if(presentQueueIndex == -1) throw new Error("Could not find any present queue.");

			// device extensions
			List<String> deviceExtensions = new ArrayList<>();
			deviceExtensions.add(VK_KHR_SWAPCHAIN_EXTENSION_NAME);

			List<VkQueue> queues = new ArrayList<>();
			device = VulkanUtils.createDevice(stack, physicalDevice, deviceExtensions, queues, universalQueueIndex!=-1?new int[] {universalQueueIndex} : new int[] {graphicsQueueIndex, presentQueueIndex});

			if(universalQueueIndex != -1) {
				graphicsQueue = presentQueue = queues.get(0);
			} else {
				graphicsQueue = queues.get(0);
				presentQueue = queues.get(1);
			}

			VkSurfaceFormatKHR.Buffer allSurfaceFormats = VulkanUtils.listSurfaceFormats(stack, physicalDevice, surface);
			VkSurfaceFormatKHR surfaceFormat = VulkanUtils.findSurfaceFormat(allSurfaceFormats);
			this.surfaceFormat = surfaceFormat.format();

			for(int i = 0; i != allocators.length; i++) allocators[i] = VulkanUtils.createAllocator(stack, instance, device, physicalDevice);

			commandPool = VulkanUtils.createCommandPool(stack, device, universalQueueIndex);
			graphCommandBuffer = VulkanUtils.createCommandBuffer(stack, device, commandPool);
			memCommandBuffer = VulkanUtils.createCommandBuffer(stack, device, commandPool);
			fbCommandBuffer = VulkanUtils.createCommandBuffer(stack, device, commandPool);

			renderPass = VulkanUtils.createRenderPass(stack, device, surfaceFormat.format());
			renderPassBeginInfo.renderPass(renderPass);

			swapchainCreateInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
					.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
					.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT|VK_IMAGE_USAGE_TRANSFER_SRC_BIT|VK_IMAGE_USAGE_TRANSFER_DST_BIT)
					.imageColorSpace(surfaceFormat.colorSpace())
					.presentMode(VK_PRESENT_MODE_FIFO_KHR) // must be supported by all drivers
					.imageFormat(surfaceFormat.format())
					.imageArrayLayers(1)
					.surface(surface)
					.clipped(false);

			if(universalQueueIndex != -1) {
				swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
			} else {
				swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT)
						.pQueueFamilyIndices(stack.ints(graphicsQueueIndex, presentQueueIndex));
			}
			swapchainImageViewCreateInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
					.viewType(VK_IMAGE_VIEW_TYPE_2D)
					.format(surfaceFormat.format());
			swapchainImageViewCreateInfo.subresourceRange()
					.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
					.baseMipLevel(0)
					.levelCount(1)
					.baseArrayLayer(0)
					.layerCount(1);

			framebufferCreateInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
					.renderPass(renderPass)
					.layers(1);

			descPool = VulkanUtils.createDescriptorPool(stack, device, 5);

			unifiedPipeline = new VulkanPipeline.UnifiedPipeline(stack, this, descPool, renderPass, EPrimitiveType.Quad);
			lineUnifiedPipeline = new VulkanPipeline.UnifiedPipeline(stack, this, descPool, renderPass, EPrimitiveType.Line);
			unifiedArrayPipeline = new VulkanPipeline.UnifiedArrayPipeline(stack, this, descPool, renderPass);
			unifiedMultiPipeline = new VulkanPipeline.UnifiedMultiPipeline(stack, this, descPool, renderPass);
			backgroundPipeline = new VulkanPipeline.BackgroundPipeline(stack, this, descPool, renderPass);


			LongBuffer semaphoreBfr = stack.callocLong(1);
			fetchFramebufferSemaphore = VulkanUtils.createSemaphore(semaphoreBfr, device);
			presentFramebufferSemaphore = VulkanUtils.createSemaphore(semaphoreBfr, device);


			VkSamplerCreateInfo samplerCreateInfo = VkSamplerCreateInfo.callocStack(stack)
					.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
					.magFilter(VK_FILTER_NEAREST)
					.minFilter(VK_FILTER_NEAREST)
					.addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
					.addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
					.addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
					.mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
					.minLod(0)
					.maxLod(0)
					.compareEnable(false)
					.anisotropyEnable(false)
					.unnormalizedCoordinates(false);
			samplers[ETextureType.NEAREST_FILTER.ordinal()] = VulkanUtils.createSampler(stack, samplerCreateInfo, device);

			samplerCreateInfo.minFilter(VK_FILTER_LINEAR)
					.magFilter(VK_FILTER_LINEAR);
			samplers[ETextureType.LINEAR_FILTER.ordinal()] = VulkanUtils.createSampler(stack, samplerCreateInfo, device);

			int globalUniformBufferSize = 4*4*4*(VulkanUtils.MAX_GLOBALTRANS_COUNT+1); // mat4+(1+MAX_GLOBALTRANS_COUNT)
			globalUniformStagingBuffer = createBuffer(globalUniformBufferSize, STAGING_BUFFER);
			globalUniformBufferData = BufferUtils.createByteBuffer(globalUniformBufferSize);
			globalUniformBuffer = createBuffer(globalUniformBufferSize, STATIC_BUFFER);
			backgroundUniformBfr = createBuffer(4*4*4, STATIC_BUFFER); // mat4
			unifiedUniformBfr = createBuffer(4, STATIC_BUFFER);

			if(globalUniformBuffer == null || backgroundUniformBfr == null || unifiedUniformBfr == null) throw new Error("Could not create uniform buffers.");

			installUniformBuffer(globalUniformBuffer, 0);

			installUniformBuffer(backgroundUniformBfr, 2, 0, backgroundPipeline);
			installUniformBuffer(unifiedUniformBfr, 2, 0, lineUnifiedPipeline);
			installUniformBuffer(unifiedUniformBfr, 2, 0, unifiedArrayPipeline);
			installUniformBuffer(unifiedUniformBfr, 2, 0, unifiedMultiPipeline);
			installUniformBuffer(unifiedUniformBfr, 2, 0, unifiedPipeline);

			for(int i = 0; i != MAX_CACHE_COUNT; i++) installUniformBuffer(unifiedUniformBfr, 3, i, unifiedMultiPipeline);

		} finally {
			if(unifiedUniformBfr == null) invalidate();
		}
	}

	@Override
	public void invalidate() {
		closeMutex.acquireUninterruptibly();
		resourceMutex.acquireUninterruptibly();
		closeMutex.release();

		textures.forEach(VulkanTextureHandle::destroy);
		buffers.forEach(VulkanBufferHandle::destroy);

		for(long sampler : samplers) {
			if(sampler != 0) vkDestroySampler(device, sampler, null);
		}

		if(presentFramebufferSemaphore != VK_NULL_HANDLE) vkDestroySemaphore(device, presentFramebufferSemaphore, null);
		if(fetchFramebufferSemaphore != VK_NULL_HANDLE) vkDestroySemaphore(device, fetchFramebufferSemaphore, null);
		if(swapchain != VK_NULL_HANDLE) {
			destroyFramebuffers(-1);
			destroySwapchainViews(-1);
			vkDestroySwapchainKHR(device, swapchain, null);
		}

		if(backgroundPipeline != null) backgroundPipeline.destroy();
		if(lineUnifiedPipeline != null) lineUnifiedPipeline.destroy();
		if(unifiedArrayPipeline != null) unifiedArrayPipeline.destroy();
		if(unifiedMultiPipeline != null) unifiedMultiPipeline.destroy();
		if(unifiedPipeline != null) unifiedPipeline.destroy();
		if(descPool != VK_NULL_HANDLE) vkDestroyDescriptorPool(device, descPool, null);

		for(long allocator : allocators) if(allocator != 0) vmaDestroyAllocator(allocator);

		if(renderPass != VK_NULL_HANDLE) vkDestroyRenderPass(device, renderPass, null);

		if(fbCommandBuffer != null) vkFreeCommandBuffers(device, commandPool, fbCommandBuffer);
		if(memCommandBuffer != null) vkFreeCommandBuffers(device, commandPool, memCommandBuffer);
		if(graphCommandBuffer != null) vkFreeCommandBuffers(device, commandPool, graphCommandBuffer);
		if(commandPool != VK_NULL_HANDLE) vkDestroyCommandPool(device, commandPool, null);
		commandBufferRecording = false;

		if(device != null) vkDestroyDevice(device, null);

		fbCommandBuffer = null;
		memCommandBuffer = null;
		graphCommandBuffer = null;
		commandPool = VK_NULL_HANDLE;

		presentFramebufferSemaphore = VK_NULL_HANDLE;
		fetchFramebufferSemaphore = VK_NULL_HANDLE;
		swapchain = VK_NULL_HANDLE;
		device = null;
		super.invalidate();
		resourceMutex.release();
	}

	@Override
	public void setShadowDepthOffset(float depth) {
		unifiedUniformBfrData.putFloat(0, depth);
		unifiedDataUpdated = true;
	}

	private void updateUnifiedStatic() {
		if(unifiedDataUpdated) {
			updateBufferAt(unifiedUniformBfr, 0, unifiedUniformBfrData);
			unifiedDataUpdated = false;
		}
	}

	protected VulkanBufferHandle unifiedUniformBfr = null;
	private ByteBuffer unifiedUniformBfrData = BufferUtils.createByteBuffer(4);
	private boolean unifiedDataUpdated = false;


	private LongBuffer imageBfr = BufferUtils.createLongBuffer(1);
	private LongBuffer imageViewBfr = BufferUtils.createLongBuffer(1);
	private PointerBuffer imageAllocationBfr = BufferUtils.createPointerBuffer(1);


	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		if(!commandBufferRecording) return null;
		if(consumedTexSlots == VulkanUtils.MAX_TEXTURE_COUNT) {
			throw new Error("Out of texture slots: increase VulkanUtils.MAX_TEXTURE_COUNT");
		}

		VulkanTextureHandle vkTexHandle = createTexture(width, height, VK_FORMAT_R4G4B4A4_UNORM_PACK16, VK_IMAGE_USAGE_SAMPLED_BIT|VK_IMAGE_USAGE_TRANSFER_DST_BIT, true, name);
		changeLayout(vkTexHandle, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, true);

		if(data != null) updateTexture(vkTexHandle, 0, 0, width, height, data);
		return vkTexHandle;
	}

	@Override
	protected void drawMulti(MultiDrawHandle call) {
		if(!commandBufferRecording || call == null || call.drawCalls == null || call.sourceQuads == null) return;
		if(call.getVertexArrayId() >= installedManagedHandleCount) return;

		updateUnifiedStatic();

		VulkanBufferHandle vkDrawCalls = (VulkanBufferHandle) call.drawCalls;

		bind(unifiedMultiPipeline);

		unifiedMultiPipeline.pushConstantBfr
				.putInt(0, call.sourceQuads.texture!=null ?call.sourceQuads.texture.getTextureId():0)
				.putInt(4, call.getVertexArrayId());

		unifiedMultiPipeline.pushConstants(graphCommandBuffer);
		unifiedMultiPipeline.bindVertexBuffers(graphCommandBuffer, vkDrawCalls.getBufferIdVk());

		vkCmdDraw(graphCommandBuffer, 4, call.used, 0, 0);

		((VulkanMultiBufferHandle)call.drawCalls).inc();
	}

	private VulkanMultiBufferHandle unifiedArrayBfr = createMultiBuffer(2*100*4*4, DYNAMIC_BUFFER);
	private ByteBuffer unifiedArrayStaging = BufferUtils.createByteBuffer(2*100*4*4);

	@Override
	protected void drawUnifiedArray(UnifiedDrawHandle call, int primitive, int vertexCount, float[] trans, float[] colors, int array_len) {
		if(!commandBufferRecording || call == null || call.vertices == null) return;

		updateUnifiedStatic();

		if(primitive != EPrimitiveType.Quad) throw new Error("not implemented primitive: " + primitive);

		FloatBuffer data = unifiedArrayStaging.asFloatBuffer();

		data.put(colors, 0, array_len*4);
		data.position(4*100);
		data.put(trans, 0, array_len*4);
		updateBufferAt(unifiedArrayBfr, 0, unifiedArrayStaging);

		bind(unifiedArrayPipeline);

		long vb = ((VulkanBufferHandle)call.vertices).getBufferIdVk();
		unifiedArrayPipeline.bindVertexBuffers(graphCommandBuffer, vb, vb, unifiedArrayBfr.getBufferIdVk());

		unifiedArrayPipeline.pushConstantBfr.putInt(0, call.texture!=null?call.texture.getTextureId():0);
		unifiedArrayPipeline.pushConstants(graphCommandBuffer);

		vkCmdDraw(graphCommandBuffer, vertexCount, array_len, call.offset, 0);
		unifiedArrayBfr.inc();
	}

	private Map<Integer, VulkanBufferHandle> lineIndexBfr = new HashMap<>();

	@Override
	protected void drawUnified(UnifiedDrawHandle call, int primitive, int vertices, int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(!commandBufferRecording || call == null || call.vertices == null) return;

		updateUnifiedStatic();

		if(primitive == EPrimitiveType.Triangle || primitive == EPrimitiveType.Quad) {
			bind(unifiedPipeline);
		} else {
			bind(lineUnifiedPipeline);
		}

		long vb = ((VulkanBufferHandle)call.vertices).getBufferIdVk();
		lastPipeline.bindVertexBuffers(graphCommandBuffer, vb, vb);

		ByteBuffer unifiedPushConstants = lastPipeline.pushConstantBfr;
		unifiedPushConstants.putInt(0, call.texture!=null?call.texture.getTextureId():0);

		unifiedPushConstants.putFloat(4, sx);
		unifiedPushConstants.putFloat(8, sy);
		unifiedPushConstants.putFloat(12, x);
		unifiedPushConstants.putFloat(16, y);
		unifiedPushConstants.putFloat(20, z);

		if(color != null) {
			unifiedPushConstants.putFloat(28, color.red);
			unifiedPushConstants.putFloat(32, color.green);
			unifiedPushConstants.putFloat(36, color.blue);
			unifiedPushConstants.putFloat(40, color.alpha);
		} else {
			unifiedPushConstants.putFloat(28, 1);
			unifiedPushConstants.putFloat(32, 1);
			unifiedPushConstants.putFloat(36, 1);
			unifiedPushConstants.putFloat(40, 1);
		}

		unifiedPushConstants.putFloat(44, intensity);
		unifiedPushConstants.putInt(48, mode);

		lastPipeline.pushConstants(graphCommandBuffer);

		if(primitive == EPrimitiveType.Triangle) {
			vkCmdDraw(graphCommandBuffer, vertices, 1, call.offset, 0);
		} else if(primitive == EPrimitiveType.Quad) {
			vkCmdDraw(graphCommandBuffer, 4, 1, call.offset, 0);
		} else {
			VulkanBufferHandle indexBfr = lineIndexBfr.get(vertices);

			if(indexBfr == null) {
				ByteBuffer indices = BufferUtils.createByteBuffer((vertices)*2*4);
				IntBuffer data = indices.asIntBuffer();

				for(int i = 0; i != vertices; i++) {
					data.put(i*2, i);
					data.put(i*2+1, (i+1)%vertices);
				}

				indexBfr = createBuffer(indices.remaining(), STATIC_BUFFER);
				updateBufferAt(indexBfr, 0, indices);

				lineIndexBfr.put(vertices, indexBfr);
			}

			vkCmdBindIndexBuffer(graphCommandBuffer, indexBfr.getBufferIdVk(), 0, VK_INDEX_TYPE_UINT32);
			vkCmdDrawIndexed(graphCommandBuffer, (vertices+(primitive==EPrimitiveType.LineLoop?0:-1))*2, 1, 0, call.offset, 0);
		}
	}

	@Override
	public void drawBackground(BackgroundDrawHandle call) {
		if(!commandBufferRecording || call == null || call.texture == null || call.vertices == null || call.colors == null) return;

		VulkanTextureHandle vkTex = (VulkanTextureHandle) call.texture;
		VulkanBufferHandle vkShape = (VulkanBufferHandle) call.vertices;
		VulkanBufferHandle vkColor = (VulkanBufferHandle) call.colors;

		if(!vkTex.isInstalled()) return;

		bind(backgroundPipeline);

		if(backgroundDataUpdated) {
			updateBufferAt(backgroundUniformBfr, 0, backgroundUniformBfrData);
			backgroundDataUpdated = false;
		}

		lastPipeline.pushConstantBfr.putInt(0, call.texture.getTextureId());
		lastPipeline.pushConstants(graphCommandBuffer);

		backgroundPipeline.bindVertexBuffers(graphCommandBuffer, vkShape.getBufferIdVk(), vkColor.getBufferIdVk());

		int starti = call.offset < 0 ? (int)Math.ceil(-call.offset/(float)call.stride) : 0;
		int draw_lines = call.lines-starti;

		int triangleCount = ((VulkanBufferHandle) call.vertices).getSize()/20;

		for (int i = 0; i != draw_lines; i++) {
			int lineStart = (call.offset+call.stride*(i+starti))*3;
			int lineLen = call.width*3;
			if(lineStart >= triangleCount) break;
			else if(lineStart+lineLen >= triangleCount) lineLen = triangleCount-lineStart;

			vkCmdDraw(graphCommandBuffer, lineLen, 1, lineStart, 0);
		}
	}

	@Override
	public void setHeightMatrix(float[] matrix) {
		backgroundUniformBfrData.asFloatBuffer().put(matrix, 0, 16);
		backgroundDataUpdated = true;
	}

	protected final VulkanBufferHandle backgroundUniformBfr;
	private ByteBuffer backgroundUniformBfrData = BufferUtils.createByteBuffer((4*4+2*4+1)*4);
	private boolean backgroundDataUpdated = false;


	protected int globalAttrIndex = 0;
	private Matrix4f global = new Matrix4f();

	@Override
	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		if(!commandBufferRecording) return;

		if(globalAttrIndex == VulkanUtils.MAX_GLOBALTRANS_COUNT) throw new Error("Out of globalTrans slots: increase VulkanUtils.MAX_GLOBALTRANS_COUNT");
		globalAttrIndex++;

		finishFrame();

		global.identity();
		global.scale(sx, sy, sz);
		global.translate(x, y, z);
		global.get(4*4*4*(globalAttrIndex+1), globalUniformBufferData);

		if(lastPipeline != null) vkCmdPushConstants(graphCommandBuffer, lastPipeline.pipelineLayout, VK_SHADER_STAGE_ALL_GRAPHICS, 0, new int[]{globalAttrIndex});
	}

	@Override
	public void updateTexture(TextureHandle handle, List<int[]> diff, ByteBuffer data) {
		if(!commandBufferRecording || handle == null) return;

		VulkanTextureHandle vkTexture = (VulkanTextureHandle)handle;
		if(vkTexture.getImageViewId() == VK_NULL_HANDLE) return;

		int stagingPos = prepareStagingData(data);

		int count = diff.size();
		VkBufferImageCopy.Buffer regions = VkBufferImageCopy.create(count);
		for(int i = 0; i != count; i++) {
			VkBufferImageCopy imageCopy = regions.get(i);
			int[] original = diff.get(i);
			imageCopy.imageOffset().set(original[0], original[1], 0);
			imageCopy.imageExtent().set(original[2], original[3], 1);
			imageCopy.imageSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1);
			imageCopy.bufferOffset(stagingPos+original[4]).bufferRowLength(original[2]);
		}

		changeLayout(vkTexture, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, true);
		vkCmdCopyBufferToImage(memCommandBuffer, stagingBuffers.get(stagingBufferIndex).getBufferIdVk(), vkTexture.getTextureIdVk(), VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions);
		changeLayout(vkTexture, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, true);
	}

	@Override
	public void updateTexture(TextureHandle textureIndex, int left, int bottom, int width, int height, ShortBuffer data) {
		if(!commandBufferRecording || textureIndex == null || width == 0 || height == 0) return;

		VulkanTextureHandle vkTexture = (VulkanTextureHandle) textureIndex;
		if(vkTexture.getImageViewId() == VK_NULL_HANDLE) return;

		int stagingPos = prepareStagingData(data);

		VkBufferImageCopy.Buffer region = VkBufferImageCopy.create(1);
		region.get(0).imageOffset().set(left, bottom, 0);
		region.get(0).imageExtent().set(width, height, 1);
		region.get(0).imageSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1);
		region.get(0).bufferOffset(stagingPos).bufferRowLength(width);

		changeLayout(vkTexture, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, true);
		vkCmdCopyBufferToImage(memCommandBuffer, stagingBuffers.get(stagingBufferIndex).getBufferIdVk(), vkTexture.getTextureIdVk(), VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region);
		changeLayout(vkTexture, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, true);
	}

	private static final String REP_TEXTURE_MARKER = "replaced-texture";

	@Override
	public TextureHandle resizeTexture(TextureHandle textureIndex, int width, int height, ShortBuffer data) {
		if(textureIndex == null) return null;

		((VulkanTextureHandle)textureIndex).setDestroy();
		TextureHandle texture = generateTexture(width, height, data, REP_TEXTURE_MARKER);
		((VulkanTextureHandle)texture).replace(textureIndex);
		return texture;
	}


	private final VkImageMemoryBarrier.Buffer layoutTransition = VkImageMemoryBarrier.create(1)
			.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
			.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
			.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
			.srcAccessMask(VK_ACCESS_MEMORY_WRITE_BIT|VK_ACCESS_MEMORY_READ_BIT)
			.dstAccessMask(VK_ACCESS_MEMORY_WRITE_BIT|VK_ACCESS_MEMORY_READ_BIT);

	private void changeLayout(VulkanTextureHandle texture, int oldLayout, int newLayout, boolean memOrFB) {
		if(!commandBufferRecording) return;

		layoutTransition.image(texture.getTextureIdVk())
				.oldLayout(oldLayout)
				.newLayout(newLayout);

		layoutTransition.subresourceRange().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1);

		vkCmdPipelineBarrier(memOrFB?memCommandBuffer:fbCommandBuffer, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0, null, null, layoutTransition);
	}

	private final PointerBuffer map_buffer_bfr = BufferUtils.createPointerBuffer(1);


	protected int usedStagingMemory = 0;
	protected int stagingBufferIndex = 0;
	protected final List<VulkanBufferHandle> stagingBuffers = new ArrayList<>();

	private int prepareStagingData(Buffer data) {
		ByteBuffer bdata = (data instanceof ByteBuffer)?(ByteBuffer)data : null;
		ShortBuffer sdata = (data instanceof ShortBuffer)?(ShortBuffer)data : null;


		int size;
		if(bdata != null) size = bdata.remaining();
		else if(sdata != null) size = sdata.remaining()*2;
		else throw new Error("Not yet implemented Buffer variant: " + data.getClass().getName());

		do {
			if(stagingBuffers.size() == stagingBufferIndex) {
				int newSize = 1024*(1<<stagingBufferIndex); // aka 1kB * 2^index

				if(newSize < size) newSize = size; // don't create a too small buffer

				stagingBuffers.add(stagingBufferIndex, createBuffer(newSize, STAGING_BUFFER));
			} else {
				if(usedStagingMemory+size > stagingBuffers.get(stagingBufferIndex).getSize()) {
					stagingBufferIndex++;
					usedStagingMemory = 0;
				} else {
					break;
				}
			}
		} while(true);

		VulkanBufferHandle currentStagingBuffer = stagingBuffers.get(stagingBufferIndex);

		vmaMapMemory(allocators[currentStagingBuffer.getType()], currentStagingBuffer.getAllocation(), map_buffer_bfr);
		ByteBuffer mapped = MemoryUtil.memByteBuffer(map_buffer_bfr.get(0)+usedStagingMemory, currentStagingBuffer.getSize()-usedStagingMemory);

		if(bdata != null) mapped.put(bdata.asReadOnlyBuffer());
		if(sdata != null) mapped.asShortBuffer().put(sdata.asReadOnlyBuffer());

		vmaUnmapMemory(allocators[currentStagingBuffer.getType()], currentStagingBuffer.getAllocation());

		int offset = usedStagingMemory;
		usedStagingMemory += size;
		// bufferOffset must be a multiple of 4
		usedStagingMemory -= -usedStagingMemory%4;

		return offset;
	}

	private final VkBufferCopy.Buffer update_buffer_region = VkBufferCopy.create(1);

	@Override
	public void updateBufferAt(BufferHandle handle, int pos, ByteBuffer data) {
		if(!commandBufferRecording || handle == null || data.remaining() == 0) return;

		VulkanBufferHandle vkBuffer = (VulkanBufferHandle)handle;

		if(vkBuffer.getType() == STATIC_BUFFER) {

			if (data.remaining() >= 65536) {
				int writePos = prepareStagingData(data);
				update_buffer_region.get(0).set(writePos, pos, data.remaining());
				vkCmdCopyBuffer(memCommandBuffer, stagingBuffers.get(stagingBufferIndex).getBufferIdVk(), vkBuffer.getBufferIdVk(), update_buffer_region);
			} else {
				vkCmdUpdateBuffer(memCommandBuffer, vkBuffer.getBufferIdVk(), pos, data);
			}

			syncQueues(vkBuffer.getEvent(), vkBuffer.getBufferIdVk());
		} else {
			vmaMapMemory(allocators[vkBuffer.getType()], vkBuffer.getAllocation(), map_buffer_bfr);
			ByteBuffer mapped = MemoryUtil.memByteBuffer(map_buffer_bfr.get(0), vkBuffer.getSize());
			mapped.put(data.asReadOnlyBuffer());

			vmaUnmapMemory(allocators[vkBuffer.getType()], vkBuffer.getAllocation());
			vmaFlushAllocation(allocators[vkBuffer.getType()], vkBuffer.getAllocation(), pos, data.remaining());
		}
	}

	@Override
	public void updateBufferAt(BufferHandle handle, List<Integer> pos, List<Integer> len, ByteBuffer data) {
		if(!commandBufferRecording || handle == null) return;

		VulkanBufferHandle vkBuffer = (VulkanBufferHandle)handle;

		int writePos = prepareStagingData(data);
		int count = pos.size();
		VkBufferCopy.Buffer update_buffer_regions = VkBufferCopy.create(count);
		for(int i = 0; i != count; i++) {
			int off = pos.get(i);
			update_buffer_regions.get(i).set(writePos+off, off, len.get(i));
		}

		vkCmdCopyBuffer(memCommandBuffer, stagingBuffers.get(stagingBufferIndex).getBufferIdVk(), vkBuffer.getBufferIdVk(), update_buffer_regions);
		syncQueues(vkBuffer.getEvent(), vkBuffer.getBufferIdVk());
	}

	private LongBuffer bufferBfr = BufferUtils.createLongBuffer(1);
	private PointerBuffer bufferAllocationBfr = BufferUtils.createPointerBuffer(1);

	private final VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.create()
			.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
			.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

	private final VmaAllocationCreateInfo bufferAllocInfo = VmaAllocationCreateInfo.create();

	private int consumedTexSlots = 0;

	private VulkanTextureHandle createTexture(int width, int height, int format, int usage, boolean color, String name) {
		VulkanUtils.createImage(this, width, height, format, usage, color, imageBfr, imageViewBfr, imageAllocationBfr);

		VulkanTextureHandle vkTexHandle = new VulkanTextureHandle(this, color?consumedTexSlots:-1, imageBfr.get(0), imageAllocationBfr.get(0), imageViewBfr.get(0));
		if(color && name != REP_TEXTURE_MARKER) consumedTexSlots++;
		if(!color) vkTexHandle.setInstalled(); // depth images cant be installed
		textures.add(vkTexHandle);
		return vkTexHandle;
	}

	private static final int STAGING_BUFFER = 0;
	private static final int STATIC_BUFFER = 1;
	private static final int DYNAMIC_BUFFER = 2;
	public static final int TEXTUREDATA_BUFFER = 2;
	public static final int READBACK_BUFFER = 3;

	protected VulkanMultiBufferHandle createMultiBuffer(int size, int type) {
		VulkanMultiBufferHandle vkMultiBfrHandle = new VulkanMultiBufferHandle(this, type, size);
		multiBuffers.add(vkMultiBfrHandle);
		return vkMultiBfrHandle;
	}
	protected VulkanBufferHandle createBuffer(int size, int type) {
		if(type == STAGING_BUFFER) {
			bufferCreateInfo.usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
			bufferAllocInfo.usage(VMA_MEMORY_USAGE_CPU_ONLY);
		} else if(type == STATIC_BUFFER) {
			bufferCreateInfo.usage(VK_BUFFER_USAGE_TRANSFER_DST_BIT|VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT|VK_BUFFER_USAGE_INDEX_BUFFER_BIT|VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
			bufferAllocInfo.usage(VMA_MEMORY_USAGE_GPU_ONLY);
		} else if(type == DYNAMIC_BUFFER) {
			bufferCreateInfo.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
			bufferAllocInfo.usage(VMA_MEMORY_USAGE_CPU_TO_GPU);
		} else if(type == READBACK_BUFFER) {
			bufferCreateInfo.usage(VK_BUFFER_USAGE_TRANSFER_DST_BIT);
			bufferAllocInfo.usage(VMA_MEMORY_USAGE_GPU_TO_CPU);
		}
		bufferCreateInfo.size(size);

		long event;
		try {
			event = VulkanUtils.createEvent(device);
		} catch(Throwable thrown) {
			thrown.printStackTrace();
			return null;
		}

		if(vmaCreateBuffer(allocators[type], bufferCreateInfo, bufferAllocInfo, bufferBfr, bufferAllocationBfr, null) < 0) {
			vkDestroyEvent(device, event, null);
			return null;
		}


		VulkanBufferHandle vkBfrHandle = new VulkanBufferHandle(this, type, bufferBfr.get(0), bufferAllocationBfr.get(0), event, size);
		buffers.add(vkBfrHandle);
		return vkBfrHandle;
	}


	private LongBuffer syncQueueBfr = BufferUtils.createLongBuffer(1);
	private VkBufferMemoryBarrier.Buffer synQueueArea = VkBufferMemoryBarrier.create(1)
			.sType(VK_STRUCTURE_TYPE_BUFFER_MEMORY_BARRIER)
			.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
			.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
			.size(VK_WHOLE_SIZE)
			.offset(0);

	private void syncQueues(long event, long buffer) {
		syncQueueBfr.put(0, event);
		vkCmdSetEvent(memCommandBuffer, event, VK_PIPELINE_STAGE_TRANSFER_BIT|VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT);
		synQueueArea.srcAccessMask(VK_ACCESS_MEMORY_WRITE_BIT).dstAccessMask(VK_ACCESS_MEMORY_READ_BIT).buffer(buffer);
		vkCmdWaitEvents(graphCommandBuffer, syncQueueBfr, VK_PIPELINE_STAGE_TRANSFER_BIT|VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT|VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT, null, synQueueArea, null);
	}

	@Override
	public BackgroundDrawHandle createBackgroundDrawCall(int vertices, TextureHandle texture) {
		VulkanBufferHandle vertexBfr = createBuffer(vertices*5*4, STATIC_BUFFER);
		VulkanBufferHandle colorBfr = createBuffer(vertices*4, STATIC_BUFFER);
		return new BackgroundDrawHandle(this, -1, texture, vertexBfr, colorBfr);
	}

	@Override
	public UnifiedDrawHandle createUnifiedDrawCall(int vertices, String name, TextureHandle texture, float[] data) {
		BufferHandle vertexBuffer = createBuffer(vertices*(texture!=null?4:2)*4, STATIC_BUFFER);
		if (data != null) {
			try(MemoryStack stack = MemoryStack.stackPush()) {
				ByteBuffer dataBfr = stack.malloc(data.length*4);
				dataBfr.asFloatBuffer().put(data);
				updateBufferAt(vertexBuffer, 0, dataBfr);
			}
		}

		return new UnifiedDrawHandle(this, -1, 0, vertices, texture, vertexBuffer);
	}

	@Override
	protected MultiDrawHandle createMultiDrawCall(String name, ManagedHandle source) {
		VulkanMultiBufferHandle drawCallBuffer = createMultiBuffer(MultiDrawHandle.MAX_CACHE_ENTRIES*12*4, DYNAMIC_BUFFER);
		drawCallBuffer.reset();
		return new MultiDrawHandle(this, managedHandles.size(), MultiDrawHandle.MAX_CACHE_ENTRIES, source, drawCallBuffer);
	}

	@Override
	public void clearDepthBuffer() {
		if(!commandBufferRecording) return;
		finishFrame();

		VkClearAttachment.Buffer clearAttachment = VkClearAttachment.create(1);
		clearAttachment.get(0).set(VK_IMAGE_ASPECT_DEPTH_BIT, 1, CLEAR_VALUES.get(1));
		VkClearRect.Buffer clearRect = VkClearRect.create(1).layerCount(1).baseArrayLayer(0);
		clearRect.rect().extent().set(fbWidth, fbHeight);

		vkCmdClearAttachments(graphCommandBuffer, clearAttachment, clearRect);
	}

	private long swapchain = VK_NULL_HANDLE;
	private long[] swapchainImages;
	private long[] swapchainViews;
	private long[] framebuffers;
	private VkSurfaceCapabilitiesKHR surfaceCapabilities = VkSurfaceCapabilitiesKHR.create();
	private VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.create();
	private VkFramebufferCreateInfo framebufferCreateInfo = VkFramebufferCreateInfo.create();
	private VkImageViewCreateInfo swapchainImageViewCreateInfo = VkImageViewCreateInfo.create();

	private void destroySwapchainViews(int count) {
		if(swapchainViews == null) return;
		if(count == -1) count = swapchainViews.length;

		for(int i = 0; i != count; i++) {
			vkDestroyImageView(device, swapchainViews[i], null);
		}
		swapchainViews = null;
	}

	private void destroyFramebuffers(int count) {
		if(framebuffers == null) return;
		if(count == -1) count = framebuffers.length;

		for(int i = 0; i != count; i++) {
			vkDestroyFramebuffer(device, framebuffers[i], null);
		}
		framebuffers = null;
	}

	private VulkanTextureHandle depthImage = null;
	protected final VulkanBufferHandle globalUniformStagingBuffer;
	protected final VulkanBufferHandle globalUniformBuffer;
	private ByteBuffer globalUniformBufferData;
	private Matrix4f projMatrix = new Matrix4f();

	private int newWidth;
	private int newHeight;
	private boolean resizeScheduled = false;

	@Override
	public void resize(int width, int height) {
		newWidth = width;
		newHeight = height;
		resizeScheduled = true;
	}

	private void doResize(int width, int height) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			destroyFramebuffers(-1);
			destroySwapchainViews(-1);

			if (vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfaceCapabilities) != VK_SUCCESS) {
				return;
			}

			if (nativeScale == 0) {
				if (surfaceCapabilities.currentExtent().height() != -1) {
					nativeScale = ((float) surfaceCapabilities.currentExtent().height()) / height;
				} else {
					nativeScale = 1;
				}
			}

			fbWidth = (int) (width * nativeScale);
			fbHeight = (int) (height * nativeScale);
			int imageCount = surfaceCapabilities.minImageCount() + 1;

			VkExtent2D minDim = surfaceCapabilities.maxImageExtent();
			VkExtent2D maxDim = surfaceCapabilities.maxImageExtent();

			fbWidth = Math.max(Math.min(fbWidth, maxDim.width()), minDim.width());
			fbHeight = Math.max(Math.min(fbHeight, maxDim.height()), minDim.height());
			if (surfaceCapabilities.maxImageCount() != 0)
				imageCount = Math.min(imageCount, surfaceCapabilities.maxImageCount());

			swapchainCreateInfo.preTransform(surfaceCapabilities.currentTransform())
					.minImageCount(imageCount)
					.oldSwapchain(swapchain)
					.imageExtent()
					.width(fbWidth)
					.height(fbHeight);

			LongBuffer swapchainBfr = stack.callocLong(1);
			boolean error = vkCreateSwapchainKHR(device, swapchainCreateInfo, null, swapchainBfr) != VK_SUCCESS;
			vkDestroySwapchainKHR(device, swapchain, null);

			if (error) {
				swapchain = VK_NULL_HANDLE;
				return;
			} else {
				swapchain = swapchainBfr.get(0);
			}

			swapchainImages = VulkanUtils.getSwapchainImages(device, swapchain);
			if (swapchainImages == null) {
				vkDestroySwapchainKHR(device, swapchain, null);
				swapchain = VK_NULL_HANDLE;
				return;
			}

			if(depthImage != null) depthImage.setDestroy();
			depthImage = createTexture(fbWidth, fbHeight, VK_FORMAT_D32_SFLOAT, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, false, "depth-image");

			LongBuffer imageViewBfr = stack.callocLong(1);
			swapchainViews = new long[swapchainImages.length];
			for (int i = 0; i != swapchainImages.length; i++) {
				swapchainImageViewCreateInfo.image(swapchainImages[i]);


				long imageView;
				try {
					imageView = VulkanUtils.createImageView(device, swapchainImages[i], surfaceFormat, true, imageViewBfr);
				} catch(Throwable thrown) {
					thrown.printStackTrace();
					destroySwapchainViews(i);
					vkDestroySwapchainKHR(device, swapchain, null);
					swapchain = VK_NULL_HANDLE;
					return;
				}
				swapchainViews[i] = imageView;
			}



			framebufferCreateInfo.width(fbWidth)
					.height(fbHeight);

			LongBuffer framebufferBfr = stack.callocLong(1);
			framebuffers = new long[swapchainViews.length];
			for (int i = 0; i != swapchainViews.length; i++) {
				framebufferCreateInfo.pAttachments(stack.longs(swapchainViews[i], depthImage.getImageViewId()));

				if (vkCreateFramebuffer(device, framebufferCreateInfo, null, framebufferBfr) != VK_SUCCESS) {
					destroyFramebuffers(i);
					destroySwapchainViews(-1);
					vkDestroySwapchainKHR(device, swapchain, null);
					swapchain = VK_NULL_HANDLE;
				}

				framebuffers[i] = framebufferBfr.get(0);
			}

			backgroundPipeline.resize(fbWidth, fbHeight);
			lineUnifiedPipeline.resize(fbWidth, fbHeight);
			unifiedMultiPipeline.resize(fbWidth, fbHeight);
			unifiedArrayPipeline.resize(fbWidth, fbHeight);
			unifiedPipeline.resize(fbWidth, fbHeight);
			renderPassBeginInfo.renderArea().extent().width(fbWidth).height(fbHeight);

			projMatrix.identity();
			projMatrix.scale(1.0f, -1.0f, 1.0f);
			projMatrix.ortho(0,  width,0, height, -1, 1, true);
			projMatrix.get(0, globalUniformBufferData);
		}
	}

	private int swapchainImageIndex = -1;
	private boolean commandBufferRecording = false;

	private final VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.create().sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO).pClearValues(CLEAR_VALUES);
	private final static VkClearValue.Buffer CLEAR_VALUES = VkClearValue.create(2); // only zeros is equal to black
	static {
		CLEAR_VALUES.get(1).depthStencil().set(1, 0);
	}

	private int installedManagedHandleCount = 0;


	private static final VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

	@Override
	public void startFrame() {
		if(swapchainImageIndex != -1) endFrame();

		if(resizeScheduled) {
			doResize(newWidth, newHeight);
			resizeScheduled = false;
		}

		closeMutex.acquireUninterruptibly();
		resourceMutex.acquireUninterruptibly();
		closeMutex.release();

		try(MemoryStack stack = MemoryStack.stackPush()) {
			super.startFrame();

			if(swapchain == VK_NULL_HANDLE) {
				swapchainImageIndex = -1;
				return;
			}

			Iterator<VulkanTextureHandle> iter = textures.iterator();
			while(iter.hasNext()) {
				VulkanTextureHandle texture = iter.next();

				if (texture.shouldBeDestroyed()) {
					texture.destroy();
				} else if (!texture.isInstalled()) {
					installTexture(texture, texture.getTextureId());
					texture.setInstalled();
				}
			}

			if(installedManagedHandleCount < managedHandles.size()) {
				for(int i = installedManagedHandleCount; i != managedHandles.size(); i++) {
					installUniformBuffer((VulkanBufferHandle) managedHandles.get(i).bufferHolder.vertices, 3, i, unifiedMultiPipeline);
				}

				installedManagedHandleCount = managedHandles.size();
			}

			IntBuffer swapchainImageIndexBfr = stack.callocInt(1);
			int err = vkAcquireNextImageKHR(device, swapchain, -1L, fetchFramebufferSemaphore, VK_NULL_HANDLE, swapchainImageIndexBfr);
			if(err != VK_SUBOPTIMAL_KHR && err != VK_SUCCESS) {
				swapchainImageIndex = -1;
				return;
			}

			swapchainImageIndex = swapchainImageIndexBfr.get(0);
			renderPassBeginInfo.framebuffer(framebuffers[swapchainImageIndex]);

			if(vkBeginCommandBuffer(graphCommandBuffer, commandBufferBeginInfo) != VK_SUCCESS) return;
			if(vkBeginCommandBuffer(memCommandBuffer, commandBufferBeginInfo) != VK_SUCCESS) {
				vkEndCommandBuffer(graphCommandBuffer);
				return;
			}
			commandBufferRecording = true;
			// reset staging buffer
			usedStagingMemory = 0;
			stagingBufferIndex = 0;
			globalAttrIndex = 0;
			multiBuffers.forEach(VulkanMultiBufferHandle::reset);

			vkCmdBeginRenderPass(graphCommandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
			lastPipeline = null;

			update_buffer_region.dstOffset(0).srcOffset(0).size(globalUniformBuffer.getSize());
			vkCmdCopyBuffer(memCommandBuffer, globalUniformStagingBuffer.getBufferIdVk(), globalUniformBuffer.getBufferIdVk(), update_buffer_region);
			syncQueues(globalUniformBuffer.getEvent(), globalUniformBuffer.getBufferIdVk());


			if(textDrawer == null) {
				textDrawer = new LWJGLTextDrawer(this, guiScale);
				VulkanTextureHandle texture = textures.stream().filter(t -> t.getTextureId()!=-1).findFirst().get();
				for (int i = 0; i != VulkanUtils.MAX_TEXTURE_COUNT; i++) installTexture(texture, i);
			}
		} finally {
			if(!commandBufferRecording) resourceMutex.release();
		}
	}

	private VulkanBufferHandle framebufferReadBack = null;
	private VkBufferImageCopy.Buffer readBackRegion = VkBufferImageCopy.create(1);
	private int rbWidth = -1, rbHeight = -1;

	private boolean fbCBrecording = false;

	private static final VkImageSubresourceRange CLEAR_SUBRESOURCE = VkImageSubresourceRange.calloc().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1);

	public void clearFramebuffer() {
		if(!commandBufferRecording) return;

		if(!fbCBrecording) {
			if(vkBeginCommandBuffer(fbCommandBuffer, commandBufferBeginInfo)!=VK_SUCCESS) return;
			fbCBrecording = true;
		}

		VulkanTextureHandle texture = new VulkanTextureHandle(this, -1, swapchainImages[swapchainImageIndex], 0, 0);


		changeLayout(texture, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, false);
		vkCmdClearColorImage(fbCommandBuffer, swapchainImages[swapchainImageIndex], VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, CLEAR_VALUES.get(0).color(), CLEAR_SUBRESOURCE);
		changeLayout(texture, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, false);
	}

	public void readFramebuffer(IntBuffer pixels, int width, int height) {
		if(!commandBufferRecording) return;

		if(width > fbWidth) width = fbWidth;
		if(height > fbHeight) height = fbHeight;

		if(rbWidth != width || rbHeight != height) {
			if(framebufferReadBack != null) {
				framebufferReadBack.destroy();
				buffers.remove(framebufferReadBack);
			}

			rbWidth = width;
			rbHeight = height;
			framebufferReadBack = createBuffer(4*rbHeight*rbWidth, READBACK_BUFFER);
		}

		readBackRegion.bufferOffset(0).bufferRowLength(width);
		readBackRegion.imageSubresource().set(VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1);
		readBackRegion.imageOffset().set(0, 0, 0);
		readBackRegion.imageExtent().set(width, height, 1);

		VulkanTextureHandle texture = new VulkanTextureHandle(this, -1, swapchainImages[swapchainImageIndex], 0, 0);

		if(vkBeginCommandBuffer(fbCommandBuffer, commandBufferBeginInfo) != VK_SUCCESS) {
			return;
		}
		fbCBrecording = true;

		changeLayout(texture, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, false);
		vkCmdCopyImageToBuffer(fbCommandBuffer, swapchainImages[swapchainImageIndex], VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, framebufferReadBack.getBufferIdVk(), readBackRegion);
		changeLayout(texture, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, false);
		vkCmdPipelineBarrier(fbCommandBuffer, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, null, null, null);
		vkCmdClearColorImage(fbCommandBuffer, swapchainImages[swapchainImageIndex], VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, CLEAR_VALUES.get(0).color(), CLEAR_SUBRESOURCE);
		changeLayout(texture, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, false);

		endFrame();

		PointerBuffer ptr = BufferUtils.createPointerBuffer(1);
		Vma.vmaMapMemory(allocators[READBACK_BUFFER], framebufferReadBack.getAllocation(), ptr);
		ByteBuffer mapped = MemoryUtil.memByteBuffer(ptr.get(0), framebufferReadBack.getSize());
		IntBuffer mappedPixels = mapped.asIntBuffer();
		int[] line = new int[width];
		for(int i = 0; i != height; i++) {
			mappedPixels.position(i*width);
			mappedPixels.get(line);
			pixels.position((height-i-1)*width);
			pixels.put(line);
		}
		pixels.rewind();
		Vma.vmaUnmapMemory(allocators[READBACK_BUFFER], framebufferReadBack.getAllocation());
	}

	public void endFrame() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			boolean cmdBfrSend = false;
			if(commandBufferRecording) {
				updateBufferAt(globalUniformStagingBuffer, 0, globalUniformBufferData);

				vkCmdEndRenderPass(graphCommandBuffer);
				vkEndCommandBuffer(graphCommandBuffer);
				vkEndCommandBuffer(memCommandBuffer);
				if(fbCBrecording) vkEndCommandBuffer(fbCommandBuffer);
				commandBufferRecording = false;

				VkSubmitInfo graphSubmitInfo = VkSubmitInfo.callocStack(stack)
						.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
						.pSignalSemaphores(stack.longs(presentFramebufferSemaphore));
				if(fbCBrecording) {
					graphSubmitInfo.pCommandBuffers(stack.pointers(memCommandBuffer.address(), graphCommandBuffer.address(), fbCommandBuffer.address()));
					fbCBrecording = false;
				} else {
					graphSubmitInfo.pCommandBuffers(stack.pointers(memCommandBuffer.address(), graphCommandBuffer.address()));
				}

				if(swapchainImageIndex != -1) {
					graphSubmitInfo.pWaitSemaphores(stack.longs(fetchFramebufferSemaphore))
							.pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
							.waitSemaphoreCount(1);
				}

				if(vkQueueSubmit(graphicsQueue, graphSubmitInfo, VK_NULL_HANDLE) != VK_SUCCESS) {
					// whatever
					System.out.println("Could not submit CommandBuffers.");
				} else {
					cmdBfrSend = true;
				}
			}

			if(swapchainImageIndex != -1) {
				VkPresentInfoKHR presentInfo = VkPresentInfoKHR.callocStack(stack)
						.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
						.pImageIndices(stack.ints(swapchainImageIndex))
						.swapchainCount(1)
						.pSwapchains(stack.longs(swapchain));
				if(cmdBfrSend) presentInfo.pWaitSemaphores(stack.longs(presentFramebufferSemaphore));
				if(vkQueuePresentKHR(presentQueue, presentInfo) != VK_SUCCESS) {
					// should not happen but we can't do anything about it
				}
				vkQueueWaitIdle(presentQueue);
				swapchainImageIndex = -1;
			}
		} finally {
			resourceMutex.release();
		}
	}

	private VulkanPipeline lastPipeline = null;

	private void bind(VulkanPipeline pipeline) {
		if(pipeline != lastPipeline) {
			pipeline.bind(graphCommandBuffer, frameIndex);
			lastPipeline = pipeline;
		}
	}

	private final VkDescriptorImageInfo.Buffer install_texture_image = VkDescriptorImageInfo.create(1)
			.imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

	private final VkWriteDescriptorSet.Buffer install_texture_write = VkWriteDescriptorSet.create(1)
			.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
			.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
			.dstBinding(1)
			.descriptorCount(1)
			.pImageInfo(install_texture_image);

	private void installTexture(VulkanTextureHandle texture, int id) {
		install_texture_image.imageView(texture.getImageViewId())
				.sampler(samplers[texture.getType().ordinal()]);
		install_texture_write.dstArrayElement(id);
		backgroundPipeline.update(install_texture_write);
		lineUnifiedPipeline.update(install_texture_write);
		unifiedArrayPipeline.update(install_texture_write);
		unifiedMultiPipeline.update(install_texture_write);
		unifiedPipeline.update(install_texture_write);
	}


	private final VkDescriptorBufferInfo.Buffer install_uniform_buffer = VkDescriptorBufferInfo.create(1).range(VK_WHOLE_SIZE);

	private final VkWriteDescriptorSet.Buffer install_uniform_buffer_write = VkWriteDescriptorSet.create(1)
			.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
			.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
			.pBufferInfo(install_uniform_buffer)
			.descriptorCount(1)
			.dstArrayElement(0);

	private void installUniformBuffer(VulkanBufferHandle buffer, int binding) {
		install_uniform_buffer_write.dstBinding(binding);
		install_uniform_buffer.buffer(buffer.getBufferIdVk());

		backgroundPipeline.update(install_uniform_buffer_write);
		lineUnifiedPipeline.update(install_uniform_buffer_write);
		unifiedArrayPipeline.update(install_uniform_buffer_write);
		unifiedMultiPipeline.update(install_uniform_buffer_write);
		unifiedPipeline.update(install_uniform_buffer_write);

	}

	private void installUniformBuffer(VulkanBufferHandle buffer, int binding, int index, VulkanPipeline pipeline) {
		install_uniform_buffer_write.dstBinding(binding);
		install_uniform_buffer_write.dstArrayElement(index);
		install_uniform_buffer.buffer(buffer.getBufferIdVk());

		pipeline.update(install_uniform_buffer_write);

	}
}
