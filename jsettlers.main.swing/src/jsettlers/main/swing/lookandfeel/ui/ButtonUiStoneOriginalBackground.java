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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.utils.ImageUtils;
import jsettlers.main.swing.lookandfeel.DrawHelper;

/**
 * Button UI Implementation
 *
 * @author Andreas Butti
 */
public class ButtonUiStoneOriginalBackground extends BasicButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage;

	/**
	 * Background Image for pressed button
	 */
	private final BufferedImage backgroundPressedImage;

	/**
	 * Constructor
	 */
	public ButtonUiStoneOriginalBackground() {
		ImageProvider imageProvider = ImageProvider.getInstance();
		backgroundImage = ImageUtils.convertToBufferedImage(imageProvider.getGuiImage(3, 326));
		backgroundPressedImage = ImageUtils.convertToBufferedImage(imageProvider.getGuiImage(3, 329));
	}

	@Override
	public void installDefaults(AbstractButton button) {
		button.setFont(UIDefaults.FONT);
		button.setForeground(UIDefaults.LABEL_TEXT_COLOR);
	}

	@Override
	public void uninstallDefaults(AbstractButton b) {
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		Graphics2D g = DrawHelper.enableAntialiasing(g1);

		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		boolean down;
		if (c instanceof JToggleButton) {
			down = ((JToggleButton) c).isSelected();
		} else {
			down = model.isArmed() && model.isPressed();
		}

		if (down) {
			g.drawImage(backgroundPressedImage, 0, 0, b.getWidth(), b.getHeight(), c);
		} else {
			g.drawImage(backgroundImage, 0, 0, b.getWidth(), b.getHeight(), c);
		}

		int y = b.getHeight() / 2 + (g.getFontMetrics().getAscent() / 2);
		int x = 10;

		if (down) {
			x += 1;
			y += 1;
		}

		g.setFont(c.getFont());

		// draw shadow
		g.setColor(Color.BLACK);
		g.drawString(b.getText(), x + 1, y + 1);
		g.setColor(c.getForeground());
		g.drawString(b.getText(), x, y);
	}

}