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
package jsettlers.graphics.image.reader;

import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.sequence.Sequence;
import jsettlers.graphics.image.sequence.SequenceList;

/**
 * This is a dat file set that holds the data of a dat file in converted form.
 * <p>
 * It allows access to the torsos, settler images and landscape tiles in the file.
 * <p>
 * The lists should allow quick index access.
 * 
 * @author michael
 */
public interface DatFileSet {
	/**
	 * Gets a list of settlers in the dat file.
	 * 
	 * @return The unmodifiable list.
	 */
	SequenceList<Image> getSettlers();

	/**
	 * Gets a list of landscape tiles in the dat file.
	 * 
	 * @return The unmodifiable list.
	 */
	Sequence<SingleImage> getLandscapes();

	/**
	 * Gets a list of gui images.
	 * 
	 * @return The unmodifiable list.
	 */
	Sequence<SingleImage> getGuis();
}
