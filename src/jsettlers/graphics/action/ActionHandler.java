package jsettlers.graphics.action;

import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;


/**
 * This is a go event handler that preforms an action when the event is fired.
 * @author michael
 *
 */
public class ActionHandler implements GOEventHandler {

	private final Action action;
	private final ActionFireable connector;

	/**
	 * This is an action handler converts go events to actions.
	 * @param action The action
	 * @param connector The event to fire the action for.
	 */
	public ActionHandler(Action action, ActionFireable connector) {
		this.action = action;
		this.connector = connector;
    }

	@Override
	public void aborted(GOEvent event) {
	}

	@Override
	public void finished(GOEvent event) {
		this.connector.fireAction(this.action);
	}

	@Override
	public void phaseChanged(GOEvent event) {
	}

}
