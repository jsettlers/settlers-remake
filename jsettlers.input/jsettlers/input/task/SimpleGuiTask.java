package jsettlers.input.task;

import jsettlers.input.EGuiAction;
import synchronic.task.ITask;

public class SimpleGuiTask implements ITask {
	private static final long serialVersionUID = 1L;

	private final EGuiAction guiAction;

	public SimpleGuiTask(EGuiAction guiAction) {
		this.guiAction = guiAction;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public EGuiAction getGuiAction() {
		return guiAction;
	}

}
