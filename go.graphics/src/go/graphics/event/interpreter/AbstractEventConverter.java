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
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.SingleHandlerGoEvent;
import go.graphics.event.command.GOCommandEvent;

import java.util.LinkedList;

/**
 * This class interprets events. It provides helper functions for e.g. Swing converter to send the events.
 * 
 * @author michael
 */
public class AbstractEventConverter {
	private final GOEventHandlerProvider provider;

	private GOKeyEvent ongoingKeyEvent = null;

	private ConvertedDrawEvent ongoingDrawEvent = null;

	private ConvertedPanEvent ongoingPanEvent = null;

	private ConvertedHoverEvent ongoingHoverEvent;

	private final int PAN_PER_KEYPRESS = 20;

	private final LinkedList<EventReplacementRule> replace =
			new LinkedList<AbstractEventConverter.EventReplacementRule>();

	private ConvertedZoomEvent ongoingZoomEvent;

	protected AbstractEventConverter(GOEventHandlerProvider provider) {
		this.provider = provider;
	}

	/**
	 * trys to cancel the first found ongoing event.
	 */
	protected void tryCancelCurrentEvent() {
		if (ongoingDrawEvent != null) {
			ongoingDrawEvent.aborted();
			ongoingDrawEvent = null;
		} else if (ongoingPanEvent != null) {
			ongoingPanEvent.aborted();
			ongoingPanEvent = null;
		}
	}

	/**
	 * Starts a draw event, if none is started yet.
	 * 
	 * @param start
	 */
	protected void startDraw(UIPoint start) {
		if (ongoingDrawEvent == null) {
			ongoingDrawEvent = new ConvertedDrawEvent(start);

			handleEvent(ongoingDrawEvent);

			ongoingDrawEvent.initialized();
		}
	}

	protected void updateDrawPosition(UIPoint current) {

		if (ongoingDrawEvent != null) {
			ongoingDrawEvent.setMousePosition(current);
		}
	}

	protected void endDraw(UIPoint position) {
		if (ongoingDrawEvent != null) {
			if (tryReplaceEvent(position, ReplacableEvent.DRAW,
					ongoingDrawEvent.getTime(),
					ongoingDrawEvent.getMouseMoved())) {
				abortDraw();
			} else {
				ongoingDrawEvent.released();
				ongoingDrawEvent = null;
			}
		}
	}

	protected void abortDraw() {
		if (ongoingDrawEvent != null) {
			ongoingDrawEvent.aborted();
			ongoingDrawEvent = null;
		}
	}

	protected void startPan(UIPoint start) {
		if (ongoingPanEvent == null) {
			ongoingPanEvent = new ConvertedPanEvent(start);
			handleEvent(ongoingPanEvent);
			ongoingPanEvent.initialized();
		}
	}

	protected void updatePanPosition(UIPoint current) {
		if (ongoingPanEvent != null) {
			ongoingPanEvent.setMousePosition(current);
		}
	}

	protected void endPan(UIPoint position) {
		if (ongoingPanEvent != null) {
			if (tryReplaceEvent(position, ReplacableEvent.PAN,
					ongoingPanEvent.getTime(), ongoingPanEvent.getMouseMoved())) {
				abortPan();
			} else {
				ongoingPanEvent.released();
				ongoingPanEvent = null;
			}
		}
	}

	protected void startZoom() {
		if (ongoingZoomEvent == null) {
			ongoingZoomEvent = new ConvertedZoomEvent();
			handleEvent(ongoingZoomEvent);
			ongoingZoomEvent.initialized();
		}
	}

	protected void updateZoomFactor(float factor) {
		if (ongoingZoomEvent != null) {
			ongoingZoomEvent.setZoomFactor(factor);
		}
	}

	protected void endZoomEvent(float factor) {
		if (ongoingZoomEvent != null) {
			ongoingZoomEvent.setZoomFactor(factor);
			ongoingZoomEvent.released();
			ongoingZoomEvent = null;
		}
	}

	protected void abortPan() {
		if (ongoingPanEvent != null) {
			ongoingPanEvent.aborted();
			ongoingPanEvent = null;
		}
	}

	protected void startHover(UIPoint start) {
		if (ongoingHoverEvent == null) {
			ongoingHoverEvent = new ConvertedHoverEvent(start);
			handleEvent(ongoingHoverEvent);
			ongoingHoverEvent.initialized();
		}
	}

	protected void updateHoverPosition(UIPoint current) {
		if (ongoingHoverEvent != null) {
			ongoingHoverEvent.setMousePosition(current);
		}
	}

	protected void endHover(UIPoint position) {
		if (ongoingHoverEvent != null) {
			if (tryReplaceEvent(position, ReplacableEvent.HOVER,
					ongoingHoverEvent.getTime(),
					ongoingHoverEvent.getMouseMoved())) {
				abortHover();
			} else {
				ongoingHoverEvent.released();
				ongoingHoverEvent = null;
			}
		}
	}

	protected void abortHover() {
		if (ongoingHoverEvent != null) {
			ongoingHoverEvent.aborted();
			ongoingHoverEvent = null;
		}
	}

	protected boolean fireCommandEvent(UIPoint point, boolean isSelect) {
		ConvertedCommandEvent commandEvent =
				new ConvertedCommandEvent(point, isSelect);

		handleEvent(commandEvent);

		commandEvent.initialized();

		return commandEvent.getHandler() != null;
	}

	protected synchronized void startKeyEvent(String string) {
		if (ongoingKeyEvent == null) {
			ongoingKeyEvent = new GOKeyEvent(string);
			replaceKeyEvent(ongoingKeyEvent);
			handleEvent(ongoingKeyEvent);
			ongoingKeyEvent.started();
		}
	}

	protected synchronized void endKeyEvent(String string) {
		if (ongoingKeyEvent != null) {
			ongoingKeyEvent.released();
			ongoingKeyEvent = null;
		}
	}

	protected synchronized boolean replaceKeyEvent(GOKeyEvent event) {
		if ("DOWN".equalsIgnoreCase(event.getKeyCode())) {
			doPan(event, 0, PAN_PER_KEYPRESS);
			return true;
		} else if ("UP".equalsIgnoreCase(event.getKeyCode())) {
			doPan(event, 0, -PAN_PER_KEYPRESS);
			return true;
		} else if ("LEFT".equalsIgnoreCase(event.getKeyCode())) {
			doPan(event, PAN_PER_KEYPRESS, 0);
			return true;
		} else if ("RIGHT".equalsIgnoreCase(event.getKeyCode())) {
			doPan(event, -PAN_PER_KEYPRESS, 0);
			return true;
		} else {
			return false;
		}
	}

	private void doPan(GOKeyEvent event, int x, int y) {
		PseudoPanEvent pan = new PseudoPanEvent(event, x, y);
		this.handleEvent(pan);
	}

	/**
	 * Lets the provider handle a event.
	 */
	protected void handleEvent(GOEvent e) {
		provider.handleEvent(e);

	}

	private class ConvertedCommandEvent extends SingleHandlerGoEvent implements
			GOCommandEvent {

		private final UIPoint position;
		private final boolean selecting;

		public ConvertedCommandEvent(UIPoint position, boolean selecting) {
			this.position = position;
			this.selecting = selecting;
		}

		public void initialized() {
			setPhase(PHASE_STARTED);
			setPhase(PHASE_MODAL);
			setPhase(PHASE_FINISHED);
		}

		@Override
		public UIPoint getCommandPosition() {
			return position;
		}

		@Override
		public boolean isSelecting() {
			return selecting;
		}

	}

	protected void addReplaceRule(EventReplacementRule r) {
		replace.add(r);
	}

	private boolean tryReplaceEvent(UIPoint p, ReplacableEvent e, double time,
			double distance) {
		for (EventReplacementRule r : replace) {
			if (r.matches(e, time, distance)) {
				boolean success =
						fireCommandEvent(p,
								r.replaced == Replacement.COMMAND_SELECT);
				if (success) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean drawStarted() {
		return ongoingDrawEvent != null;
	}

	protected boolean panStarted() {
		return ongoingPanEvent != null;
	}

	/**
	 * a rule on when to replace short draw, hover or pan events with other stuff.
	 * 
	 * @author michael
	 */
	public class EventReplacementRule {

		private final ReplacableEvent search;
		private final Replacement replaced;
		private final double maxTime;
		private final double maxDistance;

		/**
		 * @param search
		 * @param replaced
		 * @param maxTime
		 *            Maximum time the event took. -1: ignore
		 * @param maxDistance
		 *            Maximum distance the cursor moved. -1: ignore
		 */
		public EventReplacementRule(ReplacableEvent search,
				Replacement replaced, double maxTime, double maxDistance) {
			this.search = search;
			this.replaced = replaced;
			this.maxTime = maxTime;
			this.maxDistance = maxDistance;
		}

		private boolean matches(ReplacableEvent e, double time, double distance) {
			return e == search && (maxTime < 0 || time < maxTime)
					&& (maxDistance < 0 || distance < maxDistance);
		}
	}

	public enum ReplacableEvent {
		DRAW,
		PAN,
		HOVER;
	}

	public enum Replacement {
		COMMAND_SELECT,
		COMMAND_ACTION;
	}
}
