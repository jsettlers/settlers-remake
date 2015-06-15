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
package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.utils.UIPanel;

public class Label extends UIPanel {
    public enum HorizontalAlignment {
        LEFT,
        CENTRE,
        RIGHT,
    }

	private final EFontSize size;
	private String[] words;
	private double[] widths = null;
	private double spaceWidth;
	private double lineHeight;
	private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTRE;

	public Label(String message, EFontSize size) {
		this.size = size;

		setText(message);
	}

	public void setText( String text )
    {
        words = text.split(" ");
    }

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
	{
	    this.horizontalAlignment = horizontalAlignment;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		String[] words = this.words; //local copy to avoid concurrant modification.

		TextDrawer drawer = gl.getTextDrawer(size);

		if (widths == null) {
			widths = new double[words.length];
			for (int i = 0; i < words.length; i++) {
				widths[i] = drawer.getWidth(words[i]);
			}
			spaceWidth = drawer.getWidth(" ");
			lineHeight = drawer.getHeight("j");
		}

		double maxwidth = getPosition().getWidth();

		StringBuilder line = new StringBuilder(words[0]);
		double linewidth = widths[0];
		double y = 0;
		for (int i = 1; i < words.length; i++) {
			double newlinewidth = linewidth + spaceWidth + widths[i];
			if (newlinewidth > maxwidth) {
				drawLine(drawer, line.toString(), y);
				line = new StringBuilder(words[i]);
				y += lineHeight;
				linewidth = widths[i];
			} else {
				line.append(" ");
				line.append(words[i]);
				linewidth = newlinewidth;
			}
		}
		drawLine(drawer, line.toString(), y);

	}

	private void drawLine(TextDrawer drawer, String string, double y) {
	    float left;
	    switch(horizontalAlignment){
        case LEFT:
            left = getPosition().getMinX();
            break;
        case RIGHT:
            left =  getPosition().getMaxX() - (float)drawer.getWidth(string);
            break;
        default:
        case CENTRE:
            left = getPosition().getCenterX() - (float)(drawer.getWidth(string) / 2);
            break;
	    }
        float bottom = getPosition().getMaxY() - (float)y - (float)drawer.getHeight("A");
	    drawer.drawString(left, bottom, string);
	}
}