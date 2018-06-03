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
package jsettlers.mapcreator.mapvalidator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;

/**
 * Action to display next error, and jumpt to it, disabled if there is no error
 * 
 * @author Andreas Butti
 */
public class GotoNextErrorAction extends AbstractAction implements ValidationResultListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Interface to scroll to position
	 */
	private IScrollToAble scrollTo;

	/**
	 * Next error to select
	 */
	private ErrorEntry nextErrorEntry = null;

	/**
	 * Constructor
	 * 
	 * @param scrollTo
	 *            Interface to scroll to position
	 */
	public GotoNextErrorAction(IScrollToAble scrollTo) {
		this.scrollTo = scrollTo;
		putValue(EditorFrame.DISPLAY_TEXT_IN_TOOLBAR, true);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (nextErrorEntry == null) {
			return;
		}
		scrollTo.scrollTo(nextErrorEntry.getPosition());
	}

	/**
	 * Update the error text and icon, if an error or not
	 */
	@Override
	public void validationFinished(ValidationListModel list) {
		String header = null;
		nextErrorEntry = null;

		for (int i = 0; i < list.size(); i++) {
			AbstractErrorEntry e = list.get(i);
			if (e instanceof ErrorHeader) {
				header = e.getText();
				continue;
			}
			if (e instanceof ErrorEntry) {
				this.nextErrorEntry = (ErrorEntry) e;
				break;
			}
		}

		if (nextErrorEntry != null) {
			String text = header + " - " + nextErrorEntry.getText();

			if (text.length() > 35) {
				text = text.substring(0, 30) + "...";
			}

			putValue(Action.NAME, text);
			setEnabled(true);
		} else {
			putValue(Action.NAME, EditorLabels.getLabel("action.goto-error"));
			setEnabled(false);
		}
	}
}
