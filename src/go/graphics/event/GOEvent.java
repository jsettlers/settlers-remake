package go.graphics.event;

/**
 * This class defines a generic go event type.
 * <p>
 * When you are notified that an event started, you may handle the event by
 * calling the setHandler-Method of the event. If the handler you gave the event
 * is accepted, event changes are forwarded to you. If your implementation does
 * not allow multiple events at the same time, you should not set a handler to
 * an event if you already handle an ongoing event.
 * <p>
 * if your event handler methods. need to be synchronized, you have to care for
 * it yourself.
 * <p>
 * After its initialization the event, changes of the event state are passed on
 * to the handler: On a phase change, the
 * {@link GOEventHandler#phaseChanged(GOEvent)} method is called. During the
 * Modal phase, the event may send additional information about the possible
 * event data it may have upon finish, e.g. a mouse position change to an
 * {@link GOModalEventHandler}.
 * <p>
 * Any final action should only be applied if the event was finished.
 * 
 * @author michael
 */
public interface GOEvent {
	/**
	 * Indicates that the event is in the initialization phase. In this pahse,
	 * the event handler is searched.
	 */
	int PHASE_INITIALIZING = 0;
	/**
	 * Indicates that the event just started.
	 */
	int PHASE_STARTED = 1;
	/**
	 * Indicates that the event is in progress.
	 */
	int PHASE_MODAL = 2;
	/**
	 * The event was finished.
	 */
	int PHASE_FINISHED = 3;

	/**
	 * The event was aborted by the user.
	 */
	int PHASE_ABORTED = 4;

	/**
	 * Sets the handler for the event.
	 * 
	 * @param handler
	 *            The handler
	 */
	void setHandler(GOEventHandler handler);
	
	/**
	 * Gets the current handler the vent has. May be null.
	 * @return Th handler.
	 */
	GOEventHandler getHandler();

	/**
	 * Gets the phase of the event.
	 * 
	 * @return The phase the event is in.
	 */
	int getPhase();
}
