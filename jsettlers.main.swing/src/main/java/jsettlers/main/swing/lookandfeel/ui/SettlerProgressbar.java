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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * ProgressBar ui
 * 
 * @author Andreas Butti
 *
 */
public class SettlerProgressbar extends BasicProgressBarUI {

	/**
	 * Settler blue
	 */
	private static final Color FOREGROUND_COLOR = new Color(0x00C7C6);

	/**
	 * Constructor
	 */
	public SettlerProgressbar() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		c.setOpaque(false);
		c.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		c.setFont(UIDefaults.FONT_SMALL);

		JProgressBar pg = (JProgressBar) c;
		pg.setForeground(FOREGROUND_COLOR);
	}

	@Override
	protected Color getSelectionForeground() {
		return Color.WHITE;
	}

	@Override
	protected Color getSelectionBackground() {
		return Color.WHITE;
	}
}
