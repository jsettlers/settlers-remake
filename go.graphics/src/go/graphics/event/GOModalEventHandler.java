package go.graphics.event;

/**
 * This interface defines a handler that is capable of handling {@link GOModalEvent}s.
 * 
 * @author michael
 */
public interface GOModalEventHandler extends GOEventHandler {
	/**
	 * This method is notified if the event data was changed.
	 * 
	 * @param event
	 *            The event data.
	 */
	void eventDataChanged(GOEvent event);
}
