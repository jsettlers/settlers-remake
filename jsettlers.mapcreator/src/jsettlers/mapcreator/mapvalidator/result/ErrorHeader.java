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

import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;

/**
 * Header entry
 * 
 * @author Andreas Butti
 */
public class ErrorHeader extends AbstractErrorEntry {

	/**
	 * Fix, if any
	 */
	private final AbstractFix fix;

	/**
	 * true: This header contains at least one error, false: only warning
	 */
	private boolean error = true;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Text to display
	 * @param fix
	 *            Fix, if any
	 */
	public ErrorHeader(String text, AbstractFix fix) {
		super(text);
		this.fix = fix;
	}

	/**
	 * @param error
	 *            true: This header contains at least one error, false: only warning
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @return true: This header contains at least one error, false: only warning
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return Fix, if any
	 */
	public AbstractFix getFix() {
		return fix;
	}

}
