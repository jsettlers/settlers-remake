/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.lookandfeel.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * Draws the yellow border on the stone texture
 * 
 * @author Andreas Butti
 */
public class BorderDrawer {

	/**
	 * Use an even number!
	 */
	private static final int CORNER_LENGTH = 26;

	private static final int PADDING = 20;

	private static final int SHADOW_OFFSET_X = 2;
	private static final int SHADOW_OFFSET_Y = 3;

	/**
	 * Graphics to draw with
	 */
	private final Graphics2D graphics;

	/**
	 * Line with to draw
	 */
	private final int lineWidth;

	/**
	 * Paint to use (Texture)
	 */
	private Paint paint;

	/**
	 * Original texture to back up
	 */
	private Stroke originalStroke;

	private int x1;
	private int y1;
	private int x2;
	private int y2;

	/**
	 * Constructor
	 * 
	 * @param g
	 *            Graphics to draw with
	 * @param lineWidth
	 *            Line with to draw
	 * @param x1
	 *            Start x position
	 * @param y1
	 *            Start y position
	 * @param x2
	 *            End x position
	 * @param y2
	 *            End y position
	 */
	public BorderDrawer(Graphics2D g, int lineWidth, int x1, int y1, int x2, int y2) {
		this.graphics = g;
		this.lineWidth = lineWidth;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 * @param paint
	 *            Paint to use (Texture)
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	/**
	 * @return Paint to use (Texture)
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * Draw a horizontal line
	 * 
	 * @param y
	 *            Y position
	 */
	protected void drawHorizontal(int y) {
		int cl2 = CORNER_LENGTH / 2;
		graphics.drawLine(x1 + cl2 + PADDING, y, x2 - cl2 - PADDING, y);
	}

	/**
	 * Draw a vertical line
	 * 
	 * @param x
	 *            x position
	 * @param right
	 *            Draw the end to the right or the left side
	 */
	protected void drawVertical(int x, boolean right) {
		initGraphics();

		// draw shadow
		graphics.setColor(new Color(0, 0, 0, 150));
		drawVertical0(x + SHADOW_OFFSET_X, y1 + SHADOW_OFFSET_Y, y2 + SHADOW_OFFSET_Y, right);

		graphics.setPaint(paint);
		drawVertical0(x, y1, y2, right);

		resetGraphics();
	}

	/**
	 * Draw a vertical line, without initialize graphics
	 * 
	 * @param x
	 *            X Position
	 * @param right
	 *            Draw the end to the right or the left side
	 */
	private void drawVertical0(int x, int y1, int y2, boolean right) {
		int cl2 = CORNER_LENGTH / 2;
		int yA = y1 + cl2 + PADDING;
		int yB = y2 - cl2 - PADDING;
		graphics.drawLine(x, yA, x, yB);

		// == corner ==

		if (right) {
			x -= CORNER_LENGTH;
		}

		// UPPER - down
		graphics.drawLine(x + cl2, y1 + PADDING, x + cl2, y1 + PADDING + CORNER_LENGTH);
		// LOWER - up
		graphics.drawLine(x + cl2, yB - cl2, x + cl2, yB + cl2);

		// UPPER - horizontal
		graphics.drawLine(x, yA, x + CORNER_LENGTH, yA);
		// LOWER - horizontal
		graphics.drawLine(x, yB, x + CORNER_LENGTH, yB);

		// UPPER - up
		if (right) {
			x -= CORNER_LENGTH;
		}
		graphics.drawLine(x + CORNER_LENGTH, y1 + PADDING - cl2, x + CORNER_LENGTH, y1 + PADDING + cl2);
		// LOWER - down
		graphics.drawLine(x + CORNER_LENGTH, yB, x + CORNER_LENGTH, yB + CORNER_LENGTH);

	}

	/**
	 * Internal draw rect, without initialize graphics
	 */
	private void drawRectInternal(int x1, int y1, int x2, int y2) {
		drawHorizontal(y1 + PADDING); // top
		drawHorizontal(y2 - PADDING); // bottom

		drawVertical0(x1 + PADDING, y1, y2, false); // left
		drawVertical0(x2 - PADDING, y1, y2, true); // right
	}

	/**
	 * Draw the rect
	 */
	public void drawRect() {
		initGraphics();

		// draw shadow
		graphics.setColor(new Color(0, 0, 0, 150));
		drawRectInternal(x1 + SHADOW_OFFSET_X, y1 + SHADOW_OFFSET_Y, x2 + SHADOW_OFFSET_X, y2 + SHADOW_OFFSET_Y);

		graphics.setPaint(paint);
		drawRectInternal(x1, y1, x2, y2);

		resetGraphics();
	}

	/**
	 * Restore the original stroke
	 */
	private void resetGraphics() {
		graphics.setStroke(originalStroke);
	}

	/**
	 * Initialize the graphics with the stroke
	 */
	private void initGraphics() {
		this.originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(lineWidth));
	}
}
