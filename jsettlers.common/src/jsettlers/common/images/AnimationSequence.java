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
package jsettlers.common.images;

/**
 * This is a definition for a sequence of images.
 * 
 * @author Michael Zangl
 */
public final class AnimationSequence {
	private final String name;
	private final int first;
	private final int length;

	/**
	 * Creates a new animation sequence link.
	 * 
	 * @param name
	 *            The name of the sequence.
	 * @param first
	 *            The first index of the sequence.
	 * @param length
	 *            The number of frames this sequence has.
	 * @see ImageLink#fromName(String, int)
	 */
	public AnimationSequence(String name, int first, int length) {
		super();
		this.name = name;
		this.first = first;
		this.length = length;
	}

	/**
	 * @return the name to use for image links.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the first index in the sequence
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * @return the length of the sequence
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Gets the n-th image in this sequence, starting by 0. If the parameter is bigger than the length of the sequence or less than 0, the result is
	 * undefined.
	 * 
	 * @param index
	 *            The relative index in this sequence.
	 * @return The image link to that image.
	 */
	public ImageLink getImage(int index) {
		assert index < length;
		return ImageLink.fromName(name, first + index);
	}
}
