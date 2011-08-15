package go.event.swingInterpreter;

import go.event.mouse.GOHoverEvent;

import java.awt.Point;

/**
 * This is a mouse vent that was converted to a go event.
 * 
 * @author michael
 */
class ConvertedHoverEvent extends AbstractMouseEvent implements
        GOHoverEvent {			

	/**
	 * Creates a new mouse event.
	 * 
	 * @param point
	 *            The point where it starts.
	 */
	public ConvertedHoverEvent(final Point point) {
		this.position = point;
	}

	@Override
	public Point getMousePosition() {
		return this.position;
	}
}