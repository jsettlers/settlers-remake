/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.mapcreator.main.error;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import jsettlers.mapcreator.localization.EditorLabels;

public class ShowErrorsButton extends JButton implements ActionListener, ListDataListener {
	/**
     * 
     */
	private static final long serialVersionUID = -1759142509787969743L;
	private final ErrorList list;
	private final IScrollToAble scrollTo;
	private ErrorsWindow window = null;

	public ShowErrorsButton(ErrorList list, IScrollToAble scrollTo) {
		super(EditorLabels.getLabel("errors"));
		this.list = list;
		this.scrollTo = scrollTo;
		addActionListener(this);
		list.addListDataListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (window == null || window.isClosed()) {
			window = new ErrorsWindow(list, scrollTo);
		} else {
			window.show();
		}
	}

	@Override
	public Color getForeground() {
		if (isEnabled()) {
			return Color.RED;
		} else {
			return super.getForeground();
		}
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		setText(String.format(EditorLabels.getLabel("errors_n"), list.getSize()));
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
	}
}
