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

import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;
import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;
import jsettlers.mapcreator.mapvalidator.result.fix.FixData;

/**
 * Action to fix all errors automatically, if clear what to do
 * 
 * @author Andreas Butti
 */
public class AutoFixErrorAction extends AbstractAction implements ValidationResultListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Error list
	 */
	private ValidationListModel list;

	/**
	 * Fix data helper
	 */
	private FixData fixData;

	/**
	 * Constructor
	 */
	public AutoFixErrorAction() {
		setEnabled(false);
	}

	/**
	 * @param fixData
	 *            Fix data helper
	 */
	public void setFixData(FixData fixData) {
		this.fixData = fixData;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (list == null) {
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			AbstractErrorEntry entry = list.get(i);
			if (entry instanceof ErrorHeader) {
				AbstractFix fix = ((ErrorHeader) entry).getFix();

				if (fix != null) {
					fix.setData(fixData);
					fix.autoFix();
				}
			}
		}
	}

	/**
	 * Update the error text and icon, if an error or not
	 */
	@Override
	public void validationFinished(ValidationListModel list) {
		setEnabled(list.getErrorCount() > 0);
		this.list = list;
	}
}
