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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ImageLink {
	private static final Pattern ORIGINAL_LINK = Pattern
			.compile("original_(\\d+)_(SETTLER|GUI|LANDSCAPE)_(\\d+)");

	public static ImageLink fromName(String name, int imageIndex) {
		Matcher matcher = ORIGINAL_LINK.matcher(name);
		if (matcher.matches()) {
			return new OriginalImageLink(EImageLinkType.valueOf(matcher.group(2)), Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher
					.group(3)), imageIndex);
		} else {
			return new DirectImageLink(name + "." + imageIndex);
		}
	}

	public abstract String getName();

	public abstract int getImageIndex();
}
