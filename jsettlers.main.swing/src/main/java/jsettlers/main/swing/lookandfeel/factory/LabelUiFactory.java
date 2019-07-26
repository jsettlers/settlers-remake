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
import jsettlers.main.swing.lookandfeel.ui.StoneBackgroundLabel;
import jsettlers.main.swing.lookandfeel.ui.UIDefaults;

/**
 * Label UI factory
 * 
 * @author Andreas Butti
 */
public final class LabelUiFactory {

	/**
	 * Forward calls
	 */
	public static final ForwardFactory FORWARD = new ForwardFactory();

	/**
	 * Header Label
	 */
	private static final StoneBackgroundLabel headerLabel = new StoneBackgroundLabel(UIDefaults.HEADER_TEXT_COLOR);

	/**
	 * Label short
	 */
	private static final StoneBackgroundLabel labelShort = new StoneBackgroundLabel(UIDefaults.LABEL_TEXT_COLOR);

	/**
	 * Label long
	 */
	private static final StoneBackgroundLabel labelLong = new StoneBackgroundLabel(UIDefaults.LABEL_TEXT_COLOR);

	/**
	 * Label long
	 */
	private static final StoneBackgroundLabel labelDynamic = new StoneBackgroundLabel(UIDefaults.LABEL_TEXT_COLOR);

	/**
	 * This is only a factory so no objects need to be created.
	 */
	private LabelUiFactory() {
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
		if (ELFStyle.LABEL_HEADER == style) {
			return headerLabel;
		}
		if (ELFStyle.LABEL_LONG == style) {
			return labelLong;
		}
		if (ELFStyle.LABEL_SHORT == style) {
			return labelShort;
		}
		if (ELFStyle.LABEL_DYNAMIC == style) {
			return labelDynamic;
		}

		return FORWARD.create(c);
	}
}