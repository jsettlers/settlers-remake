package go.event.mouse;

import go.event.GOEvent;

import java.awt.Point;

/**
 * This is a mouse event.
 * @author michael
 *
 */
public interface GOHoverEvent extends GOEvent {
	
	/**
	 * gets the position the mouse is currently on. May not be null.
	 * @return The point.
	 */
	Point getMousePosition();
}
