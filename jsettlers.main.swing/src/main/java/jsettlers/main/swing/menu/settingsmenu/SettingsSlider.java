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
 * lets the user select a value from {@link SettingsSlider#getMinimum()} to {@link SettingsSlider#getMaximum()}, the value is also displayed as string.<br/>
 * {@link SettingsSlider#minString} is shown if {@link SettingsSlider#getValue()}  == {@link SettingsSlider#getMinimum()} && {@link SettingsSlider#minString} != null
 * 
 * @author Andreas Butti
 */
public class SettingsSlider extends SettlersSlider {
	private static final long serialVersionUID = 1L;

	private String unit;
	private String minString;
	private int minValue;

	public SettingsSlider(String unit, int min_value, int max_value, String minString) {
		setStringPainted(true);
		this.unit = unit;
		this.minString = minString;

		minValue = min_value;
		setMinimum(min_value);
		setMaximum(max_value);
		setValue(50);

		putClientProperty(ELFStyle.KEY, ELFStyle.PROGRESSBAR_SLIDER);
		updateUI();
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
		if(n == minValue && minString != null) {
			setString(minString);
		} else {
			setString(n + unit);
		}
	}
}
