package go.graphics.event.command;

import go.graphics.event.mouse.GOEventProxy;

import java.awt.Point;

/**
 * This class proxys a mouse event with a given displacement.
 * 
 * @author michael
 */
public class GOCommandEventProxy extends GOEventProxy<GOCommandEvent> implements
        GOCommandEvent {

	private final Point displacement;

	/**
	 * @param baseEvent
	 *            The event that is proxied.
	 * @param displacement
	 *            The top left border of the suparea of the parent area.
	 */
	public GOCommandEventProxy(GOCommandEvent baseEvent, Point displacement) {
		super(baseEvent);
		this.displacement = displacement;
	}

	@Override
	public Point getCommandPosition() {
		Point real = baseEvent.getCommandPosition();
		return new Point(real.x - displacement.x, real.y - displacement.y);
	}

	@Override
	public boolean isSelecting() {
		return baseEvent.isSelecting();
	}
}
