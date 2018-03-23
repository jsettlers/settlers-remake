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
package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOPanEvent;

/**
 * This is a pan event that is converted from a normal swing mouse event.
 * 
 * @author michael
 */
class ConvertedPanEvent extends AbstractMouseEvent implements GOPanEvent {

	private final UIPoint original;

	/**
	 * Creates a new converted event.
	 * 
	 * @param point
	 *            THe point the user first put his mouse to.
	 * @param modifiers The modifiers
	 */
	protected ConvertedPanEvent(UIPoint point, int modifiers) {
		super(modifiers);
		this.position = point;
		this.original = point;
	}

	@Override
	public UIPoint getPanDistance() {
		return new UIPoint(position.getX() - original.getX(), position.getY() - original.getY());
	}

	@Override
	public UIPoint getPanCenter() {
		return original;
	}
}
