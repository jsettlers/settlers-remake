package go.graphics.event;

/**
 * This is a go key event.
 * @author michael
 *
 */
public class GOKeyEvent extends SingleHandlerGoEvent {
	private final String keyCode;

	/**
	 * Creates a new key event for a given key code.
	 * @param keyCode The key code.
	 */
	public GOKeyEvent(String keyCode) {
		this.keyCode = keyCode;
	}

	/**
	 * Gets the key code the event has.
	 * @return The key code.
	 */
	public String getKeyCode() {
	    return keyCode;
    }

	/**
	 * Called when the key is released, finishes the event.
	 * @throws IllegalStateException if the event was not started yet.
	 */
	public void released() {
		if (getPhase() != PHASE_MODAL) {
			throw new IllegalStateException("Key event in wrong state to be ended.");
		}
	    setPhase(PHASE_FINISHED);
    }

	/**
	 * starts the event.
	 * @throws IllegalStateException if the event was already started.
	 */
	public void started() {
		if (getPhase() != PHASE_INITIALIZING) {
			throw new IllegalStateException("key event may not be started twice.");
		}
	    this.setPhase(PHASE_STARTED);
	    this.setPhase(PHASE_MODAL);
    }
}
