package go.graphics.event.interpreter;

import go.graphics.event.mouse.GODrawEvent;

import java.awt.Point;

/**
 * This is a mouse vent that was converted to a go event.
 * 
 * @author michael
 */
public class ConvertedDrawEvent extends AbstractMouseEvent implements
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
	public Point getDrawPosition() {
		return this.position;
	}
}