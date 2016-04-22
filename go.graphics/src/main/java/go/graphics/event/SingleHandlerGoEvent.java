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
 * This class provides simple handling of events. It allows the addition of a event handler and basic phase support with event fireing.
 * 
 * @author michael
 */
public class SingleHandlerGoEvent implements GOEvent {
	private GOEventHandler handler = null;

	private int phase = PHASE_INITIALIZING;

	@Override
	public void setHandler(GOEventHandler handler) {
		if (getPhase() != PHASE_INITIALIZING) {
			throw new IllegalStateException(
					"Can only set event handler in initialization pahse.");
		}
		this.handler = handler;
	}

	@Override
	public GOEventHandler getHandler() {
		return handler;
	}

	/**
	 * Sets the phase of the current event. This should not be used by the event handler.
	 * <p>
	 * Fires a event phase chnage.
	 * 
	 * @param phase
	 *            The phase.
	 */
	protected void setPhase(int phase) {
		if (phase < this.phase) {
			throw new IllegalStateException("Cannot go backwards in states");
		}
		this.phase = phase;

		if (handler != null) {
			handler.phaseChanged(this);

			if (phase == PHASE_ABORTED) {
				handler.aborted(this);
			} else if (phase == PHASE_FINISHED) {
				handler.finished(this);
			}
		}
	}

	@Override
	public int getPhase() {
		return phase;
	}
}
