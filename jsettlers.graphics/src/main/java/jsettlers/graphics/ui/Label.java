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
import jsettlers.common.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public enum EVerticalAlignment {
		TOP,
		CENTER,
		BOTTOM,
	}

	private static class Line {
		private final String string;
		private final double linewidth;

		public Line(String string, double linewidth) {
			this.string = string;
			this.linewidth = linewidth;
		}
	}

	private static class Word {
		private String word;
		private boolean lineBreakBefore;
		private double width;

		private Word(String word, boolean lineBreakBefore) {
			this.word = word;
			this.lineBreakBefore = lineBreakBefore;
			this.width = Double.NaN;
		}

		public double getWidth(TextDrawer drawer) {
			if (Double.isNaN(width)) {
				width = drawer.getWidth(word);
			}
			return width;
		}
	}

	private final EFontSize size;
	private List<Word> words = new ArrayList<>();
	private double spaceWidth = Double.NaN;
	private double lineHeight;
	private double lineBottom;
	private final EHorizontalAlignment horizontalAlignment;
	private final EVerticalAlignment verticalAlignment;

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
		this(text, size, horizontalAlignment, EVerticalAlignment.CENTER);
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
	 * @param verticalAlignment
	 *            The vertical alignment of the text.
	 */
	public Label(String text, EFontSize size, EHorizontalAlignment horizontalAlignment, EVerticalAlignment verticalAlignment) {
		this.size = size;
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;

		setText(text);
	}

	/**
	 * Sets the text to display on the label.
	 * 
	 * @param text
	 *            The text to display.
	 */
	public synchronized void setText(String text) {
		words.clear();
		Matcher matcher = Pattern.compile("[ \n]").matcher(text);
		boolean requestLinebreak = false;
		int wordStart = 0;
		while (matcher.find()) {
			words.add(new Word(text.substring(wordStart, matcher.start()), requestLinebreak));
			requestLinebreak = matcher.group().equals("\n");
			wordStart = matcher.end();
		}

		words.add(new Word(text.substring(wordStart), requestLinebreak));
	}

	@Override
	public synchronized void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		TextDrawer drawer = gl.getTextDrawer(size);
		drawer.setColor(Color.WHITE);

		if (Double.isNaN(spaceWidth)) {
			spaceWidth = drawer.getWidth(" ");
			lineHeight = drawer.getHeight("j");
			lineBottom = drawer.getHeight("A");
		}

		double maxwidth = getPosition().getWidth();

		StringBuilder lineText = new StringBuilder();
		double linewidth = -spaceWidth;
		ArrayList<Line> lines = new ArrayList<>();
		boolean firstWord = true;
		for (Word word : words) {
			double newlinewidth = linewidth + spaceWidth + word.getWidth(drawer);
			if (!firstWord && (word.lineBreakBefore || newlinewidth > maxwidth)) {
				lines.add(new Line(lineText.toString(), linewidth));
				lineText = new StringBuilder(word.word);
				linewidth = word.getWidth(drawer);
			} else {
				if (!firstWord) {
					lineText.append(" ");
				}
				lineText.append(word.word);
				linewidth = newlinewidth;
			}
			firstWord = false;
		}
		lines.add(new Line(lineText.toString(), linewidth));

		double totalHeight = lines.size() * lineHeight;
		float y;
		y = getTextBoxTop(totalHeight);

		y -= lineBottom;
		for (Line line : lines) {
			drawLine(drawer, line, y);
			y -= lineHeight;
		}
	}

	private float getTextBoxTop(double totalHeight) {
		float y;
		switch (verticalAlignment) {
		case TOP:
			y = getPosition().getMaxY();
			break;
		case BOTTOM:
			y = (float) (getPosition().getMinY() + totalHeight);
			break;
		case CENTER:
		default:
			y = (float) (getPosition().getCenterY() + totalHeight / 2);
		}
		return y;
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
