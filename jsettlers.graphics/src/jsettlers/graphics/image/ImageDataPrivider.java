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

import java.nio.ShortBuffer;

/**
 * Classes implementing this interface are capable of providing image data.
 * <p>
 * The data should not be changed afterwards.
 * 
 * @author michael
 */
public interface ImageDataPrivider {
	/**
	 * Gets the data for the image.
	 * 
	 * @return The image data.
	 */
	ShortBuffer getData();

	/**
	 * Gets the width for the image.
	 * 
	 * @return The width as int.
	 */
	int getWidth();

	/**
	 * Gets the height for the image.
	 * 
	 * @return The height as int.
	 */
	int getHeight();

	/**
	 * Gets the x offset of the image.
	 * 
	 * @return THe x offset.
	 */
	int getOffsetX();

	/**
	 * Gets the y offset of the image.
	 * 
	 * @return The y offset.
	 */
	int getOffsetY();

}
