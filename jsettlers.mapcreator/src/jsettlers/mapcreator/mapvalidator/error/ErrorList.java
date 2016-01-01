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
package jsettlers.mapcreator.mapvalidator.error;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import jsettlers.common.position.ILocatable;

/**
 * List model with the errors on the Map
 * 
 * @author Andreas Butti
 */
public class ErrorList extends AbstractListModel<ILocatable> {
	private static final long serialVersionUID = -6645362444519496534L;

	/**
	 * Error locations
	 */
	private ArrayList<Error> errors = new ArrayList<>();

	/**
	 * Constructor
	 */
	public ErrorList() {
	}

	/**
	 * @param errors
	 *            Error locations
	 */
	public void setErrors(ArrayList<Error> errors) {
		int max = Math.max(errors.size(), this.errors.size());
		this.errors = errors;

		fireContentsChanged(this, 0, max);
	}

	@Override
	public ILocatable getElementAt(int arg0) {
		return errors.get(arg0);
	}

	@Override
	public int getSize() {
		return errors.size();
	}

}
