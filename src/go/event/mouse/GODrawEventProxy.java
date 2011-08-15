package go.event.mouse;

import java.awt.Point;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GODrawEventProxy extends GOEventProxy<GODrawEvent> implements GODrawEvent {

	private final Point displacement;

	/**
	 * @param baseEvent
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GODrawEventProxy(GODrawEvent baseEvent, Point displacement) {
		super(baseEvent);
		this.displacement = displacement;
	}

	@Override
	public Point getMousePosition() {
		Point real = (this.baseEvent).getMousePosition();
		return new Point(real.x - this.displacement.x, real.y - this.displacement.y);
	}
}
