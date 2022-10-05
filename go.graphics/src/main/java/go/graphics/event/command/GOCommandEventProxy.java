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
package go.graphics.event.command;

import java.util.Set;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOEventProxy;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GOCommandEventProxy extends GOEventProxy<GOCommandEvent> implements
		GOCommandEvent {

	private final UIPoint displacement;

	/**
	 * @param baseEvent
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GOCommandEventProxy(GOCommandEvent baseEvent, UIPoint displacement) {
		super(baseEvent);
		this.displacement = displacement;
	}

	@Override
	public UIPoint getCommandPosition() {
		UIPoint real = baseEvent.getCommandPosition();
		return new UIPoint(real.getX() - displacement.getX(), real.getY() - displacement.getY());
	}

	@Override
	public boolean isSelecting() {
		return baseEvent.isSelecting();
	}

	@Override
	public Set<EModifier> getModifiers() {
		return baseEvent.getModifiers();
	}
}
