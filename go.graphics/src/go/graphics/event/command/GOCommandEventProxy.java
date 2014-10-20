package go.graphics.event.command;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOEventProxy;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GOCommandEventProxy extends GOEventProxy<GOCommandEvent> implements
        GOCommandEvent {

	private final UIPoint displacement;

	/**
	 * @param baseEvent
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GOCommandEventProxy(GOCommandEvent baseEvent, UIPoint displacement) {
		super(baseEvent);
		this.displacement = displacement;
	}

	@Override
	public UIPoint getCommandPosition() {
		UIPoint real = baseEvent.getCommandPosition();
		return new UIPoint(real.getX() - displacement.getX(), real.getY() - displacement.getY());
	}

	@Override
	public boolean isSelecting() {
		return baseEvent.isSelecting();
	}
}
