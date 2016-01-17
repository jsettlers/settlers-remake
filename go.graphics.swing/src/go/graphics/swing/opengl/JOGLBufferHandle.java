/*******************************************************************************
 * Copyright (c) 2016
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

import go.graphics.GLBufferHandle;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

public abstract class JOGLBufferHandle implements GLBufferHandle {

	public static class JOGLGeometryHandle extends JOGLBufferHandle implements GeometryHandle {
		public JOGLGeometryHandle(JOGLDrawContext context, int geometryindex) {
			super(context, geometryindex);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("JOGLGeometryHandle [index=");
			builder.append(index);
			builder.append("]");
			return builder.toString();
		}
	}

	public static class JOGLTextureHandle extends JOGLBufferHandle implements TextureHandle {
		public JOGLTextureHandle(JOGLDrawContext context, int textureindex) {
			super(context, textureindex);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("JOGLTextureHandle [index=");
			builder.append(index);
			builder.append("]");
			return builder.toString();
		}
	}

	protected final JOGLDrawContext context;
	private boolean deleted;
	protected int index;

	public JOGLBufferHandle(JOGLDrawContext context, int index) {
		this.context = context;
		this.index = index;
	}

	@Override
	public boolean isValid() {
		return context.isValid() && context.checkGeometryIndex(index) && !deleted;
	}

	@Override
	public void delete() {
		context.deleteGeometry(index);
		deleted = true;
	}

	@Override
	public int getInternalId() {
		return index;
	}

}