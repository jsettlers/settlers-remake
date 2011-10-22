package go.graphics.swing.event.swingInterpreter;

import go.graphics.UIPoint;
import go.graphics.event.SingleHandlerGoModalEvent;
import go.graphics.event.mouse.GOPanEvent;

public class PseudoPanEvent extends SingleHandlerGoModalEvent implements GOPanEvent {

	private final UIPoint distance;

	public PseudoPanEvent(int x, int y) {
	    this.distance = new UIPoint(x, y);
    }

	@Override
	public UIPoint getPanCenter() {
		return new UIPoint(0, 0);
	}

	@Override
	public UIPoint getPanDistance() {
		return distance;
	}
	
	public void pan() {
	    this.setPhase(PHASE_STARTED);
	    this.setPhase(PHASE_MODAL);
	    this.setPhase(PHASE_FINISHED);
	}
}
