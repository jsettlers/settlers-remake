package go.event.mouse;

import go.event.GOEvent;
import go.event.GOEventHandler;

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