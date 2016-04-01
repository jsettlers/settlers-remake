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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Button for Scrollbar
 * 
 * @author Andreas Butti
 */
public class ScrollbarUiButton extends JButton implements SwingConstants {
	private static final long serialVersionUID = 1L;

	/**
	 * The direction of the arrow. One of {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH}, {@code SwingConstants.EAST} or
	 * {@code SwingConstants.WEST}.
	 */
	protected int direction;

	/**
	 * Creates a {@code BasicArrowButton} whose arrow is drawn in the specified direction and with the specified colors.
	 *
	 * @param direction
	 *            the direction of the arrow; one of {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH}, {@code SwingConstants.EAST} or
	 *            {@code SwingConstants.WEST}
	 * @param arrowColor
	 *            Color of the arrow
	 */
	public ScrollbarUiButton(int direction, Color arrowColor) {
		setRequestFocusEnabled(false);
		setDirection(direction);
		setForeground(arrowColor);
	}

	/**
	 * Sets the direction of the arrow.
	 *
	 * @param direction
	 *            the direction of the arrow; one of of {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH}, {@code SwingConstants.EAST} or
	 *            {@code SwingConstants.WEST}
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public void paint(Graphics g) {
		int w = getSize().width;
		int h = getSize().height;
		boolean isPressed = getModel().isPressed();

		if (isPressed) {
			g.translate(1, 1);
		}

		// Draw the arrow
		int size = Math.min((h - 4) / 2, (w - 4) / 2);
		size = Math.max(size, 2);
		paintTriangle(g, (w - size) / 2, (h - size) / 2, size, direction);

		// Reset the Graphics back to it's original settings
		if (isPressed) {
			g.translate(-1, -1);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(5, 5);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public boolean isFocusTraversable() {
		return false;
	}

	/**
	 * Paints a triangle.
	 *
	 * @param g
	 *            the {@code Graphics} to draw to
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param size
	 *            the size of the triangle to draw
	 * @param direction
	 *            the direction in which to draw the arrow; one of {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
	 *            {@code SwingConstants.EAST} or {@code SwingConstants.WEST}
	 */
	public void paintTriangle(Graphics g, int x, int y, int size, int direction) {
		size = Math.max(size, 2);
		int mid = (size / 2) - 1;

		g.translate(x, y);
		g.setColor(getForeground());

		switch (direction) {
		case NORTH:
			for (int i = 0; i < size; i++) {
				g.drawLine(mid - i, i, mid + i, i);
			}
			break;
		case SOUTH:
			for (int i = size - 1, j = 0; i >= 0; i--, j++) {
				g.drawLine(mid - i, j, mid + i, j);
			}
			break;
		case WEST:
			for (int i = 0; i < size; i++) {
				g.drawLine(i, mid - i, i, mid + i);
			}
			break;
		case EAST:
			for (int i = size - 1, j = 0; i >= 0; i--, j++) {
				g.drawLine(j, mid - i, j, mid + i);
			}
			break;
		}
		g.translate(-x, -y);
	}
}
