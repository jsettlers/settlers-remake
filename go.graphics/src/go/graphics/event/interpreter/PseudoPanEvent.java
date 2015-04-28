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
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.SingleHandlerGoModalEvent;
import go.graphics.event.mouse.GOPanEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a pan event that fire as long as the base event is active every 100ms to scroll the given Distance.
 * 
 * @author michael
 */
public class PseudoPanEvent extends SingleHandlerGoModalEvent implements
		GOPanEvent, GOEventHandler {

	private static final long PAN_DELAY = 50;
	private static Timer timer = null;
	private UIPoint distance;
	private final int dx;
	private final int dy;
	private TimerTask timertask;

	public PseudoPanEvent(GOEvent event, int x, int y) {
		event.setHandler(this);
		this.distance = new UIPoint(x, y);
		this.dx = x;
		this.dy = y;
	}

	@Override
	public synchronized UIPoint getPanCenter() {
		return null;
	}

	@Override
	public synchronized UIPoint getPanDistance() {
		return distance;
	}

	public synchronized void start() {
		this.setPhase(PHASE_STARTED);
		this.setPhase(PHASE_MODAL);
		fireModalDataRefreshed();
	}

	@Override
	public synchronized void phaseChanged(GOEvent event) {
		if (event.getPhase() == GOEvent.PHASE_STARTED) {
			if (timer == null) {
				timer = new Timer();
			}
			timertask = new TimerTask() {
				@Override
				public void run() {
					doPanStep();
				}
			};
			timer.schedule(timertask, PAN_DELAY, PAN_DELAY);
			start();
		}
	}

	private synchronized void doPanStep() {
		if (this.getPhase() == PHASE_MODAL) {
			this.distance =
					new UIPoint(this.distance.getX() + dx, this.distance.getY()
							+ dy);
			fireModalDataRefreshed();
		}
	}

	@Override
	public synchronized void finished(GOEvent event) {
		timertask.cancel();
		this.setPhase(PHASE_FINISHED);
	}

	@Override
	public synchronized void aborted(GOEvent event) {
		timertask.cancel();
		this.setPhase(PHASE_ABORTED);
	}
}
