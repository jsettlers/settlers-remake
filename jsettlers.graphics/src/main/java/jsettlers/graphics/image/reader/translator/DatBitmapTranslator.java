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

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.reader.bytereader.ByteReader;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This interfaces defines methods that a reader for bitmaps in dat files must provide to convert the dat files to opengl images.
 *
 * @param <T>
 * 		The image type this translator translates.
 * @author Michael Zangl
 */
public interface DatBitmapTranslator<T extends Image> {

	/**
	 * Returns whether bitmaps of this translator use a long header with offsetX and Y.
	 *
	 * @return true The header type the image uses..
	 */
	HeaderType getHeaderType();

	/**
	 * Reads a color from the reader and progresses the reader so that it stands after the color.
	 *
	 * @param reader
	 * 		The reader to read from
	 * @return A short indicating the color, e.g. in 5-5-5-1 RGBA format.
	 * @throws IOException
	 * 		If an error occurred.
	 */
	short readUntransparentColor(ByteReader reader) throws IOException;

	/**
	 * gets the color that is used as transparent.
	 *
	 * @return A short indicating the color.
	 */
	short getTransparentColor();

	/**
	 * Creates a image of the given type.
	 *
	 * @param metadata
	 * 		The image metadata
	 * @param array
	 * 		The array of image pixels.
	 * @return The image
	 */
	T createImage(ImageMetadata metadata, short[] array, String name);

}
