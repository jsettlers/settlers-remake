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

	private static final String CLIENT_PROPERTY_RANDOM_BACKGROUND_X_OFFSET = "ButtonUiStone.randomBackgroundXOffset";
	private static final String CLIENT_PROPERTY_RANDOM_BACKGROUND_Y_OFFSET = "ButtonUiStone.randomBackgroundYOffset";

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
	public void update(Graphics graphics, JComponent component) {
		int width = component.getWidth();
		int height = component.getHeight();

		Integer randomBackgroundXOffset = (Integer) component.getClientProperty(CLIENT_PROPERTY_RANDOM_BACKGROUND_X_OFFSET);
		Integer randomBackgroundYOffset = (Integer) component.getClientProperty(CLIENT_PROPERTY_RANDOM_BACKGROUND_Y_OFFSET);

		if (randomBackgroundXOffset == null || randomBackgroundYOffset == null) {
			randomBackgroundXOffset = (int) (Math.random() * backgroundImage.getWidth());
			randomBackgroundYOffset = (int) (Math.random() * backgroundImage.getHeight());
			component.putClientProperty(CLIENT_PROPERTY_RANDOM_BACKGROUND_X_OFFSET, randomBackgroundXOffset);
			component.putClientProperty(CLIENT_PROPERTY_RANDOM_BACKGROUND_Y_OFFSET, randomBackgroundYOffset);
		}

		for (int x = -randomBackgroundXOffset; x < component.getWidth(); x += backgroundImage.getWidth()) {
			for (int y = -randomBackgroundYOffset; y < component.getHeight(); y += backgroundImage.getHeight()) {
				graphics.drawImage(backgroundImage, x, y, component);
			}
		}

		boolean down;
		if (component instanceof JToggleButton) {
			down = ((JToggleButton) component).isSelected();
		} else {
			AbstractButton b = (AbstractButton) component;
			ButtonModel model = b.getModel();
			down = model.isArmed() && model.isPressed();
		}

		if (down) {
			component.setBorder(borderDown);
			graphics.setColor(new Color(0, 0, 0, 90));
			graphics.fillRect(2, 2, width - 4, height - 4);
			shiftOffset = 1;
		} else {
			component.setBorder(borderUp);
			shiftOffset = 0;
		}

		super.update(graphics, component);
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
