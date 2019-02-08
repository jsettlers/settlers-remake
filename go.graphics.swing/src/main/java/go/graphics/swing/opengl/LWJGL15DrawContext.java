/*******************************************************************************
 * Copyright (c) 2015-2018
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

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import go.graphics.AbstractColor;
import go.graphics.EGeometryFormatType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import go.graphics.swing.text.LWJGLTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.MemoryStack;

/**
 * This is the draw context implementation for LWJGL. OpenGL draw calles are mapped to the corresponding LWJGL calls.
 *
 * @author Michael Zangl
 * @author paul
 *
 */
public class LWJGL15DrawContext implements GLDrawContext {

	private TextDrawer[] sizedTextDrawers = new TextDrawer[EFontSize.values().length];
	private LWJGLTextDrawer textDrawer = null;
	protected LWJGLDebugOutput debugOutput = null;

	public final GLCapabilities glcaps;

	private GeometryHandle lastGeometry = null;
	private TextureHandle lastTexture = null;

	public LWJGL15DrawContext(GLCapabilities glcaps, boolean debug) {
		this.glcaps = glcaps;

		if(debug) debugOutput = new LWJGLDebugOutput(this);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		init();
	}

	void init() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private float lr, lg, lb, la = -1;
	private float lx, ly, lz = -2;
	private float lsx, lsy, lsz = -1;

	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) {
		if(lx != x || ly != y || lz != z || lsx != sx || lsy != sy || lsz != sz) {
			if(lsz != -1) GL11.glPopMatrix();
			GL11.glPushMatrix();
			if(x != 0 || y != 0 || z != 0) GL11.glTranslatef(x, y, z);
			if(sx != 1 || sy != 1 || sz != 1) GL11.glScalef(sx, sy, sz);
			lx = x; lsx = sx;
			ly = y; lsy = sy;
			lz = z; lsz = sz;
		}

		if(color != null) {
			float r = color.red*intensity;
			float g = color.green*intensity;
			float b = color.blue*intensity;
			float a = color.alpha;
			if(lr != r || lg != g || lb != b || la != a) GL11.glColor4f(lr=r, lg=g, lb=b, la=a);
		} else {
			if(lr != lg || lr != lb || lr != intensity || la != 1) GL11.glColor4f(intensity, intensity, intensity, 1);
			lr = lg = lb = intensity;
			la = 1;
		}

		bindTexture(texture);
		bindGeometry(geometry);
		EGeometryFormatType format = geometry.getFormat();

		if(format.getTexCoordPos() == -1) GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		specifyFormat(format);
		GL11.glDrawArrays(primitive, offset * vertices, vertices);

		if(format.getTexCoordPos() == -1) GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}

	private boolean tex_coord_on = false;

	protected void specifyFormat(EGeometryFormatType format) {
		if (format.getTexCoordPos() == -1) {
			if(tex_coord_on) GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		} else {
			if(!tex_coord_on) GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			int stride = format.getBytesPerVertexSize();
			GL11.glVertexPointer(2, GL11.GL_FLOAT, stride, 0);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, format.getTexCoordPos());
		}

		tex_coord_on = format.getTexCoordPos() != -1;
	}

	/**
	 * The global context valid flag. As soon as this is set to false, the context is not valid any more.
	 */
	private boolean contextValid = true;

	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		// reset matrix stack
		if(lsz != -1) GL11.glPopMatrix();
		lsz = -1;

		GL11.glLoadIdentity();
		GL11.glTranslatef(x, y, z);
		GL11.glScalef(sx, sy, sz);
	}

	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		int texture = GL11.glGenTextures();
		if (texture == 0) {
			return null;
		}

		//fix strange alpha test problem (minimap and landscape are unaffected)
		ShortBuffer bfr = BufferUtils.createShortBuffer(data.capacity());
		int cap = data.capacity();
		for(int i = 0;i != cap;i++)	bfr.put(i, data.get(i));

		TextureHandle textureHandle = new TextureHandle(this, texture);
		bindTexture(textureHandle);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
				GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, bfr);
		setTextureParameters();

		setObjectLabel(GL11.GL_TEXTURE, texture, name + "-tex");

		return textureHandle;
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created and is bound.
	 */
	private void setTextureParameters() {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
	}

	@Override
	public void updateTexture(TextureHandle texture, int left, int bottom,
			int width, int height, ShortBuffer data) {
		bindTexture(texture);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	protected void bindTexture(TextureHandle texture) {
		if(lastTexture != texture) {
			int id = 0;
			if (texture != null) {
				id = texture.getInternalId();
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	protected void bindGeometry(GeometryHandle geometry) {
		if(lastGeometry != geometry) {
			int id = 0;
			if (geometry != null) {
				id = geometry.getInternalId();
			}
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
			lastGeometry = geometry;
		}
	}

	private float[] heightMatrix;

	@Override
	public void setHeightMatrix(float[] matrix) {
		heightMatrix = matrix;
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
		if(textDrawer == null) textDrawer = new LWJGLTextDrawer(this);

		if (sizedTextDrawers[size.ordinal()] == null) {
			sizedTextDrawers[size.ordinal()] = textDrawer.derive(size);
		}
		return sizedTextDrawers[size.ordinal()];
	}

	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle shapeHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride, float x, float y) {
		bindTexture(textureid);
		int starti = offset < 0 ? (int)Math.ceil(-offset/(float)stride) : 0;

		if(lsz != -1) GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, -.1f);
		GL11.glScalef(1, 1, 0);
		GL11.glMultMatrixf(heightMatrix);

		if(!tex_coord_on) {
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			tex_coord_on = true;
		}

		bindGeometry(shapeHandle);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 5 * 4, 0);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 5 * 4, 3 * 4);

		bindGeometry(colorHandle);
		GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, 0);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		for (int i = starti; i != lines; i++) {
			GL11.glDrawArrays(GL11.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
		}
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

		GL11.glPopMatrix();
		lsz = -1;
	}

	@Override
	public void updateGeometryAt(GeometryHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, pos, data);
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry, EGeometryFormatType type, boolean writable, String name) {
		GeometryHandle geometryBuffer = allocateVBO(type, name);

		bindGeometry(geometryBuffer);
		try(MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer bfr = stack.malloc(4*geometry.length);
			bfr.asFloatBuffer().put(geometry);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bfr, writable ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
			setObjectLabel(KHRDebug.GL_BUFFER, geometryBuffer.getInternalId(), name + "-vertices");
		}

		return geometryBuffer;
	}

	@Override
	public GeometryHandle generateGeometry(int vertices, EGeometryFormatType type, boolean writable, String name) {
		GeometryHandle vertexBufferId = allocateVBO(type, name);

		bindGeometry(vertexBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices*type.getBytesPerVertexSize(), writable ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
		setObjectLabel(KHRDebug.GL_BUFFER, vertexBufferId.getInternalId(), name + "-vertices");
		return vertexBufferId;
	}

	GeometryHandle allocateVBO(EGeometryFormatType type, String name) {
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		return lastGeometry = new GeometryHandle(this, vbo, 0, type);
	}

	protected void setObjectLabel(int type, int id, String name) {
		if(debugOutput == null) return;

		if(glcaps.GL_KHR_debug) {
			KHRDebug.glObjectLabel(type, id, name);
		}
	}

	@Override
	public void deleteTexture(TextureHandle textureHandle) {
		GL11.glDeleteTextures(textureHandle.getInternalId());
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

	public void resize(int width, int height) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		// coordinate system origin at lower left with width and height same as
		// the window
		GL11.glOrtho(0, width, 0, height, -1, 1);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, width, height);
	}
}
