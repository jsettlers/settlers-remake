package jsettlers.input.task;

import java.util.List;

import jsettlers.input.EGuiAction;

public class MovableGuiTask extends SimpleGuiTask {
	private static final long serialVersionUID = 1L;

	private final List<Integer> selection;

	public MovableGuiTask(EGuiAction action, List<Integer> selection) {
		super(action);
		this.selection = selection;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public List<Integer> getSelection() {
		return selection;
	}

}
