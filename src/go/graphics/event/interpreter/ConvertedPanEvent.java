package go.graphics.event.interpreter;

import go.graphics.event.mouse.GOPanEvent;

import java.awt.Point;

/**
 * This is a pan event that is converted from a normal swing mouse event.
 * 
 * @author michael
 */
class ConvertedPanEvent extends AbstractMouseEvent implements GOPanEvent {

	private final Point original;

	/**
	 * Creates a new converted event.
	 * 
	 * @param point
	 *            THe point the user first put his mouse to.
	 */
	protected ConvertedPanEvent(Point point) {
		this.position = point;
		this.original = point;
	}

	@Override
	public Point getPanDistance() {
		return new Point(position.x - original.x, position.y - original.y);
	}

	@Override
	public Point getPanCenter() {
		return original;
	}
}
