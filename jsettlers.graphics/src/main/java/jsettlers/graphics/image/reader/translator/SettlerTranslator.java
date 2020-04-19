/*
 * Copyright (c) 2015 - 2018
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
 */
package jsettlers.graphics.image.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.reader.bytereader.ByteReader;
import jsettlers.graphics.image.reader.DatFileType;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This class translates settler images. Settler images are packed in sequences.
 *
 * @author Michael Zangl
 *
 */
public class SettlerTranslator implements DatBitmapTranslator<SettlerImage> {

	private final DatFileType type;

	/**
	 * Creates a new {@link SettlerTranslator}.
	 * 
	 * @param type
	 *            The {@link DatFileType} to convert colors.
	 */
	public SettlerTranslator(DatFileType type) {
		this.type = type;
	}

	@Override
	public SettlerImage createImage(ImageMetadata metadata, short[] array, String name) {
		return new SettlerImage(metadata, array, name);
	}

	@Override
	public short getTransparentColor() {
		return 0;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.DISPLACED;
	}

	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		return type.convertTo4444(reader.read16());
	}

}
