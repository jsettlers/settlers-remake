package go.event.mouse;

import java.awt.Point;

/**
 * This is a proxy that translates the pan event.
 * 
 * @author michael
 * 
 */
public class GOPanEventProxy extends GOEventProxy<GOPanEvent> implements GOPanEvent {

	private final Point displacement;

	/**
	 * Creates a new pan proxy.
	 * 
	 * @param event
	 *            The base event.
	 * @param displaced
	 *            The dsiplace vector.
	 */
	public GOPanEventProxy(GOPanEvent event, Point displaced) {
		super(event);
		this.displacement = displaced;
	}

	@Override
	public Point getPanCenter() {
		Point real = (this.baseEvent).getPanCenter();
		return new Point(real.x - this.displacement.x, real.y - this.displacement.y);
	}

	@Override
	public Point getPanDistance() {
		Point real = (this.baseEvent).getPanDistance();
		return new Point(real.x - this.displacement.x, real.y - this.displacement.y);
	}
}
