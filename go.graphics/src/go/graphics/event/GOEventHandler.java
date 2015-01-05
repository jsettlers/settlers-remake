package go.graphics.event;

/**
 * Objects that implement this class are capable of handling state changes that came from an event.
 * 
 * @author michael
 */
public interface GOEventHandler {
	/**
	 * This method is called when ever the phase of an event changes.
	 * 
	 * @param event
	 *            the Event.
	 */
	void phaseChanged(GOEvent event);

	/**
	 * This method gets called when the event is finshed.
	 * 
	 * @param event
	 *            the Event.
	 */
	void finished(GOEvent event);

	/**
	 * This method gets called when the event is aborted.
	 * 
	 * @param event
	 *            the Event.
	 */
	void aborted(GOEvent event);
}
