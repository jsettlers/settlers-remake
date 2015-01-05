package go.graphics.event;

/**
 * This is a extension to normal events.
 * <p>
 * Modal events are capable of passing on data about the event while the event is on the modal phase.
 * 
 * @author michael
 */
public class SingleHandlerGoModalEvent extends SingleHandlerGoEvent {
	/**
	 * This method notifies the handler, if it supports the {@link GOModalEventHandler}, that the event data has changed.
	 */
	protected void fireModalDataRefreshed() {
		if (getHandler() instanceof GOModalEventHandler) {
			GOModalEventHandler modalHandler = (GOModalEventHandler) getHandler();
			modalHandler.eventDataChanged(this);
		}
	}
}
