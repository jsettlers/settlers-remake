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
package jsettlers.mapcreator.mapvalidator;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.sidebar.Sidebar;
import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;

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
	 * true: show errors, false: show warnings
	 */
	private final boolean error;

	/**
	 * Constructor
	 * 
	 * @param sidebar
	 *            Sidebar to select tab
	 * @param error
	 *            true: show errors, false: show warnings
	 */
	public ShowErrorsAction(Sidebar sidebar, boolean error) {
		this.sidebar = sidebar;
		this.error = error;
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
	public void validationFinished(ValidationListModel list) {
		int count;
		String name;
		if (error) {
			count = list.getErrorCount();
			name = "errors";
		} else {
			count = list.getWarningCount();
			name = "warnings";
		}

		if (count == 0) {
			putValue(Action.NAME, EditorLabels.getLabel("action.show-" + name));
		} else {
			putValue(Action.NAME, String.format(Locale.ENGLISH, EditorLabels.getLabel("action.show-" + name + "_n"), count));
		}
	}
}
