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
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.utils.ImageUtils;

/**
 * Label UI, with different stylings
 * 
 * @author Andreas Butti
 *
 */
public class SettlersLabelUi extends BasicLabelUI {

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
	public SettlersLabelUi(Color foregroundColor, int x, int y, int width, int heigth) {
		this.foregroundColor = foregroundColor;
		ImageProvider prv = ImageProvider.getInstance();
		BufferedImage img = ImageUtils.convertToBufferedImage(prv.getGuiImage(2, 13));

		backgroundImage = img.getSubimage(x, y, width, heigth);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setPreferredSize(getPreferredSize(c));
		c.setBorder(border);
		c.setFont(UIDefaults.FONT);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.drawImage(backgroundImage, 0, 0, c);

		super.paint(g, c);
	}

}
