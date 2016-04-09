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
package jsettlers.main.swing.menu.openpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;

/**
 * Icon to clear search
 *
 * @author Andreas Butti
 */
public class ClearIcon implements Icon {

	/**
	 * If the mouse is over the icon
	 */
	private boolean hover = false;

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		Stroke oldStroke = g.getStroke();

		g.setColor(hover ? Color.GRAY : Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.fillOval(x + 2, y + 2, 18, 18);
		g.setColor(Color.WHITE);
		g.drawLine(x + 6, y + 6, x + 15, y + 15);
		g.drawLine(x + 15, y + 6, x + 6, y + 15);

		g.setStroke(oldStroke);
	}

	/**
	 * @param hover
	 *            If the mouse is over the icon
	 */
	public void setHover(boolean hover) {
		this.hover = hover;
	}

	/**
	 * @return If the mouse is over the icon
	 */
	public boolean isHover() {
		return hover;
	}

	@Override
	public int getIconWidth() {
		return 22;
	}

	@Override
	public int getIconHeight() {
		return 22;
	}
}
