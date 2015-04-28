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
package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.graphics.image.Image;

public final class ImageUtils {
	private ImageUtils() {
	}

	/**
	 * Draws an image at a rect preserving the images aspect.
	 * <p>
	 * Does not change the color!
	 * 
	 * @param gl
	 * @param image
	 * @param aleft
	 * @param abottom
	 * @param aright
	 * @param atop
	 */
	public static void drawAtRectAspect(GLDrawContext gl, Image image,
			float aleft, float abottom, float aright, float atop) {
		float imageaspect = image.getWidth() / image.getHeight();
		float left, right, top, bottom;
		if ((aright - aleft) / (atop - abottom) > imageaspect) {
			// image is too wide
			float center = (aleft + aright) / 2.0f;
			float halfwidth = (atop - abottom) / 2.0f * imageaspect;
			left = center - halfwidth;
			right = center + halfwidth;
			bottom = abottom;
			top = atop;
		} else {
			float center = (abottom + atop) / 2.0f;
			float halfheight = (aright - aleft) / 2.0f / imageaspect;
			left = aleft;
			right = aright;
			bottom = center - halfheight;
			top = center + halfheight;
		}

		image.drawImageAtRect(gl, left, bottom, right, top);
	}
}
