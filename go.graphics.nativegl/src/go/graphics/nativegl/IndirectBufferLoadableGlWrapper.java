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
package go.graphics.nativegl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This is an improved gl wrapper that can handle indirect buffers
 * 
 * @author michael
 */
public class IndirectBufferLoadableGlWrapper extends NativeGLWrapper {
	public void drawTrianglesWithTextureColored(int currentTexture,
	        java.nio.ByteBuffer byteBuffer, int currentTriangles) {
		super.drawTrianglesWithTextureColored(currentTexture,
		        makeDirect(byteBuffer), currentTriangles);
	}

	@Override
	public void updateTexture(int textureIndex, int left, int bottom,
	        int width, int height, ShortBuffer data) {
		super.updateTexture(textureIndex, left, bottom, width, height,
		        makeDirect(data));
	}

	@Override
	public int generateTexture(int width, int height, ShortBuffer data) {
		return super.generateTexture(width, height, makeDirect(data));
	}

	private static ShortBuffer makeDirect(ShortBuffer data) {
		if (data.isDirect()) {
			return data;
		} else {
			ShortBuffer newBuffer =
			        ByteBuffer.allocateDirect(data.capacity() * 2)
			                .order(ByteOrder.nativeOrder()).asShortBuffer();
			int pos = data.position();
			data.position(0);
			newBuffer.put(data);
			data.position(pos);
			newBuffer.position(pos);
			return newBuffer;
		}
	}

	/**
	 * Converts any buffer to a direct buffer.
	 * 
	 * @param byteBuffer
	 *            The buffer to convert
	 * @return A (maby new) buffer.
	 */
	private static ByteBuffer makeDirect(ByteBuffer byteBuffer) {
		if (byteBuffer.isDirect()) {
			return byteBuffer;
		} else {
			ByteBuffer newBuffer =
			        ByteBuffer.allocateDirect(byteBuffer.capacity());
			int pos = byteBuffer.position();
			byteBuffer.position(0);
			newBuffer.put(byteBuffer);
			byteBuffer.position(pos);
			newBuffer.position(pos);
			return newBuffer;
		}
	};
}
