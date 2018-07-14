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
import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import go.graphics.swing.opengl.LWJGLBufferHandle.LWJGLTextureHandle;
import go.graphics.swing.text.LWJGLTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
/**
 * This is the draw context implementation for LWJGL. OpenGL draw calles are mapped to the corresponding LWJGL calls.
 *
 * @author Michael Zangl
 * @author paul
 *
 */
public class LWJGLDrawContext implements GLDrawContext {

	private LWJGLTextDrawer[] textDrawers = new LWJGLTextDrawer[EFontSize
			.values().length];

	private final GLCapabilities glcaps;
	private Callback debugcallback = null;

	public LWJGLDrawContext(GLCapabilities glcaps, boolean debug) {
		this.glcaps = glcaps;

		if(debug) {
			debugcallback = GLUtil.setupDebugMessageCallback(System.err);
			if(glcaps.OpenGL43) {
				GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
			} else if(glcaps.GL_KHR_debug) {
				GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS);
			} else if(glcaps.GL_ARB_debug_output) {
				GL11.glEnable(ARBDebugOutput.GL_DEBUG_OUTPUT_SYNCHRONOUS_ARB);
			}
		}

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void color(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}

	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture != null ? texture.getInternalId() : 0);

		if (glcaps.GL_ARB_vertex_array_object) {
			ARBVertexArrayObject.glBindVertexArray(geometry.getInternalFormatId());
			GL11.glDrawArrays(primitive, offset * vertices, vertices);
		} else {
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, geometry.getInternalId());
			EGeometryFormatType format = geometry.getFormat();

			if(format.getTexCoordPos() == -1) GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

			specifyFormat(format);
			GL11.glDrawArrays(primitive, offset * vertices, vertices);

			if(format.getTexCoordPos() == -1) GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}

	}

	private void specifyFormat(EGeometryFormatType format) {
		if (format.getTexCoordPos() == -1) {
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		} else {
			int stride = format.getBytesPerVertexSize();
			GL11.glVertexPointer(2, GL11.GL_FLOAT, stride, 0);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, format.getTexCoordPos());
		}

	}

	/**
	 * The global context valid flag. As soon as this is set to false, the context is not valid any more.
	 */
	private boolean contextValid = true;

	@Override
	public void glPushMatrix() {
		GL11.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		GL11.glPopMatrix();
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		// 1 byte aligned.
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		int texture = GL11.glGenTextures();
		if (texture == 0) {
			return null;
		}

		//fix strange alpha test problem (minimap and landscape are unaffected)
		ShortBuffer bfr = BufferUtils.createShortBuffer(data.capacity());
		int cap = data.capacity();
		for(int i = 0;i != cap;i++)	bfr.put(i, data.get(i));



		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
				GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, bfr);
		setTextureParameters();

		setObjectLabel(GL11.GL_TEXTURE, texture, name + "-tex");

		return new LWJGLTextureHandle(this, texture);
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

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f) ; // prevent writing of transparent pixels to z buffer
	}

	@Override
	public void updateTexture(TextureHandle texture, int left, int bottom,
			int width, int height, ShortBuffer data) {
		bindTexture(texture);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	private void bindTexture(TextureHandle texture) {
		int id;
		if (texture == null) {
			id = 0;
		} else {
			id = texture.getInternalId();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	@Override
	public int makeSideLengthValid(int width) {
		return TextureCalculator.supportedTextureSize(glcaps, width);
	}

	@Override
	public void glMultMatrixf(float[] matrix) {
		GL11.glMultMatrixf(matrix);
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
			textDrawers[size.ordinal()] = new LWJGLTextDrawer(size, this);
		}
		return textDrawers[size.ordinal()];
	}

	private int backgroundVAO = 0;

	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle vertexHandle, GeometryHandle paintHandle, int offset, int lines, int width, int stride) {
		bindTexture(textureid);

		if(glcaps.GL_ARB_vertex_array_object) {
			if(backgroundVAO == 0) {
				backgroundVAO = ARBVertexArrayObject.glGenVertexArrays();
				ARBVertexArrayObject.glBindVertexArray(backgroundVAO);
				GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexHandle.getInternalId());
				GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, paintHandle.getInternalId());
				GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 3 * 4, 0);
				GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 3 * 4, 8);

				setObjectLabel(GL11.GL_VERTEX_ARRAY, backgroundVAO, "background-vao");
				setObjectLabel(GL43.GL_BUFFER, vertexHandle.getInternalId(), "background-shape");
				setObjectLabel(GL43.GL_BUFFER, paintHandle.getInternalId(), "background-shape");
			}

			ARBVertexArrayObject.glBindVertexArray(backgroundVAO);
			for (int i = 0; i != lines; i++) {
				GL11.glDrawArrays(GL11.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
			}
		} else {
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexHandle.getInternalId());
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, paintHandle.getInternalId());
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 3 * 4, 0);
			GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 3 * 4, 8);

			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
			for (int i = 0; i != lines; i++) {
				GL11.glDrawArrays(GL11.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
			}
			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		}
	}

	@Override
	public void updateGeometryAt(GeometryHandle handle, int pos, ByteBuffer data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.getInternalId());
		data.rewind();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, pos, data);
	}

	@Override
	public GeometryHandle storeGeometry(float[] geometry, EGeometryFormatType type, String name) {
		GeometryHandle geometryBuffer = allocateVBO(type, name);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, geometryBuffer.getInternalId());
		try(MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer bfr = stack.malloc(4*geometry.length);
			bfr.asFloatBuffer().put(geometry);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bfr, type == EGeometryFormatType.Texture2D ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
		}

		return geometryBuffer;
	}

	boolean checkGeometryIndex(int geometryindex) {
		return geometryindex > 0;
	}

	void deleteGeometry(int geometryindex) {
		GL15.glDeleteBuffers(geometryindex);
	}

	@Override
	public GeometryHandle generateGeometry(int vertices, EGeometryFormatType type, String name) {
		GeometryHandle vertexBufferId = allocateVBO(type, name);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId.getInternalId());
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices*type.getBytesPerVertexSize(), type == EGeometryFormatType.Background ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW);
		return vertexBufferId;
	}
	private GeometryHandle allocateVBO(EGeometryFormatType type, String name) {
		int vao = 0;
		int vbo = GL15.glGenBuffers();
		if (glcaps.GL_ARB_vertex_array_object && type != EGeometryFormatType.Background) {
			vao = ARBVertexArrayObject.glGenVertexArrays();
			ARBVertexArrayObject.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			if (type.getTexCoordPos() != -1) {
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			}

			specifyFormat(type);
		}

		if(type != EGeometryFormatType.Background)  {
			setObjectLabel(GL43.GL_BUFFER, vbo, name + "-vertices");
			setObjectLabel(GL11.GL_VERTEX_ARRAY, vao, name + "-vao");
		}

		return new LWJGLBufferHandle.LWJGLGeometryHandle(this, type, vao, vbo);
	}

	private void setObjectLabel(int type, int id, String name) {
		if(debugcallback == null) return;

		if(glcaps.OpenGL43) {
			GL43.glObjectLabel(type, id, name);
		} else if(glcaps.GL_KHR_debug) {
			KHRDebug.glObjectLabel(type, id, name);
		}
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
