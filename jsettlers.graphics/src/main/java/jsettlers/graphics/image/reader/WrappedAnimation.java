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

import jsettlers.common.images.AnimationSequence;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.image.sequence.Sequence;

/**
 * Mapps an {@link AnimationSequence} to the acutal images using an
 * {@link ImageProvider}
 * 
 * @author michael
 *
 */
public class WrappedAnimation implements Sequence<Image> {

	private final ImageProvider imageProvider;
	private final AnimationSequence sequence;

	public WrappedAnimation(ImageProvider imageProvider, AnimationSequence sequence) {
		this.imageProvider = imageProvider;
		this.sequence = sequence;
	}

	@Override
	public int length() {
		return sequence.getLength();
	}

	@Override
	public Image getImage(int index) {
		return imageProvider.getImage(sequence.getImageLink(index));
	}

	@Override
	public Image getImageSafe(int index) {
		return index < 0 || index >= length() ? NullImage.getInstance() : getImage(index);
	}

}
