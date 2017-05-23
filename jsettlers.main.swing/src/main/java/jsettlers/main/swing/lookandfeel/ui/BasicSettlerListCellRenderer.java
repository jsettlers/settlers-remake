/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Base class for all settlers list cell renderers (also used for ComboBox)
 * 
 * @author Andreas Butti
 * @param <T>
 *            Type
 */
public abstract class BasicSettlerListCellRenderer<T> extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = 1L;

	private static final Color SELECTED_FOREGROUND_COLOR = Color.BLACK;
	private static final Color SELECTED_BACKGROUND_COLOR = UIDefaults.LABEL_TEXT_COLOR;

	private static final Color UNSELECTED_FOREGROUND_COLOR = UIDefaults.LABEL_TEXT_COLOR;
	private static final Color UNSELECTED_EVEN_INDEX_BACKGROUND_COLOR = Color.BLACK;
	private static final Color UNSELECTED_ODD_INDEX_BACKGROUND_COLOR = new Color(0x222222);

	/**
	 * Constructor
	 */
	public BasicSettlerListCellRenderer() {
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		if (isSelected) {
			setForeground(SELECTED_FOREGROUND_COLOR);
			setBackground(SELECTED_BACKGROUND_COLOR);
		} else {
			setForeground(UNSELECTED_FOREGROUND_COLOR);
			if (index % 2 == 0) {
				setBackground(UNSELECTED_EVEN_INDEX_BACKGROUND_COLOR);
			} else {
				setBackground(UNSELECTED_ODD_INDEX_BACKGROUND_COLOR);
			}
		}
		setValue(list, (T) value, index, isSelected, cellHasFocus);

		return this;
	}

	/**
	 * Set the value to the label
	 * 
	 * @param list
	 *            List
	 * @param value
	 *            Value
	 * @param cellHasFocus
	 *            Focus
	 * @param isSelected
	 *            Selected
	 * @param index
	 *            Index
	 */
	protected abstract void setValue(JList<?> list, T value, int index, boolean isSelected, boolean cellHasFocus);
}
