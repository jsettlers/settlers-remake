package jsettlers.graphics.startscreen;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public abstract class ExecutableAction extends Action {
	public ExecutableAction() {
	    super(EActionType.EXECUTABLE);
    }

	abstract public void execute();
}
