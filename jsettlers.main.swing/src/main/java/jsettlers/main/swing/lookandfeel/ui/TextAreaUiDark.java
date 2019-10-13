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

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

/**
 * Text field UI for dark backgrounds
 * 
 * @author Andreas Butti
 */
public class TextAreaUiDark extends BasicTextAreaUI {

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		TextComponentHelper.installUi((JTextComponent) c);
		c.setFont(UIDefaults.FONT_SMALL);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		TextComponentHelper.uninstallUI(c);
	}

	@Override
	protected void paintSafely(Graphics g) {
		// this hack is needed to paint a half transparency background
		paintBackground(g);
		super.paintSafely(g);
	}

	@Override
	protected void paintBackground(Graphics g) {
		TextComponentHelper.paintBackground(g, getComponent());
	}
}