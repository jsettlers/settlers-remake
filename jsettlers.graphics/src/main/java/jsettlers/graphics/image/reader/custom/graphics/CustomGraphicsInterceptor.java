/*
 * Copyright (c) 2018
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

package jsettlers.graphics.image.reader.custom.graphics;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.image.reader.DatFileReader;

/**
 * Different settlers versions use different image indexes.
 * <p>
 * In the game, we always reference the image indexes of the gold edition.
 * <p>
 * This utility class holds the information about which index remapping to use for which file.
 *
 * @author michael
 */
public class CustomGraphicsInterceptor {

	private CustomGraphicsInterceptor() {
	}

	/**
	 * Returns a reader that uses our custom graphics if available.
	 *
	 * @param fileIndex
	 * 		file index
	 * @param reader
	 * 		The reader, using any index (auto-detected)
	 * @return DatFileReader preferring the custom graphics
	 */
	public static DatFileReader prependCustomGraphics(int fileIndex, DatFileReader reader, ImageProvider imageProvider) {
//		if (fileIndex == 36) { // use our own ships
//			return new CustomShipsDatFile(reader, imageProvider);
//		}
		return reader;
	}

}
