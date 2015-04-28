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
import go.graphics.event.GOModalEventHandler;

/**
 * This method proxys a mouse event handler.
 * <p>
 * It is a normal event handler, that porxys all events that were recived to the given event handler and just changes the vent target.
 * <p>
 * It supports unmodal and modal, events.
 * 
 * @author michael
 */
public class EventHandlerProxy implements GOModalEventHandler {
	private final GOEvent eventToProxy;
	private final GOEventHandler handler;

	/**
	 * Creates a new mouse event proxy.
	 * 
	 * @param eventToProxy
	 *            The event that is forwarded.
	 * @param handler
	 *            The handler to send event changes to.
	 */
	protected EventHandlerProxy(GOEvent eventToProxy,
			GOEventHandler handler) {
		this.eventToProxy = eventToProxy;
		this.handler = handler;
	}

	@Override
	public void aborted(GOEvent event) {
		this.handler.aborted(this.eventToProxy);
	}

	@Override
	public void finished(GOEvent event) {
		this.handler.finished(this.eventToProxy);
	}

	@Override
	public void phaseChanged(GOEvent event) {
		this.handler.phaseChanged(this.eventToProxy);
	}

	@Override
	public void eventDataChanged(GOEvent event) {
		if (this.handler instanceof GOModalEventHandler) {
			((GOModalEventHandler) this.handler).eventDataChanged(this.eventToProxy);
		}
	}
}
