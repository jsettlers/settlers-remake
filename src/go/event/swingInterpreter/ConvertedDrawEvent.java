package go.event.swingInterpreter;

import go.event.mouse.GODrawEvent;

import java.awt.Point;

/**
 * This is a mouse vent that was converted to a go event.
 * 
 * @author michael
 */
class ConvertedDrawEvent extends AbstractMouseEvent implements
        GODrawEvent {
	/**
	 * Creates a new mouse event.
	 * 
	 * @param point
	 *            The point where it starts.
	 */
	public ConvertedDrawEvent(final Point point) {
		this.position = point;
	}

	@Override
	public Point getMousePosition() {
		return this.position;
	}
}