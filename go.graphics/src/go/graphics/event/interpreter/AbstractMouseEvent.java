package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.SingleHandlerGoModalEvent;

public class AbstractMouseEvent extends SingleHandlerGoModalEvent {

	protected UIPoint position;
	private int mouseMoved = 0;
	private long startTime = 0;

	public AbstractMouseEvent() {
		super();
		startTime = System.currentTimeMillis();
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
		return mouseMoved;
	}

	public float getTime() {
		return (System.currentTimeMillis() - startTime) / 1000f;
	}

	/**
	 * Sets the mouse position as a given point.
	 * 
	 * @param current
	 *            The position.
	 */
	protected void setMousePosition(final UIPoint current) {
		if (this.position != null) {
			mouseMoved +=
					Math.abs(current.getX() - this.position.getX())
							+ Math.abs(current.getY() - this.position.getY());
		}

		this.position = current;
		if (getPhase() == PHASE_MODAL) {
			fireModalDataRefreshed();
		}
	}
}