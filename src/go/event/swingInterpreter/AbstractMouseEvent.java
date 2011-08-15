package go.event.swingInterpreter;

import go.event.SingleHandlerGoModalEvent;

import java.awt.Point;

public class AbstractMouseEvent extends SingleHandlerGoModalEvent {

	protected Point position;
	private int mouseMoved = 0;

	public AbstractMouseEvent() {
		super();
	}

	/**
	 * Stats that the event was initialized and should change to its modal pahse.
	 */
	public void initialized() {
		setPhase(PHASE_STARTED);
		setPhase(PHASE_MODAL);
	}

	/**
	 * Ends the event.
	 */
	public void released() {
		setPhase(PHASE_FINISHED);
	}

	/**
	 * The event was aborted.
	 */
	public void aborted() {
		setPhase(PHASE_ABORTED);
	}

	/**
	 * Computes whether the mouse was moved (more than just a few pixels) during the event.
	 * 
	 * @return true if the mouse was moved while beeing pressed.
	 */
	public int getMouseMoved() {
		return this.mouseMoved;
	}

	/**
	 * Sets the mouse position as a given point.
	 * 
	 * @param position
	 *            The position.
	 */
	protected void setMousePosition(final Point position) {
		this.mouseMoved += Math.abs(position.x - this.position.x) + Math.abs(position.y - this.position.y);

		this.position = position;
		if (getPhase() == PHASE_MODAL) {
			fireModalDataRefreshed();
		}
	}
}