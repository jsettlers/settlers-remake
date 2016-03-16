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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;
import sun.swing.SwingUtilities2;

/**
 * Stone toggle Button UI
 * 
 * @author Andreas Butti
 */
public class ButtonUiStone extends BasicToggleButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("granit_texture1.png");

	/**
	 * Button down
	 */
	private final Border borderDown;

	/**
	 * Button up
	 */
	private final Border borderUp;

	/**
	 * Offset
	 */
	private int shiftOffset = 0;

	/**
	 * Constructor
	 * 
	 * @param borderUp
	 *            Button up
	 * @param borderDown
	 *            Button down
	 */
	public ButtonUiStone(Border borderUp, Border borderDown) {
		this.borderUp = borderUp;
		this.borderDown = borderDown;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
		c.setBorder(borderUp);
		c.setFont(UIDefaults.FONT_PLAIN);
		c.setForeground(UIDefaults.LABEL_TEXT_COLOR);
	}

	@Override
	public void update(Graphics g, JComponent c) {
		int w = c.getWidth();
		int h = c.getHeight();

		Integer sx0 = (Integer) c.getClientProperty("ButtonUiStone.bgx");
		Integer sy0 = (Integer) c.getClientProperty("ButtonUiStone.bgy");

		if (sx0 == null || sy0 == null) {
			sx0 = (int) Math.random() * backgroundImage.getWidth();
			sy0 = (int) Math.random() * backgroundImage.getHeight();
			c.putClientProperty("ButtonUiStone.bgx", sx0);
			c.putClientProperty("ButtonUiStone.bgy", sy0);
		}

		int sx1 = sx0;
		int sy1 = sy0;

		for (int x = sx1; x < c.getWidth(); x += backgroundImage.getWidth()) {
			for (int y = sy1; y < c.getWidth(); y += backgroundImage.getHeight()) {
				g.drawImage(backgroundImage, x, y, c);
			}
		}

		boolean down;
		if (c instanceof JToggleButton) {
			down = ((JToggleButton) c).isSelected();
		} else {
			down = false;
			AbstractButton b = (AbstractButton) c;
			ButtonModel model = b.getModel();
			down = model.isArmed() && model.isPressed();
		}

		if (down) {
			c.setBorder(borderDown);
			g.setColor(new Color(0, 0, 0, 90));
			g.fillRect(2, 2, w - 4, h - 4);
			shiftOffset = 1;
		} else {
			c.setBorder(borderUp);
			shiftOffset = 0;
		}

		super.update(g, c);
	}

	@Override
	protected int getTextShiftOffset() {
		return shiftOffset;
	}

	@Override
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);
		int mnemonicIndex = b.getDisplayedMnemonicIndex();

		g.setColor(Color.BLACK);
		SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
				textRect.x + getTextShiftOffset() + 1,
				textRect.y + fm.getAscent() + getTextShiftOffset() + 1);

		g.setColor(b.getForeground());
		SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
				textRect.x + getTextShiftOffset(),
				textRect.y + fm.getAscent() + getTextShiftOffset());
	}

	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
	}
}
