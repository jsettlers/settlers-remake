package jsettlers.input.task;

import java.util.List;

import jsettlers.input.EGuiAction;

public class DestroyMovablesAction extends SimpleGuiTask {
	private static final long serialVersionUID = 3607849657705611288L;

	private final List<Integer> selection;

	public DestroyMovablesAction(List<Integer> selection) {
		super(EGuiAction.DESTROY_MOVABLES);
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
