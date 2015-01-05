package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;

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
	public ConvertedDrawEvent(final UIPoint point) {
		this.position = point;
	}

	@Override
	public UIPoint getDrawPosition() {
		return this.position;
	}
}