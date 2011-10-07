package go.graphics.opengl.event.swingInterpreter;

import go.graphics.event.SingleHandlerGoModalEvent;
import go.graphics.event.mouse.GOPanEvent;

import java.awt.Point;

public class PseudoPanEvent extends SingleHandlerGoModalEvent implements GOPanEvent {

	private final Point distance;

	public PseudoPanEvent(int x, int y) {
	    this.distance = new Point(x, y);
    }

	@Override
	public Point getPanCenter() {
		return new Point(0, 0);
	}

	@Override
	public Point getPanDistance() {
		return distance;
	}
	
	public void pan() {
	    this.setPhase(PHASE_STARTED);
	    this.setPhase(PHASE_MODAL);
	    this.setPhase(PHASE_FINISHED);
	}
}
