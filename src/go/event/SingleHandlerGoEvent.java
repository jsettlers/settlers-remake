package go.event;

/**
 * This class provides simple handling of events. It allows the addition of a
 * event handler and basic phase support with event fireing.
 * 
 * @author michael
 */
public class SingleHandlerGoEvent implements GOEvent {
	private GOEventHandler handler = null;

	private int phase = PHASE_INITIALIZING;

	@Override
	public void setHandler(GOEventHandler handler) {
		if (getPhase() != PHASE_INITIALIZING) {
			throw new IllegalStateException(
			        "Can only set event handler in initialization pahse.");
		}
		this.handler = handler;
	}

	@Override
	public GOEventHandler getHandler() {
		return this.handler;
	}

	/**
	 * Sets the phase of the current event. This should not be used by the event
	 * handler.
	 * <p>
	 * Fires a event phase chnage.
	 * 
	 * @param phase
	 *            The phase.
	 */
	protected void setPhase(int phase) {
		if (phase < this.phase) {
			throw new IllegalStateException("Cannot go backwards in states");
		}
		this.phase = phase;
		
		if (this.handler != null) {
			this.handler.phaseChanged(this);

			if (phase == PHASE_ABORTED) {
				this.handler.aborted(this);
			} else if (phase == PHASE_FINISHED) {
				this.handler.finished(this);
			}
		}
	}
	
	@Override
	public int getPhase() {
	    return this.phase;
    }
}
