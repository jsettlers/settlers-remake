package go.graphics.event.mouse;

import go.graphics.UIPoint;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GODrawEventProxy extends GOEventProxy<GODrawEvent> implements GODrawEvent {

	private final UIPoint displacement;

	/**
	 * @param baseEvent
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GODrawEventProxy(GODrawEvent baseEvent, UIPoint displacement) {
		super(baseEvent);
		this.displacement = displacement;
	}

	@Override
	public UIPoint getDrawPosition() {
		UIPoint real = (this.baseEvent).getDrawPosition();
		return new UIPoint(real.getX() - this.displacement.getX(), real.getY() - this.displacement.getY());
	}
}
