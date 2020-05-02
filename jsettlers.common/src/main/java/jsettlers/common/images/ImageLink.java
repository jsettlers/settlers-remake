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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This defines a link to an image.
 * 
 * @author Michael Zangl
 * @see DirectImageLink For linking to a file
 * @see OriginalImageLink For linking to a Settlers 3 graphic.
 */
public abstract class ImageLink implements Serializable {
	private static final long serialVersionUID = 1572028978425777114L;

	private static final Pattern ORIGINAL_LINK = Pattern
			.compile("original_(\\d+)_(SETTLER|GUI|LANDSCAPE)_(\\d+)(?:_(\\d+))?");
	private static final int ORIGINAL_LINK_FILE = 1;
	private static final int ORIGINAL_LINK_TYPE = 2;
	private static final int ORIGINAL_LINK_SEQUENCE = 3;
	private static final int ORIGINAL_LINK_INDEX = 4;

	/**
	 * Converts a given name to an image link.
	 * <p>
	 * Names can either be direct names of the png files or they can have the for "original_&lt;file>_&lt;type>_&lt;sequence
	 * 
	 * @param name
	 *            The name.
	 * @param imageIndex
	 *            The index in the sequence.
	 * @return The image link for that image, no matter if it exists or not.
	 */
	public static ImageLink fromName(String name, int imageIndex) {
		Matcher matcher = ORIGINAL_LINK.matcher(name);
		if (matcher.matches()) {
			EImageLinkType type = EImageLinkType.valueOf(matcher.group(ORIGINAL_LINK_TYPE));
			int file = Integer.parseInt(matcher.group(ORIGINAL_LINK_FILE));
			int sequence = Integer.parseInt(matcher.group(ORIGINAL_LINK_SEQUENCE));
			return new OriginalImageLink(type, file, sequence, imageIndex);
		} else {
			return new DirectImageLink(name + "." + imageIndex);
		}
	}

	/**
	 * Converts a given name to an image link.
	 * <p>
	 * Names can either be direct names of the png files or they can have the for "original_&lt;file>_&lt;type>_&lt;sequence
	 *
	 * @param name
	 *            The name.
	 * @return The image link for that image, no matter if it exists or not.
	 */
	public static ImageLink fromName(String name) {
		Matcher matcher = ORIGINAL_LINK.matcher(name);
		if (matcher.matches()) {
			EImageLinkType type = EImageLinkType.valueOf(matcher.group(ORIGINAL_LINK_TYPE));
			int file = Integer.parseInt(matcher.group(ORIGINAL_LINK_FILE));
			int sequence = Integer.parseInt(matcher.group(ORIGINAL_LINK_SEQUENCE));
			int imageIndex = 0;
			String indexStr = matcher.group(ORIGINAL_LINK_INDEX);
			if(indexStr != null) imageIndex = Integer.parseInt(indexStr);
			return new OriginalImageLink(type, file, sequence, imageIndex);
		} else {
			return new DirectImageLink(name);
		}
	}

	/**
	 * Gets the name of this link.
	 * 
	 * @return A name that can be converted back to the link.
	 * @see #fromName(String, int)
	 */
	public abstract String getName();

	/**
	 * Gets the image index in the sequence.
	 * 
	 * @return The image index.
	 */
	public abstract int getImageIndex();

	public abstract int getSequence();

	public abstract EImageLinkType getType();

    public abstract int getFile();

    public abstract String getHumanName();
}
