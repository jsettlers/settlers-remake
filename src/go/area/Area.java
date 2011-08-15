package go.area;

import go.RedrawListener;
import go.event.GOEvent;
import go.event.GOKeyEvent;
import go.event.command.GOCommandEvent;
import go.event.command.GOCommandEventProxy;
import go.event.mouse.GODrawEvent;
import go.event.mouse.GODrawEventProxy;
import go.event.mouse.GOHoverEvent;
import go.event.mouse.GOHoverEventProxy;
import go.event.mouse.GOPanEvent;
import go.event.mouse.GOPanEventProxy;
import go.region.Region;
import go.region.RegionPosition;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * This class represents an area. This is a rectangular part of the screen that
 * consists of multiple regions.
 * 
 * @author michael
 */
public class Area implements RedrawListener {
	/**
	 * How wide is a border of this area?
	 */
	public static final int BORDER_SIZE = 1;

	private int width;
	private int height;

	private ArrayList<Region> regions = new ArrayList<Region>();

	private LinkedList<RedrawListener> redrawListeners =
	        new LinkedList<RedrawListener>();

	private final ArrayList<RegionPosition> regionPositions = new ArrayList<RegionPosition>();

	public Area() {

	}

	public void setWidth(int width) {
		this.width = width;
		recalculateRegions();
	}

	public int getWidth() {
		return this.width;
	}

	public void setHeight(int height) {
		this.height = height;
		recalculateRegions();
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Region region) {
		this.regions.add(region);
		region.addRedrawListener(this);
		recalculateRegions();
		requestRedraw();
	}

	public void drawArea(GL2 gl2) {
		for (int i = 0; i < this.regionPositions.size(); i++) {
			RegionPosition position = this.regionPositions.get(i);

			Rectangle content = position.getContent();
			drawRegionAt(gl2, position.getRegion(), content);
			Rectangle border = position.getBorder();
			if (border != null) {
				drawBorder(gl2, border);
			}
		}
	}

	private void recalculateRegions() {
		int regionCount = this.regions.size();
		
		this.regionPositions.clear();

		int top = this.height;
		int bottom = 0;
		int left = 0;
		int right = this.width;

		for (int i = 0; i < regionCount; i++) {
			Region r = this.regions.get(i);

			RegionPosition position = null;

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

						position =
						        new RegionPosition(r, top, top - size, left,
						                right);

						top -= size;
						break;

					case Region.POSITION_BOTTOM:
						if (top - bottom < size) {
							size = top - bottom;
						}

						position = new RegionPosition(r, size, 0, left, right);

						bottom += size;
						break;

					case Region.POSITION_LEFT:
						if (right - left < size) {
							size = right - left;
						}

						position = new RegionPosition(r, top, bottom, 0, size);

						left += size;
						break;

					case Region.POSITION_RIGHT:
						if (right - left < size) {
							size = right - left;
						}

						position =
						        new RegionPosition(r, top, bottom,
						                right - size, right);

						left += size;
						break;

					case Region.POSITION_CENTER:
					default:
						position =
						        new RegionPosition(r, top, bottom, left, right);
						top = bottom; // break;
						break;
				}
			}
			this.regionPositions.add(position);
		}
	}

	private void drawBorder(GL2 gl2, Rectangle position) {
		gl2.glColor3d(.5, .5, .5);

		gl2.glBegin(GL2.GL_QUADS);
		gl2.glVertex2i(position.x, position.y);
		gl2.glVertex2i(position.x, position.y + position.height);
		gl2.glVertex2i(position.x + position.width, position.y
		        + position.height);
		gl2.glVertex2i(position.x + position.width, position.y);
		gl2.glEnd();
	}

	private void drawRegionAt(GL2 gl2, Region r, Rectangle position) {

		gl2.glPushMatrix();
		gl2.glTranslatef(position.x, position.y, 0);

		// make stencil buffer drawable

		gl2.glColorMask(false, false, false, false);
		gl2.glDepthMask(false);
		gl2.glEnable(GL.GL_STENCIL_TEST);
		gl2.glStencilFunc(GL.GL_ALWAYS, 1, 0xFFFFFFFF); // draw stencil buffer
		gl2.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
		gl2.glRecti(0, 0, position.width, position.height); // return to normal
		// draw context
		gl2.glColorMask(true, true, true, true);
		gl2.glDepthMask(true);
		gl2.glStencilFunc(GL.GL_EQUAL, 1, 0xFFFFFFFF);
		gl2.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		r.drawRegion(gl2, position.width, position.height);

		gl2.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl2.glPopMatrix();
	}

	/**
	 * Handles a mouse event somewhere in the area and passes it on to the
	 * region.
	 * 
	 * @param event
	 *            The event.
	 */
	private void handleMouseEvent(GODrawEvent event) {
		for (int i = 0; i < this.regionPositions.size(); i++) {
			Rectangle pos = this.regionPositions.get(i).getContent();
			if (pos.contains(event.getMousePosition())) {
				GODrawEventProxy displacedEvent =
				        new GODrawEventProxy(event, new Point(pos.x, pos.y));
				this.regionPositions.get(i).getRegion().handleMouseEvent(
				        displacedEvent);
				break;
			}
		}
	}

	private void handleMouseEvent(GOHoverEvent event) {
		for (int i = 0; i < this.regionPositions.size(); i++) {
			Rectangle pos = this.regionPositions.get(i).getContent();
			if (pos.contains(event.getMousePosition())) {
				GOHoverEventProxy displacedEvent =
				        new GOHoverEventProxy(event, new Point(pos.x, pos.y));
				this.regionPositions.get(i).getRegion().handleEvent(
				        displacedEvent);
				break;
			}
		}
	}

	private void handleCommandEvent(GOCommandEvent event) {
		for (int i = 0; i < this.regionPositions.size(); i++) {
			Rectangle pos = this.regionPositions.get(i).getContent();
			if (pos.contains(event.getCommandPosition())) {
				GOCommandEventProxy displacedEvent =
				        new GOCommandEventProxy(event, new Point(pos.x, pos.y));
				this.regionPositions.get(i).getRegion().handleCommandEvent(
				        displacedEvent);
				break;
			}
		}
	}

	/**
	 * Handles any known type of event.
	 * 
	 * @param event
	 *            The event to handle.
	 */
	public void handleEvent(GOEvent event) {
		if (event instanceof GOCommandEvent) {
			handleCommandEvent((GOCommandEvent) event);
		} else if (event instanceof GOPanEvent) {
			handlePanEvent((GOPanEvent) event);
		} else if (event instanceof GODrawEvent) {
			handleMouseEvent((GODrawEvent) event);
		} else if (event instanceof GOHoverEvent) {
			System.out.println("hover");
			handleMouseEvent((GOHoverEvent) event);
		} else if (event instanceof GOKeyEvent) {
			handleKeyEvent((GOKeyEvent) event);
		}
	}

	private void handleKeyEvent(GOKeyEvent event) {
	    this.regions.get(0).handleEvent(event);
    }

	private void handlePanEvent(GOPanEvent event) {
		RegionPosition foundPosition = getRegionAt(event.getPanCenter());

		Point topLeft =
		        new Point(foundPosition.getContent().x, foundPosition
		                .getContent().y);

		GOPanEventProxy displacedEvent = new GOPanEventProxy(event, topLeft);
		foundPosition.getRegion().handleEvent(displacedEvent);
	}

	private RegionPosition getRegionAt(Point eventPosition) {
		Iterator<RegionPosition> it = this.regionPositions.iterator();
		RegionPosition foundPosition = null;
		while (it.hasNext() && foundPosition == null) {
			RegionPosition currentPos = it.next();
			if (currentPos != null
			        && currentPos.getContent().contains(eventPosition)) {
				foundPosition = currentPos;
			}
		}
		return foundPosition;
	}

	@Override
	public void requestRedraw() {
		for (RedrawListener l : this.redrawListeners) {
			l.requestRedraw();
		}
	}

	public void addRedrawListener(RedrawListener l) {
		this.redrawListeners.add(l);
	}

}
