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

import java.util.ArrayList;

/**
 * Displays a text on the GUI.
 * 
 * @author Michael Zangl
 */
public class Label extends UIPanel {
	public enum EHorizontalAlignment {
		LEFT,
		CENTER,
		RIGHT,
	}

	private static class Line {
		private final String string;
		private final double linewidth;

		public Line(String string, double linewidth) {
			this.string = string;
			this.linewidth = linewidth;
		}

	}

	private final EFontSize size;
	private String[] words;
	private double[] widths = null;
	private double spaceWidth;
	private double lineHeight;
	private double lineBottom;
	private final EHorizontalAlignment horizontalAlignment;

	/**
	 * Constructs a new Label with center alignment.
	 * 
	 * @param text
	 *            The text to display
	 * @param size
	 *            The font size for the text.
	 */
	public Label(String message, EFontSize size) {
		this(message, size, EHorizontalAlignment.CENTER);
	}

	/**
	 * Constructs a new Label.
	 * 
	 * @param text
	 *            The text to display
	 * @param size
	 *            The font size for the text.
	 * @param horizontalAlignment
	 *            The horizontal alignment of the text.
	 */
	public Label(String text, EFontSize size, EHorizontalAlignment horizontalAlignment) {
		this.size = size;
		this.horizontalAlignment = horizontalAlignment;

		setText(text);
	}

	/**
	 * Sets the text to display on the label.
	 * 
	 * @param text
	 *            The text to display.
	 */
	public synchronized void setText(String text) {
		words = text.split(" ");
		widths = null;
	}

	@Override
	public synchronized void drawAt(GLDrawContext gl) {
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

		StringBuilder lineText = new StringBuilder(words[0]);
		double linewidth = widths[0];
		ArrayList<Line> lines = new ArrayList<>();
		for (int i = 1; i < words.length; i++) {
			double newlinewidth = linewidth + spaceWidth + widths[i];
			if (newlinewidth > maxwidth) {
				lines.add(new Line(lineText.toString(), linewidth));
				lineText = new StringBuilder(words[i]);
				linewidth = widths[i];
			} else {
				lineText.append(" ");
				lineText.append(words[i]);
				linewidth = newlinewidth;
			}
		}
		lines.add(new Line(lineText.toString(), linewidth));

		double totalHeight = lines.size() * lineHeight;
		float y = (float) (getPosition().getCenterY() + totalHeight / 2 - lineBottom);
		for (Line line : lines) {
			drawLine(drawer, line, y);
			y -= lineHeight;
		}
	}

	private void drawLine(TextDrawer drawer, Line line, float bottom) {
		float left;
		switch (horizontalAlignment) {
		case LEFT:
			left = getPosition().getMinX();
			break;
		case RIGHT:
			left = (float) (getPosition().getMaxX() - line.linewidth);
			break;
		default:
		case CENTER:
			left = (float) (getPosition().getCenterX() - line.linewidth / 2);
			break;
		}
		drawer.drawString(left, bottom, line.string);
	}
}
