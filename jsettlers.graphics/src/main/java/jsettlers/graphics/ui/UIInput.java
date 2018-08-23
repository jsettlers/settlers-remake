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
package jsettlers.graphics.ui;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOKeyEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import jsettlers.common.action.Action;
import jsettlers.graphics.utils.FocusAction;

/**
 * This is single line text input field.
 * 
 * @author Michael Zangl
 *
 */
public class UIInput extends UIPanel implements GOEventHandler {

	private StringBuffer inputString = new StringBuffer();
	private int carret = 0;

	@Override
	public void phaseChanged(GOEvent event) {
	}

	@Override
	public void finished(GOEvent event) {
		String code = ((GOKeyEvent) event).getKeyCode();
		if (code.length() == 1) {
			inputString.insert(carret, code);
			carret++;
		} else if (code.equals("BACK_SPACE") && carret > 0) {
			// inputString = inputString.substring(0, inputString.length() - 1);
			inputString.replace(carret - 1, carret, "");
			carret--;
		} else if (code.equals("LEFT") && carret > 0) {
			carret--;
		} else if (code.equals("RIGHT") && carret < inputString.length()) {
			carret++;
		}
	}

	@Override
	public void aborted(GOEvent event) {}

	private static GeometryHandle geometry = null;

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);
		TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);

		float textHeight = drawer.getHeight(inputString.toString() + "X");
		float y = getPosition().getCenterY() - textHeight / 2;
		float x = getPosition().getMinX() + 2;
		drawer.drawString(x, y, inputString.toString());

		if(geometry == null || !geometry.isValid()) geometry = gl.storeGeometry(new float[] {0, 0, 0, 1}, EGeometryFormatType.VertexOnly2D, false, "uiinput-line");

		float carretX = x + drawer.getWidth(inputString.substring(0, carret) + "X") - drawer.getWidth("X");

		try {
			gl.draw2D(geometry, null, EGeometryType.LineStrip, 0, 2, carretX, y, 0, 0, textHeight, 0, null, 1);
		} catch (IllegalBufferException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return new FocusAction(this);
	}
}
