/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.lookandfeel.ui.img;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;

import jsettlers.main.swing.lookandfeel.DrawHelper;

/**
 * Image loader for UI Images
 * 
 * @author Andreas Butti
 */
public final class UiImageLoader {

	/**
	 * Cached images
	 */
	private static final HashMap<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

	/**
	 * Utility class
	 */
	private UiImageLoader() {
	}

	/**
	 * Get or load an image
	 * 
	 * @param name
	 *            Name of the image to load
	 * @return Image, do not change!
	 */
	public static BufferedImage get(String name) {
		BufferedImage image = IMAGE_CACHE.get(name);
		if (image != null) {
			return image;
		}

		image = DrawHelper.toBufferedImage(new ImageIcon(UiImageLoader.class.getResource(name)).getImage());

		IMAGE_CACHE.put(name, image);
		return image;
	}
}
