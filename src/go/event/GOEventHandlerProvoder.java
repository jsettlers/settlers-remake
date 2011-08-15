package go.event;

/**
 * This is what most systems call a event listener. When a event is created, it
 * is notified about the event and may register an event handler for that
 * specific event. It is not guaranteed that the handler is used, so the handler
 * should wait for the start-Event to handle the event.
 * 
 * @author michael
 */
public interface GOEventHandlerProvoder {
	/**
	 * Asks for a handler to the event.
	 * 
	 * @param event
	 *            The event that is created. It is in initializing phase.
	 */
	void handleEvent(GOEvent event);
}
