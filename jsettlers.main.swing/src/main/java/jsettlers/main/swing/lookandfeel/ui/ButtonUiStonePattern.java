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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import jsettlers.main.swing.lookandfeel.DrawHelper;
import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;

/**
 * Button UI Implementation
 *
 * @author Andreas Butti
 */
public class ButtonUiStonePattern extends BasicButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("sr_ui_button/sr_ui_button-bg.png");

	/**
	 * Border images if the Button is not pressed
	 */
	private final BufferedImage[] BORDER_NORMAL = { UiImageLoader.get("sr_ui_button/sr_ui_button-corner-upper-left.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-border-top.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-corner-upper-right.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-border-right.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-corner-bottom-right.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-border-bottom.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-corner-bottom_left.png"),
			UiImageLoader.get("sr_ui_button/sr_ui_button-border-left.png")
	};

	/**
	 * Border images if the Button is pressed
	 */
	private final BufferedImage[] BORDER_DOWN = BORDER_NORMAL;

	/**
	 * Constructor
	 */
	public ButtonUiStonePattern() {
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

		// Draw background
		g.drawImage(backgroundImage, 0, 0, c);

		BufferedImage[] border;
		if (down) {
			border = BORDER_DOWN;
		} else {
			border = BORDER_NORMAL;
		}

		drawBorder(g1, c, border);

		FontMetrics fm = g.getFontMetrics();
		int y = (b.getHeight() - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent();
		int x = 20;

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

	/**
	 * Draw the border
	 * 
	 * @param g
	 *            Graphics
	 * @param c
	 *            Component
	 * @param border
	 *            Border
	 */
	private void drawBorder(Graphics g, JComponent c, BufferedImage[] border) {
		final int width = c.getWidth();
		final int height = c.getHeight();

		float scale = 0.5f;

		// top-left
		drawImage(g, c, scale, border[0], 0, 0);

		// top
		drawScaled(g, c, border[1], (int) (border[0].getWidth() * scale), (int) (width - scale * border[2].getWidth()), 0, (int) (scale * border[1].getHeight()));

		// top-right
		drawImage(g, c, scale, border[2], (int) (width - border[2].getWidth() * scale), 0);

		// right
		drawScaled(g, c, border[3], (int) (width - border[3].getWidth() * scale), width, (int) (border[2].getHeight() * scale), (int) (height - border[4].getHeight() * scale));

		// bottom-right
		drawImage(g, c, scale, border[4], (int) (width - border[4].getWidth() * scale), (int) (height - border[4].getWidth() * scale));

		// bottom
		drawScaled(g, c, border[5], (int) (border[0].getWidth() * scale), (int) (width - border[2].getWidth() * scale), (int) (height - border[5].getHeight() * scale), height);

		// bottom-left
		drawImage(g, c, scale, border[6], 0, (int) (height - border[6].getWidth() * scale));

		// left
		drawScaled(g, c, border[7], 0, (int) (border[7].getWidth() * scale), (int) (border[0].getHeight() * scale), (int) (height - border[6].getHeight() * scale));
	}

	private void drawImage(Graphics g, JComponent c, float scale, BufferedImage img, int x, int y) {
		g.drawImage(img, x, y, (int) (x + img.getWidth() * scale), (int) (y + img.getHeight() * scale), 0, 0, img.getWidth(), img.getHeight(), c);
	}

	private void drawScaled(Graphics g, JComponent c, BufferedImage img, int x, int x2, int y, int y2) {
		g.drawImage(img, x, y, x2, y2, 0, 0, img.getWidth(), img.getHeight(), c);
	}

}