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
 * This class defines a generic go event type.
 * <p>
 * When you are notified that an event started, you may handle the event by calling the setHandler-Method of the event. If the handler you gave the
 * event is accepted, event changes are forwarded to you. If your implementation does not allow multiple events at the same time, you should not set a
 * handler to an event if you already handle an ongoing event.
 * <p>
 * if your event handler methods. need to be synchronized, you have to care for it yourself.
 * <p>
 * After its initialization the event, changes of the event state are passed on to the handler: On a phase change, the
 * {@link GOEventHandler#phaseChanged(GOEvent)} method is called. During the Modal phase, the event may send additional information about the possible
 * event data it may have upon finish, e.g. a mouse position change to an {@link GOModalEventHandler}.
 * <p>
 * Any final action should only be applied if the event was finished.
 * 
 * @author michael
 */
public interface GOEvent {
	/**
	 * Indicates that the event is in the initialization phase. In this pahse, the event handler is searched.
	 */
	int PHASE_INITIALIZING = 0;
	/**
	 * Indicates that the event just started.
	 */
	int PHASE_STARTED = 1;
	/**
	 * Indicates that the event is in progress.
	 */
	int PHASE_MODAL = 2;
	/**
	 * The event was finished.
	 */
	int PHASE_FINISHED = 3;

	/**
	 * The event was aborted by the user.
	 */
	int PHASE_ABORTED = 4;

	/**
	 * Sets the handler for the event.
	 * 
	 * @param handler
	 *            The handler
	 */
	void setHandler(GOEventHandler handler);

	/**
	 * Gets the current handler the vent has. May be null.
	 * 
	 * @return Th handler.
	 */
	GOEventHandler getHandler();

	/**
	 * Gets the phase of the event.
	 * 
	 * @return The phase the event is in.
	 */
	int getPhase();
}
