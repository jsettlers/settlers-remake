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
package go.graphics.area;

import java.util.LinkedList;

import go.graphics.DrawmodeListener;
import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.interpreter.AbstractMouseEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GODrawEventProxy;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;
import go.graphics.event.mouse.GOZoomEvent;
import go.graphics.region.Region;

/**
 * This class represents an area. This is a rectangular part of the screen that consists of multiple regions.
 *
 * @author michael
 */
public class Area implements RedrawListener, GOEventHandlerProvider {
	/**
	 * How wide is a border of this area?
	 */
	public static final int BORDER_SIZE = 1;

	private int width;
	private int height;

	private Region region;

	private final LinkedList<RedrawListener> redrawListeners = new LinkedList<>();

	private DrawmodeListener drawmodeListener;

	/**
	 * Creates a new area.
	 */
	public Area() {

	}

	/**
	 * Sets the width of the area.
	 *
	 * @param width
	 *            The width in pixels.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void set(Region region) {
		this.region = region;
		region.addRedrawListener(this);
	}

	public void setDrawmodeListener(DrawmodeListener drawmodeListener) {
		this.drawmodeListener = drawmodeListener;
	}

	/**
	 * Draws the area at the given gl context.
	 * <p>
	 * it assumes that the transformation is set so that the lower left corner is (0,0).
	 *
	 * @param gl2
	 */
	public void drawArea(GLDrawContext gl2) {
		region.drawRegion(gl2, width, height);
		// Rectangle border = position.getBorder();
		// if (border != null) {
		// drawBorder(gl2, border);
		// }
	}

	// /**
	// * Draws a border for a region.
	// *
	// * @param gl2
	// * The context to draw on.
	// * @param position
	// * The position oft the region to draw the border for.
	// */
	// private void drawBorder(GLDrawContext gl2, Rectangle position) {
	// gl2.color(.5f, .5f, .5f, 1);
	//
	// gl2.fillQuad(position.x, position.y, position.x + position.width,
	// position.y + position.height);
	// }

	/**
	 * Handles a mouse event somewhere in the area and passes it on to the region.
	 *
	 * @param event
	 *            The event.
	 */
	private void handleMouseEvent(GODrawEvent event) {
		GODrawEventProxy displacedEvent = new GODrawEventProxy(event, new UIPoint(0, 0));
		region.handleEvent(displacedEvent);
	}

	private void handleHoverEvent(GOHoverEvent e) {
		region.handleEvent(e);
	}


	private class SimpleHoverEvent extends AbstractMouseEvent implements
			GOHoverEvent {
		public void finish() {
			this.setPhase(PHASE_FINISHED);
		}

		@Override
		public UIPoint getHoverPosition() {
			return this.position;
		}

		@Override
		protected void setMousePosition(UIPoint position) {
			super.setMousePosition(position);
		}
	}

	public void handleCommandEvent(GOCommandEvent event) {
		region.handleEvent(event);
	}

	/**
	 * Handles any known type of event.
	 *
	 * @param event
	 *            The event to handle.
	 */
	@Override
	public void handleEvent(GOEvent event) {

		if(event instanceof GOKeyEvent) {
			if ("m".equalsIgnoreCase(((GOKeyEvent) event).getKeyCode())) {
				if(drawmodeListener != null) drawmodeListener.changeDrawMode();
			}
		}

		if (event instanceof GOCommandEvent) {
			handleCommandEvent((GOCommandEvent) event);
		} else if (event instanceof GOPanEvent) {
			handlePanEvent((GOPanEvent) event);
		} else if (event instanceof GODrawEvent) {
			handleMouseEvent((GODrawEvent) event);
		} else if (event instanceof GOHoverEvent) {
			handleHoverEvent((GOHoverEvent) event);
		} else if (event instanceof GOKeyEvent) {
			region.handleEvent(event);
		} else if (event instanceof GOZoomEvent) {
			region.handleEvent(event);
		}
	}

	private void handlePanEvent(GOPanEvent event) {
		region.handleEvent(event);
	}

	@Override
	public void requestRedraw() {
		for (RedrawListener l : redrawListeners) {
			l.requestRedraw();
		}
	}

	public void addRedrawListener(RedrawListener l) {
		redrawListeners.add(l);
	}

}
