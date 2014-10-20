package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOPanEvent;

/**
 * This is a pan event that is converted from a normal swing mouse event.
 * 
 * @author michael
 */
class ConvertedPanEvent extends AbstractMouseEvent implements GOPanEvent {

	private final UIPoint original;

	/**
	 * Creates a new converted event.
	 * 
	 * @param point
	 *            THe point the user first put his mouse to.
	 */
	protected ConvertedPanEvent(UIPoint point) {
		this.position = point;
		this.original = point;
	}

	@Override
	public UIPoint getPanDistance() {
		return new UIPoint(position.getX() - original.getX(), position.getY() - original.getY());
	}

	@Override
	public UIPoint getPanCenter() {
		return original;
	}
}
