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
package jsettlers.main.swing.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.ui.BorderButton;
import jsettlers.main.swing.lookandfeel.ui.ButtonUiStone;
import jsettlers.main.swing.lookandfeel.ui.ButtonUiStoneOriginalBg;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public final class ButtonUiFactory {

	/**
	 * Forward calls
	 */
	public static final ForwardFactory FORWARD = new ForwardFactory();

	/**
	 * Instance of the UI, for all Button the same instance
	 */
	static final ButtonUiStone STONE_UI_SMALL = new ButtonUiStone(BorderButton.BUTTON_UP_4PX, BorderButton.BUTTON_DOWN_4PX);

	/**
	 * Instance of the UI, for all Button the same instance
	 */
	static final ButtonUiStoneOriginalBg MENU_UI = new ButtonUiStoneOriginalBg();

	/**
	 * This is only a factory so no objects need to be created.
	 */
	private ButtonUiFactory() {
	}

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(ELFStyle.KEY);
		if (ELFStyle.BUTTON_STONE == style) {
			return STONE_UI_SMALL;
		}
		if (ELFStyle.BUTTON_MENU == style) {
			return MENU_UI;
		}

		return FORWARD.create(c);
	}
}