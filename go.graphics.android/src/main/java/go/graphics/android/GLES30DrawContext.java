package go.graphics.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import go.graphics.AbstractColor;
import go.graphics.BufferHandle;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;

public class GLES30DrawContext extends GLES20DrawContext {
	public GLES30DrawContext(Context ctx) {
		super(ctx);

		gles3 = true;
	}


	@Override
	public void init() {
		super.init();

		prog_unified_array = new ShaderProgram("unifiedArray");
	}


	private static final FloatBuffer floats400 = ByteBuffer.allocateDirect(400*4).order(ByteOrder.nativeOrder()).asFloatBuffer();

	@Override
	public void drawUnified2DArray(BufferHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, boolean image, boolean shadow, float[] x, float[] y, float[] z, AbstractColor[] color, float[] intensity, int count) throws IllegalBufferException {
		useProgram(prog_unified_array);
		bindTexture(texture);

		GLES20.glUniform2f(prog_unified_array.uni_info, image ? 1 : 0, shadow ? 1 : 0);
		for (int i = 0; i != count; i++) {
			floats400.put(x[i]);
			floats400.put(y[i]);
			floats400.put(z[i]);
			floats400.put(intensity[i]);
		}
		floats400.rewind();
		GLES20.glUniform4fv(prog_unified_array.trans, count, floats400);

		for (int i = 0; i != count; i++) {
			floats400.put(color[i].red);
			floats400.put(color[i].green);
			floats400.put(color[i].blue);
			floats400.put(color[i].alpha);
		}
		floats400.rewind();
		GLES20.glUniform4fv(prog_unified_array.color, count, floats400);

		bindFormat(geometry.vao);
		GLES30.glDrawArraysInstanced(primitive, offset, vertices, count);
	}

	private ShaderProgram prog_unified_array;
}
