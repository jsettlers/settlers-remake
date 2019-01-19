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
 * This is an image link to a image file in the resource directory.
 * 
 * @author Michael Zangl
 */
public class DirectImageLink extends ImageLink {
	private static final long serialVersionUID = -7746487283146780673L;

	private final String name;

	/**
	 * Create a new image link object.
	 * 
	 * @param name
	 *            The name.
	 */
	public DirectImageLink(String name) {
		this.name = name;
	}

	@Override
	public String getHumanName() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getImageIndex() {
		return 0;
	}

	@Override
	public int getSequence() {
		return getSequence();
	}

	@Override
	public EImageLinkType getType() {
		return getType();
	}

	@Override
	public int getFile() {
		return getFile();
	}
}
