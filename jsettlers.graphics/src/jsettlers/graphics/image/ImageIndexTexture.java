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
package jsettlers.graphics.image;

import go.graphics.GLDrawContext;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class ImageIndexTexture {
	private int textureIndex = -1;
	private final InputStream file;

	public ImageIndexTexture(InputStream inputStream) {
		this.file = inputStream;
	}

	public int getTextureIndex(GLDrawContext gl) {
		if (textureIndex < 0) {
			loadTexture(gl);
		}
		return textureIndex;
	}

	private void loadTexture(GLDrawContext gl) {
		try {
			final DataInputStream in =
					new DataInputStream(new BufferedInputStream(
							file));
			int i = in.available() / 2;
			final int height = nextLowerPOT(Math.sqrt(i));
			final int width = nextLowerPOT(i / height);

			// TODO: Use better buffering.
			final ShortBuffer data = ByteBuffer.allocateDirect(width * height * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
			while (data.hasRemaining()) {
				data.put(in.readShort());
			}
			data.rewind();
			textureIndex = gl.generateTexture(width, height, data);
		} catch (final IOException e) {
			e.printStackTrace();
			textureIndex = 0;
		}
	}

	private static int nextLowerPOT(double number) {
		int i = 2;
		while (i * 2 <= number) {
			i *= 2;
		}
		return i;
	}
}
