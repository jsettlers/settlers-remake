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

import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;

/**
 * This is the basic event proxy.
 * 
 * @author michael
 *
 * @param <T>
 *            The event type to proxy
 */
public class GOEventProxy<T extends GOEvent> implements GOEvent {

	protected final T baseEvent;

	public GOEventProxy(T baseEvent) {
		this.baseEvent = baseEvent;
	}

	public GOEventHandler getHandler() {
		return this.baseEvent.getHandler();
	}

	public int getPhase() {
		return this.baseEvent.getPhase();
	}

	public void setHandler(GOEventHandler handler) {
		this.baseEvent.setHandler(new EventHandlerProxy(this, handler));
	}

}