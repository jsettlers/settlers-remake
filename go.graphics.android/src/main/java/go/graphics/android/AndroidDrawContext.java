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

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import go.graphics.EGeometryFormatType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class AndroidDrawContext implements GLDrawContext {
	private final Context context;

	private TextureHandle lastTexture = null;
	private GeometryHandle lastGeometry = null;
	private int lastFormat = 0;
	private boolean gles3;

	public AndroidDrawContext(Context context, boolean gles3) {
		this.context = context;
		this.gles3 = gles3;
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


	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices) {
		glBindTexture(texture);

		if (gles3) {
			bindFormat(geometry.getInternalFormatId());
			GLES11.glDrawArrays(primitive, offset * vertices, vertices);
		} else {
			bindGeometry(geometry);
			EGeometryFormatType format = geometry.getFormat();

			if(format.getTexCoordPos() == -1) GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);

			specifyFormat(format);
			GLES11.glDrawArrays(primitive, offset * vertices, vertices);

			if(format.getTexCoordPos() == -1) GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		}

	}

	private void specifyFormat(EGeometryFormatType format) {
		if (format.getTexCoordPos() == -1) {
			GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, 0);
		} else {
			int stride = format.getBytesPerVertexSize();
			GLES11.glVertexPointer(2, GLES11.GL_FLOAT, stride, 0);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, stride, format.getTexCoordPos());
		}

	}

	private void glBindTexture(TextureHandle texture) {
		if (texture != lastTexture) {
			int id = 0;
			if (texture != null) {
				id = texture.getInternalId();
			}
			GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	private void bindFormat(int format) {
		if(format != lastFormat) {
			GLES30.glBindVertexArray(format);
			lastFormat = format;
		}
	}

	private void bindGeometry(GeometryHandle geometry) {
		if(geometry != lastGeometry) {
			int id = 0;
			if(geometry != null) {
				id = geometry.getInternalId();
			}
			GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, id);
			lastGeometry = geometry;
		}
	}

	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		// 1 byte aligned.
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);

		TextureHandle texture = genTextureIndex();
		if (texture == null) {
			return null;
		}

		glBindTexture(texture);
		GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width,
				height, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_4_4_4_4, data);

		setTextureParameters();
		return texture;
	}

	private TextureHandle genTextureIndex() {
		int[] textureIndexes = new int[1];
		GLES10.glGenTextures(1, textureIndexes, 0);
		return new TextureHandle(this, textureIndexes[0]);
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

		GLES10.glAlphaFunc(GLES10.GL_GREATER, 0.5f) ; // prevent writing of transparent pixels to z buffer
	}

	@Override
	public void updateTexture(TextureHandle textureIndex, int left, int bottom,
			int width, int height, ShortBuffer data) {
		glBindTexture(textureIndex);
		GLES10.glTexSubImage2D(GLES10.GL_TEXTURE_2D, 0, left, bottom, width,
				height, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_4_4_4_4, data);
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
	public void glMultMatrixf(float[] matrix) {
		GLES10.glMultMatrixf(matrix, 0);
	}

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return AndroidTextDrawer.getInstance(size, this);
	}

	public void reinit(int width, int height) {
		GLES10.glMatrixMode(GLES10.GL_PROJECTION);
		GLES10.glLoadIdentity();
		GLES10.glOrthof(0, width, 0, height, -1, 1);
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
		GLES10.glLoadIdentity();

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
	public GeometryHandle generateGeometry(int vertices, EGeometryFormatType format, boolean writable, String name) {
		GeometryHandle geometry = allocateVBO(format);

		bindGeometry(geometry);
		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, vertices*format.getBytesPerVertexSize(), null,
				writable ? GLES11.GL_DYNAMIC_DRAW : GLES11.GL_STATIC_DRAW);
		return geometry;
	}

	private int[] backgroundVAO = new int[] {0};

	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle vertexHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride) {
		glBindTexture(textureid);
		int starti = offset < 0 ? (int)Math.ceil(-offset/(float)stride) : 0;

		// we can simply buffer the draw call here because there is only one instance per game that uses this function
		if(gles3) {
			if (backgroundVAO[0] == 0) {
				GLES30.glGenVertexArrays(1, backgroundVAO, 0);
				bindFormat(backgroundVAO[0]);
				GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
				GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
				GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);

				GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vertexHandle.getInternalId());
				GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 5 * 4, 0);
				GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 5 * 4, 3 * 4);

				GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, colorHandle.getInternalId());
				GLES11.glColorPointer(4, GLES11.GL_UNSIGNED_BYTE, 0, 0);
			}
			bindFormat(backgroundVAO[0]);
			for(int i = starti;i != lines;i++) {
				GLES11.glDrawArrays(GLES11.GL_TRIANGLES, (offset+stride*i)*3, width*3);
			}
		} else {
			bindGeometry(vertexHandle);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 5 * 4, 0);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 5 * 4, 3 * 4);

			bindGeometry(colorHandle);
			GLES11.glColorPointer(4, GLES11.GL_UNSIGNED_BYTE, 0, 0);

			GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
			for (int i = starti; i != lines; i++) {
				GLES11.glDrawArrays(GLES11.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
			}
			GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
		}
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry, EGeometryFormatType format, boolean writable, String name) {

		GeometryHandle vertexBufferId = allocateVBO(format);
		ByteBuffer bfr = ByteBuffer.allocateDirect(4*geometry.length).order(ByteOrder.nativeOrder());
		bfr.asFloatBuffer().put(geometry);
		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, 4*geometry.length, bfr, writable ? GLES11.GL_DYNAMIC_DRAW : GLES11.GL_STATIC_DRAW);

		return vertexBufferId;
	}
	private GeometryHandle allocateVBO(EGeometryFormatType type) {
		int[] vaos = new int[] {0};
		int[] vbos = new int[] {0};
		GLES11.glGenBuffers(1, vbos, 0);
		if (gles3 && type.isSingleBuffer()) {
			GLES30.glGenVertexArrays(1, vaos, 0);
			bindFormat(vaos[0]);
			GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vbos[0]);
			GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
			if (type.getTexCoordPos() != -1) {
				GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			}

			this.specifyFormat(type);
		}

		return new GeometryHandle(this, vbos[0], vaos[0], type);
	}

	@Override
	public void updateGeometryAt(GeometryHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		data.rewind();
		GLES11.glBufferSubData(GLES11.GL_ARRAY_BUFFER, pos, data.limit(), data);
	}

	public Context getAndroidContext() {
		return context;
	}

	public void invalidateContext() {
		valid = false;
	}

	@Override
	public void deleteTexture(TextureHandle texture) {
		GLES10.glDeleteTextures(1, new int[] {texture.getInternalId()}, 0);
	}

	private boolean valid = true;

	@Override
	public boolean isValid() {
		return valid;
	}
}
