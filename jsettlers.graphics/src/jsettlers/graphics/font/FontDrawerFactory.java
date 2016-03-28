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
package jsettlers.graphics.font;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.map.IGLProvider;
import jsettlers.graphics.map.draw.DrawBuffer;

/**
 * This class generates a (cached) fall back font drawer.
 * 
 * @author Michael Zangl
 * @see FontDrawer
 */
public class FontDrawerFactory implements ITextDrawerFactory, IGLProvider {

	private GLDrawContext lastGl = null;
	private FontDrawer[] cache;
	private final DrawBuffer buffer = new DrawBuffer(this);

	@Override
	public TextDrawer getTextDrawer(GLDrawContext gl, EFontSize size) {
		if (gl != lastGl) {
			lastGl = gl;
			cache = new FontDrawer[EFontSize.values().length];
		}

		FontDrawer drawer = cache[size.ordinal()];
		if (drawer == null) {
			drawer = new FontDrawer(gl, buffer, size);
			cache[size.ordinal()] = drawer;
		}
		return drawer;
	}

	@Override
	public GLDrawContext getGl() {
		return lastGl;
	}
}
