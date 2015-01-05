package jsettlers.graphics.map;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GOPanEvent;

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
		context.setPanProgress(this, new UIPoint(0, 0));
	}

	@Override
	public void aborted(GOEvent event) {
		this.context.finishPanProgress(this, new UIPoint(0, 0));
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
