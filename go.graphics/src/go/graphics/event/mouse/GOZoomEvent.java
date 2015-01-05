package go.graphics.event.mouse;

import go.graphics.event.GOEvent;

public interface GOZoomEvent extends GOEvent {
	/**
	 * Gets the zoom factor
	 * 
	 * @return A float. 1 means no zoom, small values mean smaller, big values mean bigger.
	 */
	float getZoomFactor();
}
