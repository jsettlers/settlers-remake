/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package go.graphics.region;

import static java8.util.stream.StreamSupport.stream;

import java.util.LinkedList;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;

/**
 * This class represents a region, a part of an area.
 *
 * @author michael
 */
public class Region implements RedrawListener {
	/**
	 * Positions the region at the top of the area
	 */
	public static final int POSITION_TOP = 1;
	/**
	 * Positions the region at the bottom of the area
	 */
	public static final int POSITION_BOTTOM = 2;
	/**
	 * Positions the region at the left of the area
	 */
	public static final int POSITION_LEFT = 3;
	/**
	 * Positions the region to the right, size is the width
	 */
	public static final int POSITION_RIGHT = 4;
	/**
	 * Positions the region in the center, must be last region
	 */
	public static final int POSITION_CENTER = 0;
	/**
	 * The default width a region has.
	 */
	private static final int DEFAULT_WIDTH = 20;

	private final LinkedList<RedrawListener> redrawListeners = new LinkedList<>();
	private final LinkedList<GOEventHandlerProvider> eventHandlers = new LinkedList<>();

	private final int position;
	private int size;
	private boolean collapsed = false;
	private RegionContent content = null;

	/**
	 * Creates a new region with a dfault size.
	 *
	 * @param position
	 *            The position it should have on the area
	 */
	public Region(int position) {
		this(position, DEFAULT_WIDTH);
	}

	/**
	 * Creates a new region with a given size.
	 *
	 * @param position
	 *            The position.
	 * @param size
	 *            The size of the region.
	 */
	public Region(int position, int size) {
		this.position = position;
		this.size = size;
	}

	/**
	 * Draws the region on the gl space, assuming it 0,0 is the bottom left corner.
	 *
	 * @param gl2
	 *            The gl space
	 * @param width
	 *            The width of the region.
	 * @param height
	 *            The height of the region.
	 */
	public void drawRegion(GLDrawContext gl2, int width, int height) {
		// gl2.color(.3f, .3f, .3f, 1);
		// gl2.fillQuad(0,0, width, height);

		if (content != null) {
			content.drawContent(gl2, width, height);
		}

	}

	/**
	 * Gets the position of the region in the area.
	 *
	 * @return The position constant.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the size the region should have.
	 *
	 * @param size
	 *            The size.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * gets the size of the region
	 *
	 * @return The size in pixel.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the collapsed flag of the region.
	 *
	 * @param collapsed
	 *            If the region should be collapsed.
	 */
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	/**
	 * Sets whether the region is collapsed.
	 *
	 * @return The collapsed flag.
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * Sets the content of the region.
	 *
	 * @param content
	 *            An object providing the draw mechanism.
	 */
	public void setContent(RegionContent content) {
		this.content = content;
	}

	/**
	 * Gets the content of the region
	 *
	 * @return The content that is drawn on the region.
	 */
	public RegionContent getContent() {
		return content;
	}

	/**
	 * Adds a redraw listener to the region.
	 *
	 * @param listener
	 *            the new RedrawListener
	 */
	public void addRedrawListener(RedrawListener listener) {
		redrawListeners.add(listener);
	}

	/**
	 * Adds an provider that can seth handlers for the venets of this region.
	 *
	 * @param p
	 *            The handler.
	 */
	public void addEventHandler(GOEventHandlerProvider p) {
		synchronized (eventHandlers) {
			eventHandlers.add(p);
		}
	}

	/**
	 * Fires a go event, asks the handler providers to handle it.
	 *
	 * @param event
	 *            The event to fire.
	 */
	private void fireGoEvent(GOEvent event) {
		synchronized (eventHandlers) {
			if (content != null) {
				content.handleEvent(event);
			}
			stream(eventHandlers).forEach(eventHandler -> eventHandler.handleEvent(event));
		}
	}

	/**
	 * Lets the region handle a event.
	 * <p>
	 * All listeners are asked to set themselves as handler for the event.
	 *
	 * @param event
	 *            The event.
	 */
	public void handleEvent(GOEvent event) {
		fireGoEvent(event);
	}

	@Override
	public void requestRedraw() {
		stream(redrawListeners).forEach(RedrawListener::requestRedraw);
	}
}
