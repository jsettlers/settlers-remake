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
import jsettlers.graphics.map.draw.DrawBuffer;

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

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 */
	public abstract void draw(GLDrawContext gl, Color color);

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 * @param multiply
	 *            A number to multiply all color values with.
	 */
	public abstract void draw(GLDrawContext gl, Color color, float multiply);

	/**
	 * Convenience method, calls drawAt(gl, x, y, -1).
	 * 
	 * @param gl
	 *            The context.
	 * @param x
	 *            The x position of the center.
	 * @param y
	 *            The y position of the center
	 */
	public abstract void drawAt(GLDrawContext gl, float x, float y);

	/**
	 * Draws an object for a given player. The player -1 means no player.
	 * 
	 * @param gl
	 *            The gl context.
	 * @param x
	 *            The x coordinate on the screen.
	 * @param y
	 *            The y coordinate on the screen.
	 * @param color
	 *            The player number.
	 */
	public abstract void drawAt(GLDrawContext gl, float x, float y, Color color);

	/**
	 * Draws the image at a given {@link DrawBuffer}.
	 * 
	 * @param gl
	 *            The gl context to use.
	 * @param buffer
	 *            The draw buffer to draw the image to.
	 * @param viewX
	 *            The x position the center of the image should be.
	 * @param viewY
	 *            The y position the center of the image should be.
	 * @param color
	 *            The color the image should have (argb)
	 */
	public abstract void drawAt(GLDrawContext gl, DrawBuffer buffer,
			float viewX, float viewY, int color);

	/**
	 * Draws the image at a given {@link DrawBuffer}.
	 * 
	 * @param gl
	 *            The gl context to use.
	 * @param buffer
	 *            The draw buffer to draw the image to.
	 * @param viewX
	 *            The x position the center of the image should be.
	 * @param viewY
	 *            The y position the center of the image should be.
	 * @param color
	 *            The color the image should have (argb)
	 * @param multiply
	 *            A value to multiply the color with.
	 */
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, Color color, float multiply) {
		int iColor = dimColor(color, multiply);
		drawAt(gl, buffer, viewX, viewY, iColor);
	}

	/**
	 * Draws the image at a given rectangle.
	 * 
	 * @param gl
	 *            The gl context to draw on.
	 * @param minX
	 *            The x coordinate to draw the left bound to.
	 * @param minY
	 *            The y coordinate to draw the top bound to.
	 * @param maxX
	 *            The x coordinate to draw the right bound to.
	 * @param maxY
	 *            The y coordinate to draw the bottom bound to.
	 */
	public abstract void drawImageAtRect(GLDrawContext gl, float minX,
			float minY, float maxX, float maxY);

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
