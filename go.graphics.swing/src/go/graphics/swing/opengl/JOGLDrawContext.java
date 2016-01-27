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
package go.graphics.swing.opengl;

import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;
import go.graphics.swing.opengl.JOGLBufferHandle.JOGLGeometryHandle;
import go.graphics.swing.opengl.JOGLBufferHandle.JOGLTextureHandle;
import go.graphics.swing.text.JOGLTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * This is the draw context implementation for JOGL. OpenGL draw calles are mapped to the corresponding JOGL calls.
 * 
 * @author Michael Zangl
 *
 */
public class JOGLDrawContext implements GLDrawContext {

	private JOGLTextDrawer[] textDrawers = new JOGLTextDrawer[EFontSize
			.values().length];

	private static final int FLOATS_PER_COLORED_TRI_VERTEX = 9;
	private final GL2 gl2;
	private final boolean canUseVBOs;

	public JOGLDrawContext(GL2 gl2) {
		this.gl2 = gl2;
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		gl2.glAlphaFunc(GL2.GL_GREATER, 0.1f);
		gl2.glEnable(GL2.GL_ALPHA_TEST);
		gl2.glDepthFunc(GL2.GL_LEQUAL);
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		gl2.glEnable(GL2.GL_TEXTURE_2D);

		canUseVBOs = gl2.isExtensionAvailable("GL_ARB_vertex_buffer_object");
	}

	public void startFrame() {
		gl2.glClear(GL2.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void color(float r, float g, float b, float a) {
		gl2.glColor4f(r, g, b, a);
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
		ByteBuffer quadPoints = ByteBuffer.allocateDirect(4 * 2 * 4);
		quadPoints.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuff = quadPoints.asFloatBuffer();
		floatBuff.put(x1);
		floatBuff.put(y1);

		floatBuff.put(x1);
		floatBuff.put(y2);

		floatBuff.put(x2);
		floatBuff.put(y2);

		floatBuff.put(x2);
		floatBuff.put(y1);

		floatBuff.position(0);

		gl2.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, floatBuff);
		gl2.glDrawArrays(GL2.GL_QUADS, 0, 4);
		gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void drawLine(float[] points, boolean loop) {
		if (points.length % 3 != 0) {
			throw new IllegalArgumentException(
					"Point array length needs to be multiple of 3.");
		}
		ByteBuffer floatBuff = generateTemporaryFloatBuffer(points);

		gl2.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, floatBuff);
		gl2.glDrawArrays(loop ? GL2.GL_LINE_LOOP : GL2.GL_LINE_STRIP, 0,
				points.length / 3);
		gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	private ByteBuffer reuseableBuffer = null;
	private ArrayList<ByteBuffer> geometries = new ArrayList<ByteBuffer>();

	/**
	 * The global context valid flag. As soon as this is set to false, the context is not valid any more.
	 */
	private boolean contextValid = true;

	private ByteBuffer generateTemporaryFloatBuffer(float[] points) {
		FloatBuffer buffer;
		if (reuseableBuffer == null
				|| reuseableBuffer.capacity() < points.length * 4) {
			reuseableBuffer = ByteBuffer.allocateDirect(points.length * 4);
			reuseableBuffer.order(ByteOrder.nativeOrder());
		} else {
			reuseableBuffer.position(0);
		}

		buffer = reuseableBuffer.asFloatBuffer();
		buffer.put(points);
		buffer.position(0);
		return reuseableBuffer;
	}

	private static ByteBuffer genertateBuffer(float[] points) {
		ByteBuffer bb = ByteBuffer.allocateDirect(points.length * 4);
		bb.order(ByteOrder.nativeOrder());
		bb.asFloatBuffer().put(points);
		bb.position(0);
		return bb;
	}

	@Override
	public void glPushMatrix() {
		gl2.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		gl2.glTranslatef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		gl2.glPopMatrix();
	}

	@Deprecated
	public GL2 getGl2() {
		return gl2;
	}

	@Override
	public void glScalef(float x, float y, float z) {
		gl2.glScalef(x, y, z);
	}

	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data) {
		// 1 byte aligned.
		gl2.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		int[] textureIndexes = new int[1];
		gl2.glGenTextures(1, textureIndexes, 0);
		int texture = textureIndexes[0];
		if (texture == 0) {
			return null;
		}

		gl2.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl2.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB5_A1, width, height, 0,
				GL.GL_RGBA, GL.GL_UNSIGNED_SHORT_5_5_5_1, data);
		setTextureParameters();

		return new JOGLTextureHandle(this, texture);
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created and is bound.
	 */
	private void setTextureParameters() {
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
	}

	@Override
	public void updateTexture(TextureHandle texture, int left, int bottom,
			int width, int height, ShortBuffer data) throws IllegalBufferException {
		bindTexture(texture);
		gl2.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GL2.GL_RGBA, GL2.GL_UNSIGNED_SHORT_5_5_5_1, data);
	}

	private void bindTexture(TextureHandle texture) throws IllegalBufferException {
		int id;
		if (texture == null) {
			id = 0;
		} else {
			if (!texture.isValid()) {
				throw new IllegalBufferException("Texture handle is not valid: " + texture);
			}
			id = texture.getInternalId();
		}
		gl2.glBindTexture(GL.GL_TEXTURE_2D, id);
	}

	void deleteTexture(int textureIndex) {
		gl2.glDeleteTextures(1, new int[] {
				textureIndex
		}, 0);
	}

	private void bindArrayBuffer(GeometryHandle geometry) throws IllegalBufferException {
		int id;
		if (geometry == null) {
			id = 0;
		} else {
			if (!geometry.isValid()) {
				throw new IllegalBufferException("Geometry handle is not valid: " + geometry);
			}
			id = geometry.getInternalId();
		}
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
	}

	@Override
	public void drawQuadWithTexture(TextureHandle texture, float[] geometry) throws IllegalBufferException {
		ByteBuffer buffer = generateTemporaryFloatBuffer(geometry);

		drawQuadWithTexture(texture, buffer, geometry.length / 5);
	}

	private void drawQuadWithTexture(TextureHandle texture, ByteBuffer buffer, int len) throws IllegalBufferException {
		bindTexture(texture);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3 * 4);
		gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, buffer);
		gl2.glDrawArrays(GL2.GL_QUADS, 0, len);
	}

	@Override
	public void drawTrianglesWithTexture(TextureHandle texture, float[] geometry) throws IllegalBufferException {
		ByteBuffer buffer = generateTemporaryFloatBuffer(geometry);
		drawTrianglesWithTexture(texture, buffer, geometry.length / 5 / 3);
	}

	private void drawTrianglesWithTexture(TextureHandle texture, ByteBuffer buffer,
			int triangles) throws IllegalBufferException {
		bindTexture(texture);

		gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3 * 4);
		gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, buffer);
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, triangles * 3);
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle texture, float[] geometry) throws IllegalBufferException {
		ByteBuffer buffer = generateTemporaryFloatBuffer(geometry);
		drawTrianglesWithTextureColored(texture, buffer, geometry.length / 3
				/ FLOATS_PER_COLORED_TRI_VERTEX);
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle texture,
			ByteBuffer buffer, int triangles) throws IllegalBufferException {
		bindTexture(texture);

		gl2.glVertexPointer(3, GL2.GL_FLOAT, 6 * 4, buffer);
		buffer.position(3 * 4);
		gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 6 * 4, buffer);
		buffer.position(5 * 4);
		gl2.glColorPointer(4, GL2.GL_UNSIGNED_BYTE, 6 * 4, buffer);

		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, triangles * 3);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
	}

	@Override
	public int makeWidthValid(int width) {
		return TextureCalculator.supportedTextureSize(gl2, width);
	}

	@Override
	public int makeHeightValid(int height) {
		return TextureCalculator.supportedTextureSize(gl2, height);
	}

	@Override
	public void glMultMatrixf(float[] matrix, int offset) {
		gl2.glMultMatrixf(matrix, offset);
	}

	/**
	 * Gets a text drawer for the given text size.
	 * 
	 * @param size
	 *            The size for the drawer.
	 * @return An instance of a drawer for that size.
	 */
	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		if (textDrawers[size.ordinal()] == null) {
			textDrawers[size.ordinal()] = new JOGLTextDrawer(size, this);
		}
		return textDrawers[size.ordinal()];
	}

	@Override
	public void drawQuadWithTexture(TextureHandle texture, GeometryHandle geometry) throws IllegalBufferException {
		if (geometry == null) {
			throw new NullPointerException("Cannot draw a null geometry");
		}
		if (canUseVBOs) {
			bindTexture(texture);

			bindArrayBuffer(geometry);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, 0);
			gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, 3 * 4);

			gl2.glDrawArrays(GL2.GL_QUADS, 0, 4);

			bindArrayBuffer(null);
		} else {
			drawQuadWithTexture(texture, getGeometryBuffer(geometry), 4);
		}
	}

	@Override
	public void drawTrianglesWithTexture(TextureHandle texture, GeometryHandle geometry,
			int triangleCount) throws IllegalBufferException {
		if (canUseVBOs) {
			bindTexture(texture);

			bindArrayBuffer(geometry);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, 0);
			gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, 3 * 4);

			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, triangleCount * 3);

			bindArrayBuffer(null);
		} else {
			ByteBuffer buffer = getGeometryBuffer(geometry);
			buffer.rewind();
			drawTrianglesWithTexture(texture, buffer,
					buffer.remaining() / 5 / 4);
		}
	}

	private ByteBuffer getGeometryBuffer(GeometryHandle geometry) {
		return geometries.get(geometry.getInternalId());
	}

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle texture,
			GeometryHandle geometry, int triangleCount) throws IllegalBufferException {
		if (canUseVBOs) {
			bindTexture(texture);

			bindArrayBuffer(geometry);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 6 * 4, 0);
			gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 6 * 4, 3 * 4);
			gl2.glColorPointer(4, GL2.GL_UNSIGNED_BYTE, 6 * 4, 5 * 4);

			gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, triangleCount * 3);
			gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);

			bindArrayBuffer(null);
		} else {
			ByteBuffer buffer = getGeometryBuffer(geometry);
			drawTrianglesWithTextureColored(texture, buffer,
					buffer.remaining() / 4 / 5);
		}
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry) {
		if (canUseVBOs) {
			try {
				GeometryHandle geometryBuffer =
						generateGeometry(geometry.length * Buffers.SIZEOF_FLOAT);
				if (geometryBuffer == null) {
					return null;
				}

				GLBuffer buffer = startWriteGeometry(geometryBuffer);
				for (int i = 0; i < geometry.length; i++) {
					buffer.putFloat(geometry[i]);
				}
				endWriteGeometry(geometryBuffer);

				return geometryBuffer;
			} catch (IllegalBufferException e) {
				// TODO: Use a normal buffer instead.
				return null;
			}
		} else {
			geometries.add(genertateBuffer(geometry));
			return new JOGLGeometryHandle(this, geometries.size() - 1);
		}
	}

	boolean checkGeometryIndex(int geometryindex) {
		if (canUseVBOs) {
			// TODO: can we find out more?
			return geometryindex > 0;
		} else {
			return geometryindex >= 0 && geometryindex < geometries.size();
		}
	}

	void deleteGeometry(int geometryindex) {
		if (canUseVBOs) {
			gl2.glDeleteBuffers(1, new int[] {
					geometryindex
			}, 0);
		} else {
			// TODO: unsupported!
			geometries.set(geometryindex, null);
		}
	}

	@Override
	public GLBuffer startWriteGeometry(GeometryHandle geometry) throws IllegalBufferException {
		if (canUseVBOs) {
			bindArrayBuffer(geometry);
			ByteBuffer buffer =
					gl2.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY)
							.order(ByteOrder.nativeOrder());
			return new GLByteBufferWrapper(buffer);

		} else {
			return new GLByteBufferWrapper(getGeometryBuffer(geometry));
		}
	}

	private static class GLByteBufferWrapper implements GLBuffer {
		private final ByteBuffer buffer;

		private GLByteBufferWrapper(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		@Override
		public void putFloat(float f) {
			buffer.putFloat(f);
		}

		@Override
		public void putByte(byte b) {
			buffer.put(b);
		}

		@Override
		public void position(int position) {
			buffer.position(position);
		}
	}

	@Override
	public void endWriteGeometry(GeometryHandle geometry) {
		if (canUseVBOs) {
			gl2.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		}
	}

	@Override
	public GeometryHandle generateGeometry(int bytes) {
		int vertexBufferId;
		if (canUseVBOs) {
			vertexBufferId = allocateVBO();
			if (vertexBufferId == 0) {
				return null;
			}

			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufferId);
			gl2.glBufferData(GL.GL_ARRAY_BUFFER, bytes, null,
					GL.GL_DYNAMIC_DRAW);
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		} else {
			ByteBuffer bb = ByteBuffer.allocateDirect(bytes);
			bb.order(ByteOrder.nativeOrder());
			vertexBufferId = geometries.size();
			geometries.add(bb);
		}
		return new JOGLGeometryHandle(this, vertexBufferId);
	}

	private int allocateVBO() {
		int[] vertexBuffIds = new int[] {
				0
		};
		gl2.glGenBuffers(1, vertexBuffIds, 0);

		return vertexBuffIds[0];
	}

	public void prepareFontDrawing() {
	}

	/**
	 * Called whenever we should dispose all buffers associated with this context.
	 */
	public void disposeAll() {
		contextValid = false;
	}

	public boolean isValid() {
		return contextValid;
	}
}
