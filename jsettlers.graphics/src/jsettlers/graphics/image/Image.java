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
import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;

public abstract class Image {

	/**
	 * Convenience method, calls drawAt(gl, x, y, -1);
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

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void drawImageAtRect(GLDrawContext gl, float minX,
			float minY, float maxX, float maxY);

	public abstract void drawAt(GLDrawContext gl, DrawBuffer buffer,
			float viewX, float viewY, int iColor);

	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, Color color, float multiply) {
		int iColor = dimColor(color, multiply);
		drawAt(gl, buffer, viewX, viewY, iColor);

	}

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

}