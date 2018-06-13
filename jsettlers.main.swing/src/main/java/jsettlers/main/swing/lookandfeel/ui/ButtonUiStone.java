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
public class ButtonUiStone extends BasicButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("ui_button/ui_button-bg.png");

	/**
	 * Background Image pressed
	 */
	private final BufferedImage backgroundImagePressed = UiImageLoader.get("ui_button_down/ui_button-bg.png");

	/**
	 * Border images if the Button is not pressed
	 */
	private final BufferedImage[] BORDER_NORMAL = { UiImageLoader.get("ui_button/ui_button-corner-upper-left.png"),
			UiImageLoader.get("ui_button/ui_button-border-top.png"),
			UiImageLoader.get("ui_button/ui_button-corner-upper-right.png"),
			UiImageLoader.get("ui_button/ui_button-border-right.png"),
			UiImageLoader.get("ui_button/ui_button-corner-bottom-right.png"),
			UiImageLoader.get("ui_button/ui_button-border-bottom.png"),
			UiImageLoader.get("ui_button/ui_button-corner-bottom_left.png"),
			UiImageLoader.get("ui_button/ui_button-border-left.png")
	};

	/**
	 * Border images if the Button is pressed
	 */
	private final BufferedImage[] BORDER_DOWN = { UiImageLoader.get("ui_button_down/ui_button-corner-upper-left.png"),
			UiImageLoader.get("ui_button_down/ui_button-border-top.png"),
			UiImageLoader.get("ui_button_down/ui_button-corner-upper-right.png"),
			UiImageLoader.get("ui_button_down/ui_button-border-right.png"),
			UiImageLoader.get("ui_button_down/ui_button-corner-bottom-right.png"),
			UiImageLoader.get("ui_button_down/ui_button-border-bottom.png"),
			UiImageLoader.get("ui_button_down/ui_button-corner-bottom_left.png"),
			UiImageLoader.get("ui_button_down/ui_button-border-left.png")
	};

	/**
	 * Scale factor of the border
	 */
	private final float scale;

	/**
	 * Text padding
	 */
	private int textPaddingTopBottom;

	/**
	 * Text padding
	 */
	private int textPaddingLeftRight;

	/**
	 * Constructor
	 * 
	 * @param scale
	 *            Scale factor of the border
	 * @param textPaddingTopBottom
	 *            Text padding
	 * @param textPaddingLeftRight
	 *            Text padding
	 */
	public ButtonUiStone(float scale, int textPaddingTopBottom, int textPaddingLeftRight) {
		this.scale = scale;
		this.textPaddingTopBottom = textPaddingTopBottom;
		this.textPaddingLeftRight = textPaddingLeftRight;
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

		BufferedImage bg;
		BufferedImage[] border;
		float scale = this.scale;

		if (down) {
			border = BORDER_DOWN;
			scale /= 2;
			bg = backgroundImagePressed;
		} else {
			bg = backgroundImage;
			border = BORDER_NORMAL;
		}

		// Draw background
		g.drawImage(bg, 0, 0, c);

		BorderHelper.drawBorder(g1, c, border, scale);

		FontMetrics fm = g.getFontMetrics();
		int y = (b.getHeight() - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent();
		int x = textPaddingLeftRight;

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

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension size = super.getPreferredSize(c);
		size.width += textPaddingLeftRight * 2;
		size.height += textPaddingTopBottom * 2;
		return size;
	}

}