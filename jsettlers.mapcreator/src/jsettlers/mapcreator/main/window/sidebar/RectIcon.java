/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Rectangle icon
 * 
 * @author Andreas Butti
 */
public class RectIcon implements Icon {
	/**
	 * Size of the icon
	 */
	private int size;

	/**
	 * Color of the icon
	 */
	private Color color;

	/**
	 * Border color, <code>null</code> for no boarder
	 */
	private Color borderColor;

	/**
	 * Constructor
	 * 
	 * @param size
	 *            Size of the icon
	 * @param color
	 *            Color of the icon
	 */
	public RectIcon(int size, Color color) {
		this(size, color, null);
	}

	/**
	 * Constructor
	 * 
	 * @param size
	 *            Size of the icon
	 * @param color
	 *            Color of the icon
	 * @param borderColor
	 *            Border color, <code>null</code> for no boarder
	 */
	public RectIcon(int size, Color color, Color borderColor) {
		this.size = size;
		this.color = color;
		this.borderColor = borderColor;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, size, size);

		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(x, y, size, size);
		}
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

}
