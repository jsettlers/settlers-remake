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

import jsettlers.graphics.reader.ImageMetadata;

/**
 * This isa gui image.
 * 
 * @author michael
 *
 */
public class GuiImage extends SingleImage {

	/**
	 * Creates a new GUI image.
	 * 
	 * @param provider
	 *            The provider.
	 */
	public GuiImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}

	/**
	 * draws the button at the given x and y coodringate.
	 * 
	 * @param gl
	 * @param x
	 *            left
	 * @param y
	 *            bottom
	 */
	// public void drawAt(GL2 gl, int x, int y) {
	// int left = x;
	// int bottom = y;
	//
	// bind(gl);
	// gl.glEnable(GL2.GL_TEXTURE_2D);
	// gl.glBegin(GL2.GL_QUADS);
	// gl.glTexCoord2f(0,0);
	// gl.glVertex2f(left, bottom);
	// gl.glTexCoord2f(1,0);
	// gl.glVertex2f(left + width, bottom);
	// gl.glTexCoord2f(1,1);
	// gl.glVertex2f(left + width, bottom + height);
	// gl.glTexCoord2f(0,1);
	// gl.glVertex2f(left, bottom + height);
	// gl.glEnd();
	// gl.glDisable(GL2.GL_TEXTURE_2D);
	//
	// }

}
