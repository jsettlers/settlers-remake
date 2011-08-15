package go.event.swingInterpreter;

import go.event.mouse.GOPanEvent;

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
		return new Point(this.position.x - this.original.x, this.position.y - this.original.y);
	}

	@Override
	public Point getPanCenter() {
		return this.original;
	}
}
