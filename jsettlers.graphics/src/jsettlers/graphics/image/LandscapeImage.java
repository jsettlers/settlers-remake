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

import jsettlers.graphics.reader.ImageMetadata;

/**
 * Class to draw triangles of landscape images.
 * <p>
 * There are 2 types of images: <br>
 * Some images are big and continuous, so they are just drawn and wrapped at the end. <br>
 * Other images only consist of 6 triangles indicating a border between two terrian types in all 6 directions.
 * <p>
 * You have to do the drawing yourself, but there are helper functions that help you:
 * <p>
 * bind() activates drawing the texture.
 * 
 * @author Michael Zangl
 */
public class LandscapeImage extends SingleImage {
	private static final int CONTINUOUS_MIN_SIZE = 50;
	/**
	 * States that you request the image in the top right border of the texture.
	 */
	public static final int TRI_TOPRIGHT = 0;
	/**
	 * States that you request the image in the bottom right border of the texture.
	 */
	public static final int TRI_BOTTOMRIGHT = 1;
	/**
	 * States that you request the image in the bottom border of the texture.
	 */
	public static final int TRI_BOTTOM = 2;
	/**
	 * States that you request the image in the bottom left border of the texture.
	 */
	public static final int TRI_BOTTOMLEFT = 3;
	/**
	 * States that you request the image in the top border of the texture.
	 */
	public static final int TRI_TOP = 4;
	/**
	 * States that you request the image in the top left border of the texture.
	 */
	public static final int TRI_TOPLEFT = 5;

	/**
	 * Creates a new {@link LandscapeImage}.
	 * 
	 * @param metadata
	 *            The image meta data.
	 * @param data
	 *            The data for the image.
	 */
	public LandscapeImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}

	/**
	 * Checks whether the given image is a continuous image, that means it can be repeated when drawing.
	 * 
	 * @return If the image is continuous.
	 */
	public boolean isContinuous() {
		return this.width > CONTINUOUS_MIN_SIZE && this.height > CONTINUOUS_MIN_SIZE;
	}

}
