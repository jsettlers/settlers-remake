/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.mapvalidator.result;

import javax.swing.DefaultListModel;

/**
 * Listmodel with validation errors
 * 
 * @author Andreas Butti
 *
 */
public class ValidationListModel extends DefaultListModel<AbstractErrorEntry> {
	private static final long serialVersionUID = 1L;

	/**
	 * Error count without headers
	 */
	private int errorCount;

	/**
	 * Warning count without header
	 */
	private int warningCount;

	/**
	 * Constructor
	 */
	public ValidationListModel() {
	}

	/**
	 * Remove duplicate header, bring similar errors at the same location togther etc.
	 */
	public void prepareToDisplay() {
		errorCount = 0;
		for (int i = 0; i < getSize(); i++) {
			AbstractErrorEntry element = getElementAt(i);
			if (element instanceof ErrorEntry) {
				ErrorEntry e = (ErrorEntry) element;
				if (e.isError()) {
					errorCount++;
				} else {
					warningCount++;
				}
			}
		}
	}

	/**
	 * @return Warning count without header
	 */
	public int getWarningCount() {
		return warningCount;
	}

	/**
	 * @return Error count without headers
	 */
	public int getErrorCount() {
		return errorCount;
	}
}
