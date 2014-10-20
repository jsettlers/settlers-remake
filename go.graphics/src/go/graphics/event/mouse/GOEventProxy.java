package go.graphics.event.mouse;

import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;

/**
 * This is the basic event proxy.
 * @author michael
 *
 * @param <T> The event type to proxy
 */
public class GOEventProxy<T extends GOEvent> implements GOEvent {

	protected final T baseEvent;

	public GOEventProxy(T baseEvent) {
		this.baseEvent = baseEvent;
	}

	public GOEventHandler getHandler() {
    	return this.baseEvent.getHandler();
    }

	public int getPhase() {
    	return this.baseEvent.getPhase();
    }

	public void setHandler(GOEventHandler handler) {
    	this.baseEvent.setHandler(new EventHandlerProxy(this, handler));
    }

}