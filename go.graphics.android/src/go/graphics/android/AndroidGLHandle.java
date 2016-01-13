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
package go.graphics.android;

import go.graphics.GLBufferHandle;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import android.opengl.GLES10;
import android.opengl.GLES11;

public class AndroidGLHandle implements GLBufferHandle {
	public static class AndroidTextureHandle extends AndroidGLHandle implements TextureHandle {
		public AndroidTextureHandle(int internalId) {
			super(internalId);
		}

		@Override
		public void delete() {
			GLES10.glDeleteTextures(1, new int[] {
					getInternalId()
			}, 0);
		}

		@Override
		public boolean isValid() {
			return super.isValid() && GLES11.glIsTexture(getInternalId());
		}
	}

	public static class AndroidGeometryHandle extends AndroidGLHandle implements GeometryHandle {
		public AndroidGeometryHandle(int internalId) {
			super(internalId);
		}

		@Override
		public void delete() {
			GLES11.glDeleteBuffers(1, new int[] {
					getInternalId()
			}, 0);
		}

		@Override
		public boolean isValid() {
			return super.isValid() && GLES11.glIsBuffer(getInternalId());
		}
	}

	private int internalId;
	private boolean deleted;

	public AndroidGLHandle(int internalId) {
		this.internalId = internalId;
	}

	@Override
	public boolean isValid() {
		return !deleted;
	}

	@Override
	public void delete() {
		// TODO: Free memeory
		deleted = true;
	}

	@Override
	public int getInternalId() {
		return internalId;
	}
}
