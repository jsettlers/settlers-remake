package go.graphics.event.mouse;

import go.graphics.UIPoint;

/**
 * This is a proxy that translates the pan event.
 * 
 * @author michael
 * 
 */
public class GOPanEventProxy extends GOEventProxy<GOPanEvent> implements GOPanEvent {

	private final UIPoint displacement;

	/**
	 * Creates a new pan proxy.
	 * 
	 * @param event
	 *            The base event.
	 * @param displaced
	 *            The dsiplace vector.
	 */
	public GOPanEventProxy(GOPanEvent event, UIPoint displaced) {
		super(event);
		this.displacement = displaced;
	}

	@Override
	public UIPoint getPanCenter() {
		UIPoint real = (this.baseEvent).getPanCenter();
		return new UIPoint(real.getX() - this.displacement.getX(), real.getY() - this.displacement.getY());
	}

	@Override
	public UIPoint getPanDistance() {
		UIPoint real = (this.baseEvent).getPanDistance();
		return real;
	}
}
