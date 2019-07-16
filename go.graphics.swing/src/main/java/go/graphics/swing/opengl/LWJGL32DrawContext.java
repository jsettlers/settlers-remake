package go.graphics.swing.opengl;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

import go.graphics.GL32DrawContext;
import go.graphics.BufferHandle;
import go.graphics.TextureHandle;

public class LWJGL32DrawContext extends LWJGL20DrawContext implements GL32DrawContext {
	public LWJGL32DrawContext(GLCapabilities caps, boolean debug) {
		super(caps, debug);
		multiVAO = ARBVertexArrayObject.glGenVertexArrays();
	}

	@Override
	void init() {
		super.init();

		prog_multi = new ShaderProgram30("multi");
	}

	private int multiVAO;

	private ShaderProgram prog_multi;

	@Override
	public void drawMultiUnified2D(TextureHandle texture, BufferHandle geometry, BufferHandle drawCalls, int drawCallCount) {
		bindTexture(texture);
		useProgram(prog_multi);

		bindFormat(multiVAO);
		bindGeometry(drawCalls);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);

		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 9*4, 0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 9*4, 3 * 4);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 9*4, 7 * 4);
		GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, false, 9*4, 8 * 4);

		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, geometry.getBufferId());

		GL11.glDrawArrays(GL11.GL_POINTS, 0, drawCallCount);
		bindFormat(0);
	}

	protected class ShaderProgram30 extends LWJGL20DrawContext.ShaderProgram {

		public int geometryData;

		protected ShaderProgram30(String name) {
			super(name);

			geometryData = GL31.glGetUniformBlockIndex(program, "geometryDataBuffer");
			GL31.glUniformBlockBinding(program, geometryData, 0);
		}
	}
}
