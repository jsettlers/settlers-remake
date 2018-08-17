/*
 * Copyright (c) 2015- . 2018
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
package jsettlers.graphics.image.sequence;

import java.util.Arrays;
import java.util.Iterator;
import java8.util.function.Supplier;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.image.SingleImage;

/**
 * This class defines an image sequence.
 * 
 * @author Michael Zangl
 * @param <T>
 *            The image type images of this sequence are of.
 */
public final class ArraySequence<T extends Image> implements Iterable<T>, Sequence<T> {
	private static Sequence<Image> nullSequence;
	private final T[] images;

	/**
	 * Creates a new sequence.
	 * 
	 * @param images
	 *            The images for the sequence.
	 */
	public ArraySequence(T[] images) {
		this.images = images;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Sequence#length()
	 */
	@Override
	public int length() {
		return this.images.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Sequence#getImageLink(int)
	 */
	@Override
	public T getImage(int index, Supplier<String> name) {
		return this.images[index];

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Sequence#getImageSafe(int)
	 */
	@Override
	public Image getImageSafe(int index, Supplier<String> name) {
		if (index >= 0 && index < this.images.length) {
			return this.images[index];
		} else {
			return NullImage.getInstance();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(this.images).iterator();
	}

	/**
	 * Gets an empty sequence.
	 * 
	 * @return The emepty sequence.
	 */
	public static Sequence<Image> getNullSequence() {
		if (nullSequence == null) {
			nullSequence = new ArraySequence<>(new SingleImage[0]);
		}
		return nullSequence;
	}
}
