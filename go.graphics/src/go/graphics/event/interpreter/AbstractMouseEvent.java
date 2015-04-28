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
import go.graphics.event.SingleHandlerGoModalEvent;

public class AbstractMouseEvent extends SingleHandlerGoModalEvent {

	protected UIPoint position;
	private int mouseMoved = 0;
	private long startTime = 0;

	public AbstractMouseEvent() {
		super();
		startTime = System.currentTimeMillis();
	}

	/**
	 * Stats that the event was initialized and should change to its modal pahse.
	 */
	public void initialized() {
		setPhase(PHASE_STARTED);
		setPhase(PHASE_MODAL);
	}

	/**
	 * Ends the event.
	 */
	public void released() {
		setPhase(PHASE_FINISHED);
	}

	/**
	 * The event was aborted.
	 */
	public void aborted() {
		setPhase(PHASE_ABORTED);
	}

	/**
	 * Computes whether the mouse was moved (more than just a few pixels) during the event.
	 * 
	 * @return true if the mouse was moved while beeing pressed.
	 */
	public int getMouseMoved() {
		return mouseMoved;
	}

	public float getTime() {
		return (System.currentTimeMillis() - startTime) / 1000f;
	}

	/**
	 * Sets the mouse position as a given point.
	 * 
	 * @param current
	 *            The position.
	 */
	protected void setMousePosition(final UIPoint current) {
		if (this.position != null) {
			mouseMoved +=
					Math.abs(current.getX() - this.position.getX())
							+ Math.abs(current.getY() - this.position.getY());
		}

		this.position = current;
		if (getPhase() == PHASE_MODAL) {
			fireModalDataRefreshed();
		}
	}
}