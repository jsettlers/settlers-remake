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
package jsettlers.main.swing.menu.settingsmenu;

import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.menu.general.SettlersSlider;

/**
 * Slider to select volume in settings
 * <p />
 * This slider is technically based on a progress bar, but looks and works like the production sliders in the original game. (blue bars) The slider
 * lets the user select a value from 0 to 100%, the value is also displayed as string
 * 
 * @author Andreas Butti
 */
public class VolumeSlider extends SettlersSlider {
	private static final long serialVersionUID = 1L;

	public VolumeSlider() {
		setStringPainted(true);

		setMinimum(0);
		setMaximum(100);
		setValue(50);

		putClientProperty(ELFStyle.KEY, ELFStyle.PROGRESSBAR_SLIDER);
		updateUI();
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
		setString(n + "%");
	}
}
