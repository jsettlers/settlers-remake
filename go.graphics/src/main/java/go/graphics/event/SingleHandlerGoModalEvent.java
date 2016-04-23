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
package go.graphics.event;

/**
 * This is a extension to normal events.
 * <p>
 * Modal events are capable of passing on data about the event while the event is on the modal phase.
 * 
 * @author michael
 */
public class SingleHandlerGoModalEvent extends SingleHandlerGoEvent {
	/**
	 * This method notifies the handler, if it supports the {@link GOModalEventHandler}, that the event data has changed.
	 */
	protected void fireModalDataRefreshed() {
		if (getHandler() instanceof GOModalEventHandler) {
			GOModalEventHandler modalHandler = (GOModalEventHandler) getHandler();
			modalHandler.eventDataChanged(this);
		}
	}
}
