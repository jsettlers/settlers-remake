package go.graphics.event.mouse;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;

/**
 * This is a mouse event.
 * 
 * @author michael
 *
 */
public interface GODrawEvent extends GOEvent {

	/**
	 * gets the position the mouse is currently on. May not be null.
	 * 
	 * @return The point.
	 */
	UIPoint getDrawPosition();
}
