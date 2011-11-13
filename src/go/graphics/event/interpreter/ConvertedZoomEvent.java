package go.graphics.event.interpreter;

import go.graphics.event.mouse.GOZoomEvent;

public class ConvertedZoomEvent extends AbstractMouseEvent implements GOZoomEvent {

	private float zoom;

	@Override
	public float getZoomFactor() {
		return zoom;
	}
	
	public void setZoomFactor(float factor) {
		zoom = factor;
		fireModalDataRefreshed();
	}

}
