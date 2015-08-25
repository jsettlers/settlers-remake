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
package jsettlers.graphics.ui;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class Label extends UIPanel {
	public enum HorizontalAlignment {
		LEFT,
		CENTER,
		RIGHT,
	}

	private final EFontSize size;
	private String[] words;
	private double[] widths = null;
	private double spaceWidth;
	private double lineHeight;
	private double lineBottom;
	private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;

	public Label(String message, EFontSize size) {
		this.size = size;

		setText(message);
	}

	public void setText(String text) {
		words = text.split(" ");
	}

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setMessage(String message) {
		words = message.split(" ");
		widths = null;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		TextDrawer drawer = gl.getTextDrawer(size);

		if (widths == null) {
			widths = new double[words.length];
			for (int i = 0; i < words.length; i++) {
				widths[i] = drawer.getWidth(words[i]);
			}
			spaceWidth = drawer.getWidth(" ");
			lineHeight = drawer.getHeight("j");
			lineBottom = drawer.getHeight("A");
		}

		double maxwidth = getPosition().getWidth();

		StringBuilder line = new StringBuilder(words[0]);
		double linewidth = widths[0];
		double y = 0;
		for (int i = 1; i < words.length; i++) {
			double newlinewidth = linewidth + spaceWidth + widths[i];
			if (newlinewidth > maxwidth) {
				drawLine(drawer, line.toString(), y, linewidth);
				line = new StringBuilder(words[i]);
				y += lineHeight;
				linewidth = widths[i];
			} else {
				line.append(" ");
				line.append(words[i]);
				linewidth = newlinewidth;
			}
		}
		drawLine(drawer, line.toString(), y, linewidth);

	}

	private void drawLine(TextDrawer drawer, String string, double y, double linewidth) {
		float left;
		switch (horizontalAlignment) {
		case LEFT:
			left = getPosition().getMinX();
			break;
		case RIGHT:
			left = (float) (getPosition().getMaxX() - linewidth);
			break;
		default:
		case CENTER:
			left = (float) (getPosition().getCenterX() - linewidth / 2);
			break;
		}
		float bottom = (float) (getPosition().getMaxY() - y - lineBottom);
		drawer.drawString(left, bottom, string);
	}
}
