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
import android.opengl.GLES11;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import go.graphics.AbstractColor;
import go.graphics.EGeometryFormatType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class GLES11DrawContext implements GLDrawContext {

	private final Context context;

	private GeometryHandle lastGeometry = null;
	private TextureHandle lastTexture = null;
	private boolean tex_coord_on = false;

	public GLES11DrawContext(Context context) {
		this.context = context;
		GLES11.glClearColor(0, 0, 0, 1);
		GLES11.glPixelStorei(GLES11.GL_UNPACK_ALIGNMENT, 1);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);

		GLES11.glDepthFunc(GLES11.GL_LEQUAL);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);

		init();
	}

	private float lr, lg, lb, la = -1;
	private float lx, ly, lz = -2;
	private float lsx, lsy, lsz = -1;

	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) {
		if(lx != x || ly != y || lz != z || lsx != sx || lsy != sy || lsz != sz) {
			if(lsz != -1) GLES11.glPopMatrix();
			GLES11.glPushMatrix();
			if(x != 0 || y != 0 || z != 0) GLES11.glTranslatef(x, y, z);
			if(sx != 1 || sy != 1 || sz != 1) GLES11.glScalef(sx, sy, sz);
			lx = x; lsx = sx;
			ly = y; lsy = sy;
			lz = z; lsz = sz;
		}

		if(color != null) {
			float r = color.red*intensity;
			float g = color.green*intensity;
			float b = color.blue*intensity;
			float a = color.alpha;
			if(lr != r || lg != g || lb != b || la != a) GLES11.glColor4f(lr=r, lg=g, lb=b, la=a);
		} else {
			if(lr != lg || lr != lb || lr != intensity || la != 1) GLES11.glColor4f(intensity, intensity, intensity, 1);
			lr = lg = lb = intensity;
			la = 1;
		}

		bindTexture(texture);
		bindGeometry(geometry);
		EGeometryFormatType format = geometry.getFormat();

		if(format.getTexCoordPos() == -1) GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);

		specifyFormat(format);
		GLES11.glDrawArrays(primitive, offset * vertices, vertices);

		if(format.getTexCoordPos() == -1) GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
	}

	protected void specifyFormat(EGeometryFormatType format) {
		if (format.getTexCoordPos() == -1) {
			if(tex_coord_on) GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, 0);
		} else {
			if(!tex_coord_on) GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			int stride = format.getBytesPerVertexSize();
			GLES11.glVertexPointer(2, GLES11.GL_FLOAT, stride, 0);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, stride, format.getTexCoordPos());
		}

		tex_coord_on = format.getTexCoordPos() != -1;
	}

	protected void bindTexture(TextureHandle texture) {
		if (texture != lastTexture) {
			int id = 0;
			if (texture != null) {
				id = texture.getInternalId();
			}
			GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		// reset matrix stack
		if(lsz != -1) GLES11.glPopMatrix();
		lsz = -1;

		GLES11.glLoadIdentity();
		GLES11.glScalef(sx, sy, sz);
		GLES11.glTranslatef(x, y, z);
	}

	protected void bindGeometry(GeometryHandle geometry) {
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
		TextureHandle texture = genTextureIndex();

		bindTexture(texture);
		GLES11.glTexImage2D(GLES11.GL_TEXTURE_2D, 0, GLES11.GL_RGBA, width,
				height, 0, GLES11.GL_RGBA, GLES11.GL_UNSIGNED_SHORT_4_4_4_4, data);
		setTextureParameters();
		return texture;
	}

	private TextureHandle genTextureIndex() {
		int[] textureIndexes = new int[1];
		GLES11.glGenTextures(1, textureIndexes, 0);
		return new TextureHandle(this, textureIndexes[0]);
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created and is bound.
	 */
	private static void setTextureParameters() {
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D,
				GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_NEAREST);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D,
				GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_NEAREST);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_S,
				GLES11.GL_REPEAT);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_T,
				GLES11.GL_REPEAT);
	}

	@Override
	public void updateTexture(TextureHandle textureIndex, int left, int bottom,
			int width, int height, ShortBuffer data) {
		bindTexture(textureIndex);
		GLES11.glTexSubImage2D(GLES11.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GLES11.GL_RGBA, GLES11.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	public TextureHandle generateFontTexture(int width, int height) {
		TextureHandle texture = genTextureIndex();

		ByteBuffer data = ByteBuffer.allocateDirect(width * height * 4);
		while (data.hasRemaining()) {
			data.put((byte) 0);
		}
		data.rewind();

		bindTexture(texture);
		if(this instanceof GLES20DrawContext) {
			GLES11.glTexImage2D(GLES11.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width,
					height, 0, GLES11.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
		} else {
			GLES11.glTexImage2D(GLES11.GL_TEXTURE_2D, 0, GLES11.GL_ALPHA, width,
					height, 0, GLES11.GL_ALPHA, GLES11.GL_UNSIGNED_BYTE, data);
		}

		setTextureParameters();
		return texture;
	}

	public void updateFontTexture(TextureHandle textureIndex, int left, int bottom,
								   int width, int height, ByteBuffer data) {
		bindTexture(textureIndex);
		if(this instanceof GLES20DrawContext) {
			GLES11.glTexSubImage2D(GLES11.GL_TEXTURE_2D, 0, left, bottom, width,
					height, GLES11.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
		} else {
			GLES11.glTexSubImage2D(GLES11.GL_TEXTURE_2D, 0, left, bottom, width,
					height, GLES11.GL_ALPHA, GLES11.GL_UNSIGNED_BYTE, data);
		}
	}

	private float[] heightMatrix;

	@Override
	public void setHeightMatrix(float[] matrix) {
		heightMatrix = matrix;
	}

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return AndroidTextDrawer.getInstance(size, this);
	}

	public void reinit(int width, int height) {
		GLES11.glViewport(0, 0, width, height);
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GLES11.glOrthof(0, width, 0, height, -1, 1);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
	}

	public void init() {
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);

		GLES11.glAlphaFunc(GLES11.GL_GREATER, 0.1f);
		GLES11.glEnable(GLES11.GL_ALPHA_TEST);

		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
	}

	@Override
	public GeometryHandle generateGeometry(int vertices, EGeometryFormatType format, boolean writable, String name) {
		GeometryHandle geometry = allocateVBO(format);

		bindGeometry(geometry);
		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, vertices*format.getBytesPerVertexSize(), null,
				writable ? GLES11.GL_DYNAMIC_DRAW : GLES11.GL_STATIC_DRAW);
		return geometry;
	}

	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle vertexHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride) {
		bindTexture(textureid);
		int starti = offset < 0 ? (int)Math.ceil(-offset/(float)stride) : 0;

		if(lsz != -1) GLES11.glPopMatrix();
		GLES11.glPushMatrix();
		GLES11.glTranslatef(0, 0, -.1f);
		GLES11.glScalef(1, 1, 0);
		GLES11.glMultMatrixf(heightMatrix, 0);

		if(!tex_coord_on) {
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			tex_coord_on = true;
		}

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

		GLES11.glPopMatrix();
		lsz = -1;
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry, EGeometryFormatType format, boolean writable, String name) {
		GeometryHandle vertexBufferId = allocateVBO(format);
		ByteBuffer bfr = ByteBuffer.allocateDirect(4*geometry.length).order(ByteOrder.nativeOrder());
		bfr.asFloatBuffer().put(geometry);
		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, 4*geometry.length, bfr, writable ? GLES11.GL_DYNAMIC_DRAW : GLES11.GL_STATIC_DRAW);

		return vertexBufferId;
	}

	GeometryHandle allocateVBO(EGeometryFormatType type) {
		int[] vbos = new int[] {0};
		GLES11.glGenBuffers(1, vbos, 0);
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vbos[0]);
		return lastGeometry = new GeometryHandle(this, vbos[0], 0, type);
	}

	@Override
	public void updateGeometryAt(GeometryHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		GLES11.glBufferSubData(GLES11.GL_ARRAY_BUFFER, pos, data.remaining(), data);
	}

	public Context getAndroidContext() {
		return context;
	}

	public void invalidateContext() {
		valid = false;
	}

	@Override
	public void deleteTexture(TextureHandle texture) {
		GLES11.glDeleteTextures(1, new int[] {texture.getInternalId()}, 0);
	}

	private boolean valid = true;

	@Override
	public boolean isValid() {
		return valid;
	}
}
