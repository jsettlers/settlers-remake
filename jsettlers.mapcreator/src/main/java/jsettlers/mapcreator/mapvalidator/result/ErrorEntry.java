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

import jsettlers.common.position.ShortPoint2D;

/**
 * An error / warning entry in the validation list
 * 
 * @author Andreas Butti
 */
public class ErrorEntry extends AbstractErrorEntry {

	/**
	 * Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 */
	private final Object additionalErrorData;

	/**
	 * Position of the error
	 */
	private final ShortPoint2D pos;

	/**
	 * Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	private final String typeId;

	/**
	 * true for error, false for warning
	 */
	private final boolean error;

	/**
	 * Constructor
	 * 
	 * @param additionalErrorData
	 *            Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 * @param text
	 *            Text to display
	 * @param error
	 *            true for error, false for warning
	 * @param pos
	 *            Position of the error
	 * @param typeId
	 *            Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public ErrorEntry(Object additionalErrorData, String text, boolean error, ShortPoint2D pos, String typeId) {
		super(text);
		this.additionalErrorData = additionalErrorData;
		this.error = error;
		this.pos = pos;
		this.typeId = typeId;
	}

	/**
	 * @return Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 */
	public Object getAdditionalErrorData() {
		return additionalErrorData;
	}

	/**
	 * @return true for error, false for warning
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @return Position of the error
	 */
	public ShortPoint2D getPosition() {
		return pos;
	}

}
