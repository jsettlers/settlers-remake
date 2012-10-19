package jsettlers.graphics.map.controls.mobile;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;

public class NavigationPointDrawHandler implements GOModalEventHandler {

	private NavigationPoint point;

	NavigationPointDrawHandler(NavigationPoint point) {
		this.point = point;
	}

	@Override
	public void phaseChanged(GOEvent event) {
	}

	@Override
	public void finished(GOEvent event) {
		point.abortPanning();
	}

	@Override
	public void aborted(GOEvent event) {
		point.abortPanning();
	}

	@Override
	public void eventDataChanged(GOEvent event) {
		UIPoint current = ((GODrawEvent) event).getDrawPosition();
		point.setPanningPosition(current);
	}

}
