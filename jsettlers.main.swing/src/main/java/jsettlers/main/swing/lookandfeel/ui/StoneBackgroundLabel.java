/*******************************************************************************
 * Copyright (c) 2016 - 2018
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
import javax.swing.plaf.basic.BasicLabelUI;

import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;

/**
 * Label UI, with stone background
 *
 * @author Andreas Butti
 *
 */
public class StoneBackgroundLabel extends BasicLabelUI {

	/**
	 * Foreground color
	 */
	private final Color foregroundColor;

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("ui_static_info_bg/ui_static-info-bg.png");

	/**
	 * border scale factor
	 */
	private float BORDER_FACTOR = 0.3f;

	/**
	 * Border images if the Button is not pressed
	 */
	private final BufferedImage[] BORDER = {
		UiImageLoader.get("ui_static_info_bg/ui_static-info-corner-upper-left.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info_border-top.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info-corner-upper-right.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info_border-right.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info-corner-bottom-right.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info_border-bottom.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info-corner-bottom_left.png"),
		UiImageLoader.get("ui_static_info_bg/ui_static-info_border-left.png")
	};

	/**
	 * Fixed padding, the border image is to big
	 */
	private int paddingTop = (int) (BORDER[1/* top */].getHeight() * BORDER_FACTOR);

	/**
	 * Fixed padding, the border image is to big
	 */
	private int paddingBottom = (int) (BORDER[5/* bottom */].getHeight() * BORDER_FACTOR);

	/**
	 * Constructor
	 *
	 * @param foregroundColor
	 *            Foreground color of the Label
	 */
	public StoneBackgroundLabel(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setFont(UIDefaults.FONT);
		c.setBorder(BorderFactory.createEmptyBorder(paddingTop, (int) (BORDER[7/* left */].getWidth() * BORDER_FACTOR),
				paddingBottom, (int) (BORDER[3/* right */].getWidth() * BORDER_FACTOR)));
		c.setOpaque(false);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension size = super.getPreferredSize(c);

		size.width += (BORDER[3/* right */].getWidth() + BORDER[7/* left */].getWidth()) * BORDER_FACTOR;
		// size.height += paddingTop + paddingBottom;

		return size;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		// Repeat the graphic as much as needed, the graphic is designed for this, so the start fits to the end of the graphic
		for (int i = 0; i < c.getWidth(); i += backgroundImage.getWidth()) {
			g.drawImage(backgroundImage, i, 0, c);
		}
		BorderHelper.drawBorder(g, c, BORDER, BORDER_FACTOR);
		super.paint(g, c);
	}

}
