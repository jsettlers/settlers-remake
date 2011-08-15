package jsettlers.graphics.map;

import go.event.GOEvent;
import go.event.GOModalEventHandler;
import go.event.mouse.GOPanEvent;

import java.awt.Point;

/**
 * This is a pan handler. It just saves the pan status for polling.
 * <p>
 * Pan handlers may only be added to pan events.
 * 
 * @author michael
 */
public class PanHandler implements GOModalEventHandler {

	private final ScreenPosition context;

	/**
	 * Creates a new pan handler.
	 * 
	 * @param context
	 *            The screen position this handler should be apply to.
	 */
	public PanHandler(ScreenPosition context) {
		this.context = context;
		context.setPanProgress(this, new Point(0, 0));
	}

	@Override
	public void aborted(GOEvent event) {
		this.context.finishPanProgress(this, new Point(0, 0));
	}

	@Override
	public void finished(GOEvent event) {
		this.context.finishPanProgress(this, ((GOPanEvent) event)
		        .getPanDistance());
	}

	@Override
	public void phaseChanged(GOEvent event) {

	}

	@Override
	public void eventDataChanged(GOEvent event) {
		this.context
		        .setPanProgress(this, ((GOPanEvent) event).getPanDistance());
	}
}
