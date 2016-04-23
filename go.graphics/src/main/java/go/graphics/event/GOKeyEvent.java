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
 * This is a go key event.
 * 
 * @author michael
 *
 */
public class GOKeyEvent extends SingleHandlerGoEvent {
	private final String keyCode;

	/**
	 * Creates a new key event for a given key code.
	 * 
	 * @param keyCode
	 *            The key code.
	 */
	public GOKeyEvent(String keyCode) {
		this.keyCode = keyCode;
	}

	/**
	 * Gets the key code the event has.
	 * 
	 * @return The key code.
	 */
	public String getKeyCode() {
		return keyCode;
	}

	/**
	 * Called when the key is released, finishes the event.
	 * 
	 * @throws IllegalStateException
	 *             if the event was not started yet.
	 */
	public void released() {
		if (getPhase() != PHASE_MODAL) {
			throw new IllegalStateException("Key event in wrong state to be ended.");
		}
		setPhase(PHASE_FINISHED);
	}

	/**
	 * starts the event.
	 * 
	 * @throws IllegalStateException
	 *             if the event was already started.
	 */
	public void started() {
		if (getPhase() != PHASE_INITIALIZING) {
			throw new IllegalStateException("key event may not be started twice.");
		}
		this.setPhase(PHASE_STARTED);
		this.setPhase(PHASE_MODAL);
	}

	public void aborted() {
		if (getPhase() != PHASE_MODAL) {
			throw new IllegalStateException("Key event in wrong state to be aborted.");
		}
		setPhase(PHASE_ABORTED);
	}
}
