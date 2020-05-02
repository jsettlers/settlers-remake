package go.graphics.swing.vulkan;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

import go.graphics.EPrimitiveType;
import go.graphics.GLDrawContext;

import static org.lwjgl.vulkan.VK10.*;

public abstract class VulkanPipeline {
	private long pipeline = VK_NULL_HANDLE;
	protected long pipelineLayout = VK_NULL_HANDLE;
	protected VulkanDrawContext dc;

	protected VkPipelineLayoutCreateInfo pipelineLayoutCreateInfo; // MUST NOT BE ALLOCATED FROM STACK
	protected LongBuffer setLayouts = BufferUtils.createLongBuffer(1).put(0, 0);
	protected long descSet = 0;
	protected ByteBuffer writtenPushConstantBfr;
	protected ByteBuffer pushConstantBfr;

	public VulkanPipeline(MemoryStack stack, VulkanDrawContext dc, String prefix, long descPool, long renderPass, int primitive) {
		this.dc = dc;

		long vertShader = VK_NULL_HANDLE;
		long fragShader = VK_NULL_HANDLE;
		try {
			vertShader = VulkanUtils.createShaderModule(stack, dc.device, prefix + ".vert");
			fragShader = VulkanUtils.createShaderModule(stack, dc.device, prefix + ".frag");

			pipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.create()
					.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
					.pPushConstantRanges(getPushConstantRanges());

			pushConstantBfr = BufferUtils.createByteBuffer(pipelineLayoutCreateInfo.pPushConstantRanges().size()-4);
			writtenPushConstantBfr = BufferUtils.createByteBuffer(pushConstantBfr.capacity());

			VkDescriptorSetLayoutBinding.Buffer bindings = getDescriptorSetLayoutBindings();
			if(bindings != null) {
				VulkanUtils.createDescriptorSetLayout(stack, dc.device, bindings, setLayouts);
				pipelineLayoutCreateInfo.pSetLayouts(setLayouts);
			}

			VkPipelineVertexInputStateCreateInfo inputStateCreateInfo = getVertexInputState(stack);
			writtenBfrs = new long[inputStateCreateInfo.vertexBindingDescriptionCount()];
			Arrays.fill(writtenBfrs, VK_NULL_HANDLE);

			pipelineLayout = VulkanUtils.createPipelineLayout(stack, dc.device, pipelineLayoutCreateInfo);
			pipeline = VulkanUtils.createPipeline(stack, dc.device, primitive, pipelineLayout, renderPass, vertShader, fragShader, inputStateCreateInfo);

			descSet = VulkanUtils.createDescriptorSet(stack, dc.device, descPool, setLayouts);
		} finally {
			if(vertShader != VK_NULL_HANDLE) VK10.vkDestroyShaderModule(dc.device, vertShader, null);
			if(fragShader != VK_NULL_HANDLE) VK10.vkDestroyShaderModule(dc.device, fragShader, null);

			if(pipeline == VK_NULL_HANDLE) destroy();
		}
	}

	protected abstract VkPushConstantRange.Buffer getPushConstantRanges();
	protected abstract VkPipelineVertexInputStateCreateInfo getVertexInputState(MemoryStack stack);
	protected abstract VkDescriptorSetLayoutBinding.Buffer getDescriptorSetLayoutBindings();

	public void destroy() {
		if(pipeline != VK_NULL_HANDLE) vkDestroyPipeline(dc.device, pipeline, null);
		if(pipelineLayout != VK_NULL_HANDLE) vkDestroyPipelineLayout(dc.device, pipelineLayout, null);
		if(setLayouts != null) while(setLayouts.hasRemaining()) vkDestroyDescriptorSetLayout(dc.device, setLayouts.get(), null);
	}

	private VkViewport.Buffer viewportUpdate = VkViewport.create(1);
	private VkRect2D.Buffer scissorUpdate = VkRect2D.create(1);
	private long frame = -1;

	public void resize(int width, int height) {
		viewportUpdate.width(width).height(height).minDepth(0).maxDepth(1);
		scissorUpdate.extent().width(width).height(height);
	}

	public void bind(VkCommandBuffer commandBuffer, long frame) {
		vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
		vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipelineLayout, 0, new long[] {descSet}, null);

		if(writtenBfrs.length>0 && writtenBfrs[0] != VK_NULL_HANDLE) {
			vkCmdBindVertexBuffers(commandBuffer, 0, writtenBfrs, new long[writtenBfrs.length]);
		}

		if(this.frame != frame) {
			vkCmdSetViewport(commandBuffer, 0, viewportUpdate);
			vkCmdSetScissor(commandBuffer, 0, scissorUpdate);
			this.frame = frame;
		}

		vkCmdPushConstants(commandBuffer, pipelineLayout, VK_SHADER_STAGE_ALL_GRAPHICS, 0, new int[] {dc.globalAttrIndex});
		vkCmdPushConstants(commandBuffer, pipelineLayout, VK_SHADER_STAGE_ALL_GRAPHICS, 4, writtenPushConstantBfr);
	}

	public void pushConstants(VkCommandBuffer commandBuffer) {
		if(pushConstantBfr.compareTo(writtenPushConstantBfr) != 0) {
			vkCmdPushConstants(commandBuffer, pipelineLayout, VK_SHADER_STAGE_ALL_GRAPHICS, 4, pushConstantBfr);
			writtenPushConstantBfr.put(pushConstantBfr);
			writtenPushConstantBfr.rewind();
			pushConstantBfr.rewind();
		}
	}

	public void update(VkWriteDescriptorSet.Buffer write) {
		int pos = write.position();
		int count = write.remaining();
		for(int i = 0; i != count; i++) write.get(i+pos).dstSet(descSet);

		vkUpdateDescriptorSets(dc.device, write, null);
	}

	private long[] writtenBfrs;

	public void bindVertexBuffers(VkCommandBuffer commandBuffer, long... bfrs) {
		for(int i = 0; i != bfrs.length; i++) {
			if(writtenBfrs[i] != bfrs[i]) {
				vkCmdBindVertexBuffers(commandBuffer, 0, bfrs, new long[bfrs.length]);
				writtenBfrs = bfrs;
				return;
			}
		}
	}

	public static class BackgroundPipeline extends VulkanPipeline {

		@Override
		protected VkPushConstantRange.Buffer getPushConstantRanges() {
			VkPushConstantRange.Buffer pushConstantRanges = VkPushConstantRange.create(1);
			pushConstantRanges.get(0).set(VK_SHADER_STAGE_ALL_GRAPHICS, 0, 2*4);
			return pushConstantRanges;
		}

		@Override
		protected VkDescriptorSetLayoutBinding.Buffer getDescriptorSetLayoutBindings() {
			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(3);
			bindings.get(0).set(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null);
			bindings.get(1).set(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VulkanUtils.MAX_TEXTURE_COUNT, VK_SHADER_STAGE_FRAGMENT_BIT, null);
			bindings.get(2).set(2, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null); // height matrix
			return bindings;
		}

		@Override
		protected VkPipelineVertexInputStateCreateInfo getVertexInputState(MemoryStack stack) {
			VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.callocStack(2, stack);
			bindings.get(0).set(0, 5*4, VK_VERTEX_INPUT_RATE_VERTEX);
			bindings.get(1).set(1, 1*4, VK_VERTEX_INPUT_RATE_VERTEX);

			VkVertexInputAttributeDescription.Buffer attributes = VkVertexInputAttributeDescription.callocStack(3, stack);
			attributes.get(0).set(0, 0, VK_FORMAT_R32G32B32_SFLOAT, 0);
			attributes.get(1).set(1, 0, VK_FORMAT_R32G32_SFLOAT, 3*4);
			attributes.get(2).set(2, 1, VK_FORMAT_R32_SFLOAT, 0);

			return VkPipelineVertexInputStateCreateInfo.callocStack(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
					.pVertexAttributeDescriptions(attributes)
					.pVertexBindingDescriptions(bindings);
		}

		public BackgroundPipeline(MemoryStack stack, VulkanDrawContext dc, long descPool, long renderPass) {
			super(stack, dc, "background", descPool, renderPass, EPrimitiveType.Triangle);
		}
	}

	public static class UnifiedPipeline extends VulkanPipeline {

		@Override
		protected VkPushConstantRange.Buffer getPushConstantRanges() {
			VkPushConstantRange.Buffer pushConstantRanges = VkPushConstantRange.create(1);
			pushConstantRanges.get(0).set(VK_SHADER_STAGE_ALL_GRAPHICS, 0, (2*4+2+4)*4);//(4*4+2+3)*4);
			return pushConstantRanges;
		}

		@Override
		protected VkDescriptorSetLayoutBinding.Buffer getDescriptorSetLayoutBindings() {
			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(3);
			bindings.get(0).set(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null);
			bindings.get(1).set(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VulkanUtils.MAX_TEXTURE_COUNT, VK_SHADER_STAGE_FRAGMENT_BIT, null);
			bindings.get(2).set(2, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null); // shadow depth
			return bindings;
		}

		@Override
		protected VkPipelineVertexInputStateCreateInfo getVertexInputState(MemoryStack stack) {
			VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.callocStack(2, stack);
			bindings.get(0).set(0, 4*4, VK_VERTEX_INPUT_RATE_VERTEX);
			bindings.get(1).set(1, 2*4, VK_VERTEX_INPUT_RATE_VERTEX);

			VkVertexInputAttributeDescription.Buffer attributes = VkVertexInputAttributeDescription.callocStack(3, stack);
			attributes.get(0).set(0, 0, VK_FORMAT_R32G32_SFLOAT, 0);
			attributes.get(1).set(1, 0, VK_FORMAT_R32G32_SFLOAT, 2*4);
			attributes.get(2).set(2, 1, VK_FORMAT_R32G32_SFLOAT, 0);

			return VkPipelineVertexInputStateCreateInfo.callocStack(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
					.pVertexAttributeDescriptions(attributes)
					.pVertexBindingDescriptions(bindings);
		}

		public UnifiedPipeline(MemoryStack stack, VulkanDrawContext dc, long descPool, long renderPass, int primitive) {
			super(stack, dc, "unified", descPool, renderPass, primitive);
		}
	}

	public static class UnifiedArrayPipeline extends VulkanPipeline {

		@Override
		protected VkPushConstantRange.Buffer getPushConstantRanges() {
			VkPushConstantRange.Buffer pushConstantRanges = VkPushConstantRange.create(1);
			pushConstantRanges.get(0).set(VK_SHADER_STAGE_ALL_GRAPHICS, 0, 2*4);
			return pushConstantRanges;
		}

		@Override
		protected VkPipelineVertexInputStateCreateInfo getVertexInputState(MemoryStack stack) {
			VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.callocStack(3, stack);
			bindings.get(0).set(0, 4*4, VK_VERTEX_INPUT_RATE_VERTEX);
			bindings.get(1).set(1, 2*4, VK_VERTEX_INPUT_RATE_VERTEX);
			bindings.get(2).set(2, 4*4, VK_VERTEX_INPUT_RATE_INSTANCE);

			VkVertexInputAttributeDescription.Buffer attributes = VkVertexInputAttributeDescription.callocStack(5, stack);
			attributes.get(0).set(0, 0, VK_FORMAT_R32G32_SFLOAT, 0);
			attributes.get(1).set(1, 0, VK_FORMAT_R32G32_SFLOAT, 2*4);
			attributes.get(2).set(2, 1, VK_FORMAT_R32G32_SFLOAT, 0);
			attributes.get(3).set(3, 2, VK_FORMAT_R32G32B32A32_SFLOAT, 0);
			attributes.get(4).set(4, 2, VK_FORMAT_R32G32B32A32_SFLOAT, 4*4*100);

			return VkPipelineVertexInputStateCreateInfo.callocStack(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
					.pVertexAttributeDescriptions(attributes)
					.pVertexBindingDescriptions(bindings);
		}

		@Override
		protected VkDescriptorSetLayoutBinding.Buffer getDescriptorSetLayoutBindings() {
			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(3);
			bindings.get(0).set(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null);
			bindings.get(1).set(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VulkanUtils.MAX_TEXTURE_COUNT, VK_SHADER_STAGE_FRAGMENT_BIT, null);
			bindings.get(2).set(2, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null); // shadow depth
			return bindings;
		}

		public UnifiedArrayPipeline(MemoryStack stack, VulkanDrawContext dc, long descPool, long renderPass) {
			super(stack, dc, "unified-array", descPool, renderPass, EPrimitiveType.Quad);
		}
	}

    public static class UnifiedMultiPipeline extends VulkanPipeline {

		@Override
		protected VkPushConstantRange.Buffer getPushConstantRanges() {
			VkPushConstantRange.Buffer pushConstantRanges = VkPushConstantRange.create(1);
			pushConstantRanges.get(0).set(VK_SHADER_STAGE_ALL_GRAPHICS, 0, 3*4);
			return pushConstantRanges;
		}

		@Override
		protected VkPipelineVertexInputStateCreateInfo getVertexInputState(MemoryStack stack) {
			VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.callocStack(1, stack);
			bindings.get(0).set(0, 12*4, VK_VERTEX_INPUT_RATE_INSTANCE);

			VkVertexInputAttributeDescription.Buffer attributes = VkVertexInputAttributeDescription.callocStack(4, stack);
			attributes.get(0).set(0, 0, VK_FORMAT_R32G32B32_SFLOAT, 0);
			attributes.get(1).set(1, 0, VK_FORMAT_R32G32_SFLOAT, 3*4);
			attributes.get(2).set(2, 0, VK_FORMAT_R32G32B32A32_SFLOAT, 5*4);
			attributes.get(3).set(3, 0, VK_FORMAT_R32G32B32_SFLOAT, 9*4);

			return VkPipelineVertexInputStateCreateInfo.callocStack(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
					.pVertexAttributeDescriptions(attributes)
					.pVertexBindingDescriptions(bindings);
		}

		@Override
		protected VkDescriptorSetLayoutBinding.Buffer getDescriptorSetLayoutBindings() {
			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(4);
			bindings.get(0).set(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null);
			bindings.get(1).set(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VulkanUtils.MAX_TEXTURE_COUNT, VK_SHADER_STAGE_FRAGMENT_BIT, null);
			bindings.get(2).set(2, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_ALL_GRAPHICS, null); // shadow depth
			bindings.get(3).set(3, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, GLDrawContext.MAX_CACHE_COUNT, VK_SHADER_STAGE_ALL_GRAPHICS, null); // geometry
			return bindings;
		}

		public UnifiedMultiPipeline(MemoryStack stack, VulkanDrawContext dc, long descPool, long renderPass) {
			super(stack, dc, "unified-multi", descPool, renderPass, EPrimitiveType.Quad);
		}
    }
}
