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

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jsettlers.common.position.ILocatable;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * This is a window that contains a list of errors.
 * 
 * @author michaelz
 * 
 */
public class ErrorsWindow implements ListSelectionListener {

	private final JFrame window;
	private final JList<ILocatable> elist;
	private final ErrorList list;
	private final IScrollToAble scrollTo;

	public ErrorsWindow(ErrorList list, IScrollToAble scrollTo) {
		this.list = list;
		this.scrollTo = scrollTo;
		elist = new JList<>(list);
		elist.addListSelectionListener(this);

		window = new JFrame(EditorLabels.getLabel("errors"));
		window.add(new JScrollPane(elist));
		window.setPreferredSize(new Dimension(500, 300));
		window.pack();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public boolean isClosed() {
		return !window.isVisible();
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		int index = elist.getSelectedIndex();
		if (index > 0) {
			scrollTo.scrollTo(list.getElementAt(index).getPos());
		}
	}

	public void show() {
		window.toFront();
	}

}
