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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.command.GOCommandEventProxy;
import go.graphics.event.interpreter.AbstractMouseEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GODrawEventProxy;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;
import go.graphics.event.mouse.GOPanEventProxy;
import go.graphics.event.mouse.GOZoomEvent;
import go.graphics.region.PositionedRegion;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;

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

	private final ArrayList<Region> regions = new ArrayList<>();

	private final LinkedList<RedrawListener> redrawListeners = new LinkedList<>();

	private ArrayList<PositionedRegion> regionPositions;

	private Region activeRegion = null;

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
		this.regionPositions = null;
		this.width = width;
	}

	/**
	 * gets the width of the area.
	 *
	 * @return The width
	 */
	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.regionPositions = null;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void add(Region region) {
		regions.add(region);
		regionPositions = null;
		region.addRedrawListener(this);
	}

	/**
	 * Draws the area at the given gl context.
	 * <p>
	 * it assumes that the transformation is set so that the lower left corner is (0,0).
	 *
	 * @param gl2
	 */
	public void drawArea(GLDrawContext gl2) {
		if (regionPositions == null) {
			recalculateRegions();
		}

		for (int i = 0; i < regionPositions.size(); i++) {
			PositionedRegion position = regionPositions.get(i);

			drawRegionAt(gl2, position);
			// Rectangle border = position.getBorder();
			// if (border != null) {
			// drawBorder(gl2, border);
			// }
		}
	}

	private static void drawRegionAt(GLDrawContext gl2, PositionedRegion position) {
		gl2.glPushMatrix();
		gl2.glTranslatef(position.getLeft(), position.getBottom(), 0);

		position.getRegion().drawRegion(gl2, position.getRight() - position.getLeft(),
				position.getTop() - position.getBottom());

		gl2.glPopMatrix();
	}

	private void recalculateRegions() {
		int regionCount = regions.size();
		regionPositions = new ArrayList<>();

		int top = height;
		int bottom = 0;
		int left = 0;
		int right = width;

		for (int i = 0; i < regionCount; i++) {
			Region r = regions.get(i);

			PositionedRegion position = null;

			if (r.isCollapsed() || bottom > top || left > right) {
				position = null;
				continue;
			} else {

				int size = r.getSize() + BORDER_SIZE;
				switch (r.getPosition()) {
				case Region.POSITION_TOP:
					if (top - bottom < size) {
						size = top - bottom;
					}

					position = new PositionedRegion(r, top, top - size, left,
							right);

					top -= size;
					break;

				case Region.POSITION_BOTTOM:
					if (top - bottom < size) {
						size = top - bottom;
					}

					position = new PositionedRegion(r, size, 0, left, right);

					bottom += size;
					break;

				case Region.POSITION_LEFT:
					if (right - left < size) {
						size = right - left;
					}

					position = new PositionedRegion(r, top, bottom, 0, size);

					left += size;
					break;

				case Region.POSITION_RIGHT:
					if (right - left < size) {
						size = right - left;
					}

					position = new PositionedRegion(r, top, bottom, right
							- size, right);

					left += size;
					break;

				case Region.POSITION_CENTER:
				default:
					position = new PositionedRegion(r, top, bottom, left,
							right);
					top = bottom; // break;
					break;
				}
			}
			regionPositions.add(position);
		}
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
		if (regionPositions == null) {
			recalculateRegions();
		}

		for (int i = 0; i < regionPositions.size(); i++) {
			PositionedRegion pos = regionPositions.get(i);
			if (pos.contentContains(event.getDrawPosition())) {
				setActiveRegion(regionPositions.get(i).getRegion());
				GODrawEventProxy displacedEvent = new GODrawEventProxy(event, new UIPoint(pos.getLeft(),
						pos.getBottom()));
				regionPositions.get(i).getRegion().handleEvent(displacedEvent);
				break;
			}
		}
	}

	private void handleHoverEvent(GOHoverEvent e) {
		e.setHandler(new SplitHoverHandler());
	}

	private class SplitHoverHandler implements GOModalEventHandler {
		private PositionedRegion currentRegion;
		private RegionContent currentContent;
		private SimpleHoverEvent sendEvent;

		@Override
		public void phaseChanged(GOEvent event) {
			if (event.getPhase() == GOEvent.PHASE_STARTED) {
				eventDataChanged(event);
			}
		}

		@Override
		public void finished(GOEvent event) {
			if (sendEvent != null) {
				sendEvent.finish();
			}
		}

		@Override
		public void aborted(GOEvent event) {
			if (event != null) {
				sendEvent.finish();
			}
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			if (event instanceof GOHoverEvent) {
				UIPoint point = ((GOHoverEvent) event).getHoverPosition();
				PositionedRegion nextRegion = getUnder(point);
				RegionContent nextContent = nextRegion == null ? null : nextRegion.getRegion().getContent();
				if (nextContent != currentContent) {
					if (currentRegion != null) {
						endWithRegion(point);
					}
					currentRegion = nextRegion;
					currentContent = nextContent;
					if (nextRegion != null) {
						startWithRegion(point);
					}
				} else if (sendEvent != null) {
					changePoint(point);
				}
			}
		}

		private void startWithRegion(UIPoint areaPoint) {
			sendEvent = new SimpleHoverEvent();
			changePoint(areaPoint);
			currentRegion.getRegion().handleEvent(sendEvent);
			sendEvent.initialized();
		}

		private void endWithRegion(UIPoint point) {
			changePoint(point);
			sendEvent.finish();
			sendEvent = null;
		}

		private void changePoint(UIPoint point) {
			double x = point.getX() - currentRegion.getLeft();
			double y = point.getY() - currentRegion.getBottom();
			sendEvent.setMousePosition(new UIPoint(x, y));
		}
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

	private PositionedRegion getUnder(UIPoint p) {
		if (regionPositions == null) {
			recalculateRegions();
		}

		for (PositionedRegion region : regionPositions) {
			if (region.contentContains(p)) {
				return region;
			}
		}
		return null;
	}

	public void handleCommandEvent(GOCommandEvent event) {
		for (int i = 0; i < regionPositions.size(); i++) {
			PositionedRegion pos = regionPositions.get(i);
			if (pos.contentContains(event.getCommandPosition())) {
				GOCommandEventProxy displacedEvent = new GOCommandEventProxy(event,
						new UIPoint(pos.getLeft(), pos.getBottom()));
				setActiveRegion(regionPositions.get(i).getRegion());
				regionPositions.get(i).getRegion().handleEvent(displacedEvent);
				break;
			}
		}
	}

	private void setActiveRegion(Region region) {
		activeRegion = region;
	}

	/**
	 * Handles any known type of event.
	 *
	 * @param event
	 *            The event to handle.
	 */
	@Override
	public void handleEvent(GOEvent event) {

		if (event instanceof GOCommandEvent) {
			handleCommandEvent((GOCommandEvent) event);
		} else if (event instanceof GOPanEvent) {
			handlePanEvent((GOPanEvent) event);
		} else if (event instanceof GODrawEvent) {
			handleMouseEvent((GODrawEvent) event);
		} else if (event instanceof GOHoverEvent) {
			handleHoverEvent((GOHoverEvent) event);
		} else if (event instanceof GOKeyEvent) {
			if (activeRegion != null) {
				activeRegion.handleEvent(event);
			}
		} else if (event instanceof GOZoomEvent) {
			if (activeRegion == null && regionPositions != null && !regionPositions.isEmpty()) {
				// if there is no active region, set the first active,
				// so the zoom is working in the Editor, even if you
				// didn't press a mouse button
				setActiveRegion(regionPositions.get(0).getRegion());
			}
			if (activeRegion != null) {
				activeRegion.handleEvent(event);
			}
		}
	}

	private void handlePanEvent(GOPanEvent event) {
		if (event.getPanCenter() == null) {
			if (!regionPositions.isEmpty()) {
				PositionedRegion centerPosition = regionPositions
						.get(regionPositions.size() - 1);
				centerPosition.getRegion().handleEvent(event);
			}
		} else {
			PositionedRegion foundPosition = getRegionAt(event.getPanCenter());

			if (foundPosition != null) {
				UIPoint topLeft = new UIPoint(foundPosition.getLeft(),
						foundPosition.getTop());

				GOPanEventProxy displacedEvent = new GOPanEventProxy(event,
						topLeft);
				foundPosition.getRegion().handleEvent(displacedEvent);
			}
		}
	}

	private PositionedRegion getRegionAt(UIPoint eventPosition) {
		Iterator<PositionedRegion> it = regionPositions.iterator();
		PositionedRegion foundPosition = null;
		while (it.hasNext() && foundPosition == null) {
			PositionedRegion currentPos = it.next();
			if (currentPos != null
					&& currentPos.contentContains(eventPosition)) {
				foundPosition = currentPos;
			}
		}
		return foundPosition;
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
