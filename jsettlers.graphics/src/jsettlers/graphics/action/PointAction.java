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
package jsettlers.graphics.action;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This action states that the user wants something to move to the given position.
 *
 * @author Michael Zangl
 */
public class PointAction extends Action {

	private final ShortPoint2D position;

	/**
	 * Creates a new moveto aciton.
	 *
	 * @param type
	 *            The type of this action.
	 * @param position
	 *            The position the user clicked at.
	 */
	public PointAction(EActionType type, ShortPoint2D position) {
		super(type);
		this.position = position;
	}

	/**
	 * Gets the position on the map the user wants to move the unit(s) to.
	 *
	 * @return The position.
	 */
	public ShortPoint2D getPosition() {
		return this.position;
	}
}
