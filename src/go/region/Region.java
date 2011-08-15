package go.region;

import go.RedrawListener;
import go.event.GOEvent;
import go.event.GOEventHandlerProvoder;
import go.event.command.GOCommandEventProxy;
import go.event.mouse.GODrawEvent;
import go.event.mouse.GOPanEvent;

import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL2;

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

	private final int position;

	private int size;

	private LinkedList<RedrawListener> redrawListeners =
	        new LinkedList<RedrawListener>();

	private boolean collapsed = false;

	private RegionContent content = null;
	
	private LinkedList<GOEventHandlerProvoder> eventHandlers =
	        new LinkedList<GOEventHandlerProvoder>();

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
	 * Draws the region on the gl space, assuming it 0,0 is the bottom left
	 * corner.
	 * 
	 * @param gl2
	 *            The gl space
	 * @param width
	 *            The width of the region.
	 * @param height
	 *            The height of the region.
	 */
	public void drawRegion(GL2 gl2, int width, int height) {
		gl2.glColor3d(.3, .3, .3);

		gl2.glBegin(GL2.GL_QUADS);
		gl2.glVertex3i(0, 0, 0);
		gl2.glVertex3i(0, height, 0);
		gl2.glVertex3i(width, height, 0);
		gl2.glVertex3i(width, 0, 0);
		gl2.glEnd();

		if (this.content != null) {
			this.content.drawContent(gl2, width, height);
		}

	}

	/**
	 * Gets the position of the region in the area.
	 * 
	 * @return The position constant.
	 */
	public int getPosition() {
		return this.position;
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
		return this.size;
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
		return this.collapsed;
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
		return this.content;
	}

	public void redraw() {
		for (RedrawListener listener : this.redrawListeners) {
			listener.requestRedraw();
		}
	}

	/**
	 * Adds a redraw listener to the region.
	 * @param l
	 */
	public void addRedrawListener(RedrawListener l) {
		this.redrawListeners.add(l);
	}

	/**
	 * Adds an provider that can seth handlers for the venets of this region.
	 * @param p The handler.
	 */
	public void addEventHandler(GOEventHandlerProvoder p) {
		synchronized (this.eventHandlers) {
			this.eventHandlers.add(p);
		}
	}

	/**
	 * Fires a go event, asks the handler providers to handle it.
	 * @param event The event to fire.
	 */
	private void fireGoEvent(GOEvent event) {
		synchronized (this.eventHandlers) {
			if (this.content instanceof GOEventHandlerProvoder) {
				((GOEventHandlerProvoder) this.content).handleEvent(event);
			}
	        Iterator<GOEventHandlerProvoder> it = this.eventHandlers.iterator();
	        while (it.hasNext()) {
	        	it.next().handleEvent(event);
	        }
        }
	}
	
	/**
	 * Fires a go event on the region.
	 * @param event The event to fire.
	 */
	public void handleEvent(GOEvent event) {
		fireGoEvent(event);
	}

	/**
	 * Lets the area handle a mouse event.
	 * 
	 * @param event
	 *            The mouse event to handle.
	 */
	public void handleMouseEvent(GODrawEvent event) {
		fireGoEvent(event);
	}
	
	public void handlePanEvent(GOPanEvent event) {
		fireGoEvent(event);
	}

	public void handleCommandEvent(GOCommandEventProxy displacedEvent) {
		fireGoEvent(displacedEvent);
	}

	@Override
    public void requestRedraw() {
	    redraw();
    }
}
