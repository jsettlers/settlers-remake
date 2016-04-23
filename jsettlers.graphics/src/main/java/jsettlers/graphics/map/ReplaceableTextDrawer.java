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
package jsettlers.graphics.map;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.font.ITextDrawerFactory;

/**
 * This is a text drawer that uses the text drawer provided by opengl or the provided text drawer if one is given.
 * 
 * @author Michael Zangl
 */
public class ReplaceableTextDrawer implements ITextDrawerFactory {

	private ITextDrawerFactory textDrawerFactory;

	/**
	 * Sets the factory to use to generate text drawer if needed.
	 * 
	 * @param drawerFactory
	 *            The factory to use. <code>null</code> to use the default.
	 */
	public void setTextDrawerFactory(ITextDrawerFactory drawerFactory) {
		this.textDrawerFactory = drawerFactory;
	}

	/**
	 * Gets the text drawer to use.
	 * 
	 * @param gl
	 *            The gl context to use as fallback
	 * @param size
	 *            The font size
	 * @return A text drawer.
	 */
	@Override
	public TextDrawer getTextDrawer(GLDrawContext gl, EFontSize size) {
		if (textDrawerFactory != null) {
			return textDrawerFactory.getTextDrawer(gl, size);
		} else {
			return gl.getTextDrawer(size);
		}
	}

}
