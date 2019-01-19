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

import go.graphics.GLDrawContext;
import go.graphics.IllegalBufferException;
import jsettlers.common.Color;

/**
 * This is an image that can be displayed on the GUI.
 * 
 * @author michael
 *
 */
public abstract class Image {

	/**
	 * Gets the (pixel) width of this image.
	 * 
	 * @return The image width.
	 */
	public abstract int getWidth();

	/**
	 * Gets the (pixel) height of this image.
	 * 
	 * @return The image height.
	 */
	public abstract int getHeight();

	public void drawAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		drawOnlyImageAt(gl, x, y, z, torsoColor, fow);
		drawOnlyShadowAt(gl, x, y, z);
	}

	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {}
	public void drawOnlyShadowAt(GLDrawContext gl, float x, float y, float z) {}

	/**
	 * Draws the image at a given rectangle.
	 * 
	 * @param gl
	 *            The gl context to draw on.
	 * @param x
	 *            The x coordinate to draw the left bound to.
	 * @param y
	 *            The y coordinate to draw the top bound to.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 */
	public abstract void drawImageAtRect(GLDrawContext gl, float x,
			float y, float width, float height);

	/**
	 * Multiplies the color with an float.
	 * 
	 * @param color
	 *            The color
	 * @param multiply
	 *            The value to multiply with in [0, 1]
	 * @return The dimmed (blacker) color.
	 */
	public static int dimColor(Color color, float multiply) {
		int iColor;
		if (multiply == 1) {
			iColor = color.getABGR();
		} else {
			iColor =
					Color.getABGR(color.getRed() * multiply, color.getGreen()
							* multiply, color.getBlue() * multiply,
							color.getAlpha());
		}
		return iColor;
	}

	/**
	 * Creates a crash report. This should not happen if we check that the texture is valid every time.
	 * 
	 * @param e
	 *            The exception.
	 */
	protected void handleIllegalBufferException(IllegalBufferException e) {
		// TODO Create crash report
		e.printStackTrace();
	}

}
