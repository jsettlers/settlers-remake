package go.graphics.event.mouse;

import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOModalEventHandler;

/**
 * This method proxys a mouse event handler.
 * <p>
 * It is a normal event handler, that porxys all events that were recived to the
 * given event handler and just changes the vent target.
 * <p>
 * It supports unmodal and modal, events.
 * 
 * @author michael
 */
public class EventHandlerProxy implements GOModalEventHandler {
	private final GOEvent eventToProxy;
	private final GOEventHandler handler;

	/**
	 * Creates a new mouse event proxy.
	 * 
	 * @param eventToProxy The event that is forwarded.
	 * @param handler The handler to send event changes to.
	 */
	protected EventHandlerProxy(GOEvent eventToProxy,
	        GOEventHandler handler) {
		this.eventToProxy = eventToProxy;
		this.handler = handler;
	}

	@Override
	public void aborted(GOEvent event) {
		this.handler.aborted(this.eventToProxy);
	}

	@Override
	public void finished(GOEvent event) {
		this.handler.finished(this.eventToProxy);
	}

	@Override
	public void phaseChanged(GOEvent event) {
		this.handler.phaseChanged(this.eventToProxy);
	}

	@Override
    public void eventDataChanged(GOEvent event) {
	    if (this.handler instanceof GOModalEventHandler) {
	    	((GOModalEventHandler) this.handler).eventDataChanged(this.eventToProxy);
	    }
    }
}
