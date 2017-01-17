/*******************************************************************************
 * Copyright (c) 2015
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
package go.graphics.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES11;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import go.graphics.android.AndroidGLHandle.AndroidGeometryHandle;
import go.graphics.android.AndroidGLHandle.AndroidTextureHandle;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class AndroidContext implements GLDrawContext {
	private final Context context;

	private TextureHandle lastTexture = null;

	public AndroidContext(Context context) {
		this.context = context;
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
		quadDatas = new float[3 * 6];
		quadDatas[0] = x1;
		quadDatas[1] = y1;
		quadDatas[2] = 0;
		quadDatas[3] = x2;
		quadDatas[4] = y1;
		quadDatas[5] = 0;
		quadDatas[6] = x1;
		quadDatas[7] = y2;
		quadDatas[8] = 0;
		quadDatas[9] = x1;
		quadDatas[10] = y2;
		quadDatas[11] = 0;
		quadDatas[12] = x2;
		quadDatas[13] = y1;
		quadDatas[14] = 0;
		quadDatas[15] = x2;
		quadDatas[16] = y2;
		quadDatas[17] = 0;

		glBindTexture(null);
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(quadDatas);

		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 3 * 4, floatBuff);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, quadDatas.length / 3);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void glPushMatrix() {
		GLES10.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GLES10.glTranslatef(x, y, z);
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GLES10.glScalef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		GLES10.glPopMatrix();
	}

	@Override
	public void color(float red, float green, float blue, float alpha) {
		GLES10.glColor4f(red, green, blue, alpha);
	}

	private FloatBuffer reuseableBuffer = null;
	private ByteBuffer quadEleementBuffer;
	private FloatBuffer reuseableBufferDuplicate;

	private FloatBuffer generateTemporaryFloatBuffer(float[] points) {
		int floatCount = points.length;
		FloatBuffer b = createReusedBuffer(floatCount);
		b.put(points);
		b.position(0);
		return b;
	}

	private FloatBuffer createReusedBuffer(int floatCount) {
		if (reuseableBuffer == null
				|| reuseableBuffer.position(0).capacity() < floatCount) {
			ByteBuffer quadPoints = ByteBuffer.allocateDirect(floatCount * 4);
			quadPoints.order(ByteOrder.nativeOrder());
			reuseableBuffer = quadPoints.asFloatBuffer();
			reuseableBufferDuplicate = reuseableBuffer.duplicate();
		} else {
			reuseableBuffer.position(0);
		}
		return reuseableBuffer;
	}

	@Override
	public void drawLine(float[] points, boolean loop) {
		if (points.length % 3 != 0) {
			throw new IllegalArgumentException(
					"Point array length needs to be multiple of 3.");
		}
		glBindTexture(null);
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(points);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuff);
		GLES10.glDrawArrays(loop ? GLES10.GL_LINE_LOOP : GLES10.GL_LINE_STRIP,
				0, points.length / 3);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	}

	private void glBindTexture(TextureHandle texture) {
		if (texture != lastTexture) {
			int id;
			if (texture == null) {
				id = 0;
			} else {
				id = texture.getInternalId();
			}
			GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	@Override
	public void drawQuadWithTexture(TextureHandle textureid, float[] geometry) {
		if (quadEleementBuffer == null) {
			generateQuadElementBuffer();
		}
		quadEleementBuffer.position(0);
		glBindTexture(textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		FloatBuffer texbuffer = reuseableBufferDuplicate;
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, texbuffer);

		GLES10.glDrawElements(GLES10.GL_TRIANGLES, 6, GLES10.GL_UNSIGNED_BYTE,
				quadEleementBuffer);
	}

	private void generateQuadElementBuffer() {
		quadEleementBuffer = ByteBuffer.allocateDirect(6);
		quadEleementBuffer.put((byte) 0);
		quadEleementBuffer.put((byte) 1);
		quadEleementBuffer.put((byte) 3);
		quadEleementBuffer.put((byte) 3);
		quadEleementBuffer.put((byte) 1);
		quadEleementBuffer.put((byte) 2);
	}

	@Override
	public void drawTrianglesWithTexture(TextureHandle textureid, float[] geometry) {
		glBindTexture(textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		FloatBuffer texbuffer = reuseableBufferDuplicate;
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, texbuffer);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 5);
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid, float[] geometry) {
		glBindTexture(textureid);

		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 9 * 4, buffer);
		FloatBuffer texbuffer = reuseableBufferDuplicate;
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 9 * 4, texbuffer);
		FloatBuffer colorbuffer = buffer.duplicate(); // we need it selden enogh
														// to allocate a new one.
		colorbuffer.position(5);
		GLES10.glColorPointer(4, GLES10.GL_FLOAT, 9 * 4, colorbuffer);

		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 9);
		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid,
			ByteBuffer byteBuffer, int currentTriangles) {
		glBindTexture(textureid);

		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 6 * 4, byteBuffer);
		ByteBuffer texbuffer = byteBuffer.duplicate();
		texbuffer.position(3 * 4);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 6 * 4, texbuffer);
		ByteBuffer colorbuffer = byteBuffer.duplicate(); // we need it selden
															// enogh
															// to allocate a new
															// one.
		colorbuffer.position(5 * 4);
		GLES10.glColorPointer(4, GLES10.GL_UNSIGNED_BYTE, 6 * 4, colorbuffer);

		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, currentTriangles * 3);
		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);

	}

	private static int getPowerOfTwo(int value) {
		int guess = 1;
		while (guess < value) {
			guess *= 2;
		}
		return guess;
	}

	@Override
	public int makeWidthValid(int width) {
		return getPowerOfTwo(width);
	}

	@Override
	public int makeHeightValid(int height) {
		return getPowerOfTwo(height);
	}

	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data) {
		// 1 byte aligned.
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);

		TextureHandle texture = genTextureIndex();
		if (texture == null) {
			return null;
		}

		glBindTexture(texture);
		GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width,
				height, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_5_5_5_1,
				data);

		setTextureParameters();
		return texture;
	}

	private static AndroidTextureHandle genTextureIndex() {
		int[] textureIndexes = new int[1];
		GLES10.glGenTextures(1, textureIndexes, 0);
		return new AndroidTextureHandle(textureIndexes[0]);
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created and is bound.
	 */
	private static void setTextureParameters() {
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,
				GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,
				GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,
				GLES10.GL_REPEAT);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,
				GLES10.GL_REPEAT);
	}

	@Override
	public void updateTexture(TextureHandle textureIndex, int left, int bottom,
			int width, int height, ShortBuffer data) {
		glBindTexture(textureIndex);
		GLES10.glTexSubImage2D(GLES10.GL_TEXTURE_2D, 0, left, bottom, width,
				height, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_5_5_5_1, data);
	}

	public TextureHandle generateTextureAlpha(int width, int height) {
		// 1 byte aligned.
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);

		TextureHandle texture = genTextureIndex();
		if (texture == null) {
			return null;
		}

		ByteBuffer data = ByteBuffer.allocateDirect(width * height);
		while (data.hasRemaining()) {
			data.put((byte) 0);
		}
		data.rewind();

		glBindTexture(texture);
		GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_ALPHA, width,
				height, 0, GLES10.GL_ALPHA, GLES10.GL_UNSIGNED_BYTE, data);

		setTextureParameters();
		return texture;
	}

	public void updateTextureAlpha(TextureHandle textureIndex, int left, int bottom,
			int width, int height, ByteBuffer data) {
		glBindTexture(textureIndex);
		GLES10.glTexSubImage2D(GLES10.GL_TEXTURE_2D, 0, left, bottom, width,
				height, GLES10.GL_ALPHA, GLES10.GL_UNSIGNED_BYTE, data);
	}

	@Override
	public void glMultMatrixf(float[] matrix, int offset) {
		GLES10.glMultMatrixf(matrix, offset);
	}

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return AndroidTextDrawer.getInstance(size, this);
	}

	@Override
	public void drawQuadWithTexture(TextureHandle textureid, GeometryHandle geometryindex) {
		if (quadEleementBuffer == null) {
			generateQuadElementBuffer();
		}
		quadEleementBuffer.position(0);

		glBindTexture(textureid);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex.getInternalId());
		GLES11.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, 0);
		GLES11.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, 3 * 4);

		GLES11.glDrawElements(GLES10.GL_TRIANGLES, 6, GLES10.GL_UNSIGNED_BYTE,
				quadEleementBuffer);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	public void reinit(int width, int height) {
		GLES10.glMatrixMode(GLES10.GL_PROJECTION);
		GLES10.glLoadIdentity();
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
		GLES10.glLoadIdentity();

		GLES10.glScalef(2f / width, 2f / height, -.5f);
		// TODO: do not scale depth by 0.

		GLES10.glTranslatef(-width / 2, -height / 2, .25f);

		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

		GLES10.glAlphaFunc(GLES10.GL_GREATER, 0.1f);
		GLES10.glEnable(GLES10.GL_ALPHA_TEST);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

		GLES10.glDepthFunc(GLES10.GL_LEQUAL);
		GLES10.glEnable(GLES10.GL_DEPTH_TEST);

		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
	}

	@Override
	public void drawTrianglesWithTexture(TextureHandle textureid, GeometryHandle geometryindex,
			int triangleCount) {
		glBindTexture(textureid);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex.getInternalId());
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 5 * 4, 0);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 5 * 4, 3 * 4);

		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, triangleCount * 3);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public GeometryHandle generateGeometry(int bytes) {
		int[] vertexBuffIds = new int[] {
				0
		};
		GLES11.glGenBuffers(1, vertexBuffIds, 0);

		int vertexBufferId = vertexBuffIds[0];
		if (vertexBufferId == 0) {
			return null;
		}

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vertexBufferId);
		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, bytes, null,
				GLES11.GL_DYNAMIC_DRAW);
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
		return new AndroidGeometryHandle(vertexBufferId);
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid,
			GeometryHandle geometryindex, int triangleCount) {
		glBindTexture(textureid);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex.getInternalId());
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 6 * 4, 0);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 6 * 4, 3 * 4);
		GLES11.glColorPointer(4, GLES11.GL_UNSIGNED_BYTE, 6 * 4, 5 * 4);

		GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, triangleCount * 3);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry) {
		int bytes = 4 * geometry.length;

		GeometryHandle vertexBufferId = generateGeometry(bytes);

		GLBuffer buffer = startWriteGeometry(vertexBufferId);
		for (int i = 0; i < geometry.length; i++) {
			buffer.putFloat(geometry[i]);
		}
		endWriteGeometry(vertexBufferId);

		return vertexBufferId;
	}

	private GraphicsByteBuffer currentBuffer = null;
	private float[] quadDatas;

	@Override
	public GLBuffer startWriteGeometry(GeometryHandle geometryindex) {
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex.getInternalId());
		currentBuffer = new GraphicsByteBuffer();
		return currentBuffer;
	}

	@Override
	public void endWriteGeometry(GeometryHandle geometryindex) {
		if (currentBuffer != null) {
			currentBuffer.writeBuffer();
			currentBuffer.position(0);
		}
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	public static final class GraphicsByteBuffer implements
			GLDrawContext.GLBuffer {
		private static int BUFFER_LENGTH = 1024;
		private static ByteBuffer buffer = ByteBuffer.allocateDirect(
				BUFFER_LENGTH).order(ByteOrder.nativeOrder());
		private static int bufferstart = 0;
		private static int bufferlength = 0;

		private void assertBufferHas(int remaining) {
			if (bufferlength + remaining > BUFFER_LENGTH) {
				writeBuffer();
				bufferstart += bufferlength;
				bufferlength = 0;
				buffer.position(0);
			}
		}

		@Override
		public void putFloat(float f) {
			assertBufferHas(4);
			buffer.putFloat(f);
			bufferlength += 4;
		}

		@Override
		public void putByte(byte b) {
			assertBufferHas(1);
			buffer.put(b);
			bufferlength++;
		}

		@Override
		public void position(int position) {
			if (bufferstart + bufferlength != position) {
				if (bufferlength > 0) {
					writeBuffer();
				}
				bufferstart = position;
				bufferlength = 0;
				buffer.position(0);
			}
		}

		private void writeBuffer() {
			buffer.position(0);
			GLES11.glBufferSubData(GLES11.GL_ARRAY_BUFFER, bufferstart,
					bufferlength, buffer);
		}
	}

	public Context getAndroidContext() {
		return context;
	}

	public void invalidateContext() {
		// TODO invalidate context
	}
}
