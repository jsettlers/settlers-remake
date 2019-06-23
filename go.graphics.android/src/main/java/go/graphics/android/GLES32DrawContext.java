package go.graphics.android;

import android.content.Context;
import android.opengl.GLES31;

import go.graphics.GL32DrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

public class GLES32DrawContext extends GLES20DrawContext implements GL32DrawContext {
	public GLES32DrawContext(Context ctx) {
		super(ctx);
	}

	@Override
	public void init() {
		super.init();

		int[] vaos = new int[1];
		GLES31.glGenVertexArrays(1, vaos, 0);
		multiVAO = vaos[0];
		prog_multi = new ShaderProgram30("multi");
	}

	private int multiVAO;

	private ShaderProgram prog_multi;

	@Override
	public void drawMultiUnified2D(TextureHandle texture, GeometryHandle geometry, GeometryHandle drawCalls, int drawCallCount) {
		bindTexture(texture);
		useProgram(prog_multi);

		bindFormat(multiVAO);
		bindGeometry(drawCalls);
		GLES31.glEnableVertexAttribArray(0);
		GLES31.glEnableVertexAttribArray(1);
		GLES31.glEnableVertexAttribArray(2);
		GLES31.glEnableVertexAttribArray(3);

		int call_size = drawCalls.getFormat().getBytesPerVertexSize();
		GLES31.glVertexAttribPointer(0, 3, GLES31.GL_FLOAT, false, call_size, 0);
		GLES31.glVertexAttribPointer(1, 4, GLES31.GL_FLOAT, false, call_size, 3 * 4);
		GLES31.glVertexAttribPointer(2, 1, GLES31.GL_FLOAT, false, call_size, 7 * 4);
		GLES31.glVertexAttribPointer(3, 1, GLES31.GL_FLOAT, false, call_size, 8 * 4);

		GLES31.glBindBufferBase(GLES31.GL_UNIFORM_BUFFER, 0, geometry.getInternalId());
		GLES31.glDrawArrays(GLES31.GL_POINTS, 0, drawCallCount);
		bindFormat(0);
	}

	protected class ShaderProgram30 extends GLES20DrawContext.ShaderProgram {

		public int geometryData;

		protected ShaderProgram30(String name) {
			super(name);

			geometryData = GLES31.glGetUniformBlockIndex(program, "geometryDataBuffer");
			GLES31.glUniformBlockBinding(program, geometryData, 0);
		}
	}
}
