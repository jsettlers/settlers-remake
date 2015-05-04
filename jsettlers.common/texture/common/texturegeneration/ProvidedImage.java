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
package jsettlers.common.texturegeneration;

import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

import jsettlers.common.Color;
import jsettlers.graphics.image.ImageDataPrivider;

public class ProvidedImage implements ImageDataPrivider {

	private final BufferedImage image;
	private final int[] offsets;

	public ProvidedImage(BufferedImage image, int[] offsets) {
		this.image = image;
		this.offsets = offsets;
	}

	@Override
	public ShortBuffer getData() {
		ShortBuffer data = ShortBuffer.allocate(image.getWidth() * image.getHeight());
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = new Color(image.getRGB(x, y));
				data.put(color.toShortColor(1));
			}
		}
		data.rewind();
		return data;
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public int getOffsetX() {
		return offsets[0];
	}

	@Override
	public int getOffsetY() {
		return offsets[1];
	}

}
