package jsettlers.graphics.startscreen;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

/**
 * This is an action tha can be executed.
 * @author michael
 *
 */
public abstract class ExecutableAction extends Action {
	public ExecutableAction() {
	    super(EActionType.EXECUTABLE);
    }

	abstract public void execute();
}
