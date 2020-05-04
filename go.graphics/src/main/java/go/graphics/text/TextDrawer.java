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
package go.graphics.text;

import go.graphics.AbstractColor;

public interface TextDrawer {

	/**
	 * Renders the given text centered around cx, cy.
	 * 
	 * @param cx
	 *            The center in x direction.
	 * @param cy
	 *            The center in y direction.
	 * @param text
	 *            The text to render.
	 */
	default void renderCentered(float cx, float cy, String text) {
		drawString(cx-(getWidth(text)/2), cy-(getHeight(text)/2), text);
	}

	/**
	 * Draws a string
	 *  @param x
	 *            Left bound.
	 * @param y
	 *            Bottom line.
	 * @param string
	 */
	default void drawString(float x, float y, String string) {
		drawString(x, y, null, string);
	}

	/**
	 * Draws a string
	 *  @param x
	 *            Left bound.
	 * @param y
	 *            Bottom line.
	 * @param color
	 * @param string
	 */
	void drawString(float x, float y, AbstractColor color, String string);

	float getWidth(String string);

	float getHeight(String string);
}