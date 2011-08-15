package jsettlers.input.task;

import java.util.List;

import jsettlers.common.position.ISPosition2D;
import jsettlers.input.EGuiAction;

public class MoveToGuiTask extends SimpleGuiTask {
	private static final long serialVersionUID = 1L;

	private final ISPosition2D pos;
	private final List<Integer> selection;

	public MoveToGuiTask(EGuiAction guiAction, ISPosition2D pos, List<Integer> selection) {
		super(guiAction);
		this.pos = pos;
		this.selection = selection;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public ISPosition2D getPosition() {
		return pos;
	}

	public List<Integer> getSelection() {
		return selection;
	}

}
