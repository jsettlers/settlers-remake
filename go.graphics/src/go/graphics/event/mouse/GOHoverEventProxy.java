package go.graphics.event.mouse;

import go.graphics.UIPoint;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GOHoverEventProxy extends GOEventProxy<GOHoverEvent> implements
		GOHoverEvent {

	private final UIPoint displacement;

	/**
	 * @param event
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GOHoverEventProxy(GOHoverEvent event, UIPoint displacement) {
		super(event);
		this.displacement = displacement;
	}

	@Override
	public UIPoint getHoverPosition() {
		UIPoint real = (this.baseEvent).getHoverPosition();
		return new UIPoint(real.getX() - this.displacement.getX(), real.getY()
				- this.displacement.getY());
	}
}
