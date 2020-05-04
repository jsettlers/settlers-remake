/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * Settler Combobox UI
 * 
 * @author Andreas Butti
 */
public class SettlersComboboxUi extends BasicComboBoxUI {

	/**
	 * Renderer of list cells
	 */
	private static class SettlersListCellRenderer extends BasicSettlerListCellRenderer<Object> {
		private static final long serialVersionUID = 1L;

		@Override
		protected void setValue(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			setText(String.valueOf(value));
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);

		((JComboBox<?>) c).setRenderer(new SettlersListCellRenderer());
		c.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		c.setFont(UIDefaults.FONT_SMALL);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.setOpaque(true);
		c.setBorder(null);
	}

	@Override
	protected JButton createArrowButton() {
		JButton button = new ScrollbarUiButton(BasicArrowButton.SOUTH, UIDefaults.ARROW_COLOR);
		button.setName("ComboBox.arrowButton");
		return button;
	}
}