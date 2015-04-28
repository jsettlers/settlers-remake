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
package go.graphics.event.mouse;

import go.graphics.UIPoint;

/**
 * This is a proxy that translates the pan event.
 * 
 * @author michael
 * 
 */
public class GOPanEventProxy extends GOEventProxy<GOPanEvent> implements GOPanEvent {

	private final UIPoint displacement;

	/**
	 * Creates a new pan proxy.
	 * 
	 * @param event
	 *            The base event.
	 * @param displaced
	 *            The dsiplace vector.
	 */
	public GOPanEventProxy(GOPanEvent event, UIPoint displaced) {
		super(event);
		this.displacement = displaced;
	}

	@Override
	public UIPoint getPanCenter() {
		UIPoint real = (this.baseEvent).getPanCenter();
		return new UIPoint(real.getX() - this.displacement.getX(), real.getY() - this.displacement.getY());
	}

	@Override
	public UIPoint getPanDistance() {
		UIPoint real = (this.baseEvent).getPanDistance();
		return real;
	}
}
