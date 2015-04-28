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

import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

/**
 * This class translates shadows.
 * 
 * @author michael
 *
 */
public class ShadowTranslator implements DatBitmapTranslator<ShadowImage> {
	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		return 0;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.DISPLACED;
	}

	@Override
	public short getTransparentColor() {
		return 0x0001;
	}

	@Override
	public ShadowImage createImage(ImageMetadata metadata, short[] array) {
		return new ShadowImage(metadata, array);
	}
}
