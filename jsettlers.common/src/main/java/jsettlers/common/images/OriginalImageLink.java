/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.util.Locale;

/**
 * This is a virtual link to a image in a settler image file.
 * <p>
 * Files indexes are always the ones of the gold editon of settlers 3. Internal translating allows to migtrate between gold and demo.
 * 
 * @author Michael Zangl
 * @see EImageLinkType
 */
public final class OriginalImageLink extends ImageLink {
	private static final long serialVersionUID = -9042004381156308651L;

	private final EImageLinkType type;
	private final int file;
	private final int sequence;
	private final int image;
	private final int length;
	private final String humanName;
	private OriginalImageLink fallback;

	/**
	 * Creates a new image link description.
	 * 
	 * @param type
	 *            The type
	 * @param file
	 *            The file it is in
	 * @param sequence
	 *            The sequence index
	 * @param image
	 *            The image in the sequence, for {@value EImageLinkType#SETTLER} images.
	 * @param length
	 *            The number contained in the sequence that is linked,
	 */
	public OriginalImageLink(EImageLinkType type, int file, int sequence, int image, int length, String name) {
		this.type = type;
		this.file = file;
		this.sequence = sequence;
		this.image = image;
		this.length = length;
		this.humanName = name;
	}

	public OriginalImageLink(EImageLinkType type, int file, int sequence, int image, int length) {
		this(type, file, sequence, image, length, null);
	}

	/**
	 * Creates a new image link description.
	 * 
	 * @param type
	 *            The type
	 * @param file
	 *            The file it is in
	 * @param sequence
	 *            The sequence index
	 * @param image
	 *            The image in the sequence, for {@value EImageLinkType#SETTLER} images.
	 */
	public OriginalImageLink(EImageLinkType type, int file, int sequence, int image) {
		this(type, file, sequence, image, 0);
	}

	/**
	 * Creates a new image link description for {@value EImageLinkType#GUI} images.
	 * 
	 * @param type
	 *            The type
	 * @param file
	 *            The file it is in
	 * @param sequence
	 *            The sequence index
	 */
	public OriginalImageLink(EImageLinkType type, int file, int sequence) {
		this(type, file, sequence, 0);
	}

	/**
	 * Gets the type of the image.
	 * 
	 * @return The image type
	 */
	public EImageLinkType getType() {
		return type;
	}

	/**
	 * Gets the file.
	 * 
	 * @return The files number.
	 */
	public int getFile() {
		return file;
	}

	/**
	 * Gets the seuqence index inside the file.
	 * <p>
	 * For GUI and LANDSCAPE images, this defines the image.
	 * 
	 * @return The index
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * Gets the image index inside the sequence.
	 * 
	 * @return The image index
	 */
	public int getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "image[type=" + type + ", file=" + file + ", sequence=" + sequence + ", image=" + image + "]";
	}

	/**
	 * Gets the length of this strip.
	 * 
	 * @return The length as int
	 */
	public int getLength() {
		return length;
	}

	@Override
	public String getName() {
		return String.format(Locale.ENGLISH, "original_%d_%s_%d", file, type.toString(), sequence);
	}

	@Override
	public int getImageIndex() {
		return image;
	}

	public EImageLinkType type() {
		return type;
	}

	public String getHumanName() {
		return humanName;
	}

	public void setFallback(OriginalImageLink fallback) {
		this.fallback = fallback;
	}

	public OriginalImageLink getFallback() {
		return fallback;
	}
}
