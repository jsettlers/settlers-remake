/*
 * Copyright (c) 2018
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
 */
package jsettlers.common.action;

import jsettlers.common.movable.EMovableType;

/**
 * This action is used to convert any movables to the given {@link EMovableType}.
 * 
 * @author Andreas Eberle
 */
public class ConvertAction extends Action {

	private final EMovableType toType;
	private final short amount;

	/**
	 * This action is used to convert any movables to the given type.
	 * 
	 * @param toType
	 *            target type to convert the movables to
	 * @param amount
	 *            number of movables that should be converted. <br>
	 *            if amount == {@link Short}.MAX_VALUE all selected movables will be converted.
	 */
	public ConvertAction(EMovableType toType, short amount) {
		super(EActionType.CONVERT);
		this.toType = toType;
		this.amount = amount;
	}

	/**
	 * @return {@link EMovableType} the movables should become.
	 */
	public EMovableType getTargetType() {
		return toType;
	}

	/**
	 * @return number of movables to convert. If value == {@link Short} .MAX_VALUE all movables should be converted.
	 */
	public short getAmount() {
		return amount;
	}

}
