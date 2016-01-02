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
package jsettlers.mapcreator.mapvalidator.tasks;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.sidebar.Sidebar;
import jsettlers.mapcreator.mapvalidator.ValidationResultListener;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;

/**
 * Action to display errors, display error count as text (always enabled, always clickable)
 * 
 * @author Andreas Butti
 */
public class ShowErrorsAction extends AbstractAction implements ValidationResultListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Sidebar to select tab
	 */
	private final Sidebar sidebar;

	/**
	 * Constructor
	 * 
	 * @param list
	 *            Error list
	 * @param sidebar
	 *            Sidebar to select tab
	 */
	public ShowErrorsAction(Sidebar sidebar) {
		this.sidebar = sidebar;
		putValue(EditorFrame.DISPLAY_TEXT_IN_TOOLBAR, true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		sidebar.selectError();
	}

	/**
	 * Update the error text and icon, if an error or not
	 */
	@Override
	public void validationFinished(ValidationList list) {
		if (list.getErrorCount() == 0) {
			putValue(Action.NAME, EditorLabels.getLabel("action.show-errors"));
		} else {
			putValue(Action.NAME, String.format(EditorLabels.getLabel("action.show-errors_n"), list.getErrorCount()));
		}
	}
}
