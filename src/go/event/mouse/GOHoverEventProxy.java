package go.event.mouse;

import java.awt.Point;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GOHoverEventProxy extends GOEventProxy<GOHoverEvent> implements GOHoverEvent {

	private final Point displacement;

	/**
	 * @param event
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GOHoverEventProxy(GOHoverEvent event, Point displacement) {
		super(event);
		this.displacement = displacement;
	}

	@Override
	public Point getMousePosition() {
		Point real = (this.baseEvent).getMousePosition();
		return new Point(real.x - this.displacement.x, real.y - this.displacement.y);
	}
}
