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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.sidebar.Sidebar;

/**
 * Action to display errors, display error count as text
 * 
 * @author Andreas Butti
 */
public class ShowErrorsAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Sidebar to select tab
	 */
	private final Sidebar sidebar;

	/**
	 * Error list
	 */
	private ErrorList list;

	/**
	 * Constructor
	 * 
	 * @param list
	 *            Error list
	 * @param sidebar
	 *            Sidebar to select tab
	 */
	public ShowErrorsAction(ErrorList list, Sidebar sidebar) {
		this.list = list;
		this.sidebar = sidebar;
		putValue(EditorFrame.DISPLAY_TEXT_IN_TOOLBAR, true);
		list.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateText();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				updateText();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				updateText();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		sidebar.selectError();
	}

	/**
	 * Update the error text and icon, if an error or not
	 */
	public void updateText() {
		if (list.getSize() == 0) {
			putValue(Action.NAME, EditorLabels.getLabel("action.show-errors"));
		} else {
			putValue(Action.NAME, String.format(EditorLabels.getLabel("action.show-errors_n"), list.getSize()));
		}
	}
}
