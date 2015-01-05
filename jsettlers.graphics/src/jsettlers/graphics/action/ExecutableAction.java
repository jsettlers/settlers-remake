package jsettlers.graphics.action;

/**
 * This is an action tha can be executed.
 * 
 * @author michael
 *
 */
public abstract class ExecutableAction extends Action {
	public ExecutableAction() {
		super(EActionType.EXECUTABLE);
	}

	abstract public void execute();
}
