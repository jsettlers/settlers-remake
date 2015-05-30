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
package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

/**
 * This class translates settler images.
 *
 * @author michael
 *
 */
public class SettlerTranslator implements DatBitmapTranslator<SettlerImage> {

	@Override
	public SettlerImage createImage(ImageMetadata metadata, short[] array) {
		return new SettlerImage(metadata, array);
	}

	@Override
	public short getTransparentColor() {
		return 0x00;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.DISPLACED;
	}

	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		int read = reader.read16();

		int R5 = (int)((read & 0xF800) >> 11);
		int G6 = (int)((read & 0x07E0) >> 5);
		int B5 = (int)(read & 0x001F);

		int G5 = (int)( (float) G6 * 31.0f / 63.0f + 0.5f );

		int rgb = R5;
		rgb = rgb << 5;
		rgb |= G5;
		rgb = rgb << 5;
		rgb |= B5;

		return (short) (rgb << 1 | 0x01);
	}

}
