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

import jsettlers.graphics.map.draw.ImageProvider;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Label UI, with different stylings
 * 
 * @author Andreas Butti
 *
 */
public class SettlersDynamicLabelUi extends BasicLabelUI {

	private final Color foregroundColor;
	private final BufferedImage backgroundImage;
	private final Border border = BorderFactory.createEmptyBorder(2, 5, 2, 5);

	/**
	 * Constructor
	 * 
	 * @param foregroundColor
	 *            Foreground color of the Label
	 * @param x
	 *            Subimage position / size
	 * @param y
	 *            Subimage position / size
	 * @param width
	 *            Subimage position / size
	 * @param heigth
	 *            Subimage position / size
	 */
	public SettlersDynamicLabelUi(Color foregroundColor, int x, int y, int width, int heigth) {
		this.foregroundColor = foregroundColor;
		ImageProvider imageProvider = ImageProvider.getInstance();
		BufferedImage guiImage = imageProvider.getGuiImage(2, 13, null).convertToBufferedImage();

		this.backgroundImage = guiImage.getSubimage(x, y, width, heigth);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setBorder(border);
		c.setFont(UIDefaults.FONT);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(super.getPreferredSize(c).width, backgroundImage.getHeight());
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		int w = c.getWidth();
		int bgw = backgroundImage.getWidth();
		int bgh = backgroundImage.getHeight();

		// left border
		g.drawImage(backgroundImage, 0, 0, c);

		// draw center
		for (int x = bgw; x < w; x += bgw - 6) {
			g.drawImage(backgroundImage, x, 0, x + bgw - 6, bgh, 3, 0, bgw - 3, bgh, c);
		}

		// draw right border
		g.drawImage(backgroundImage, w, 0, w - 2, bgh, bgw - 2, 0, bgw, bgh, c);
		super.paint(g, c);
	}
}
