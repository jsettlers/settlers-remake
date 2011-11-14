package jsettlers.input.task;

import java.util.List;

import jsettlers.common.position.ISPosition2D;
import jsettlers.input.EGuiAction;

public class MoveToGuiTask extends MovableGuiTask {
	private static final long serialVersionUID = 1L;

	private final ISPosition2D pos;

	public MoveToGuiTask(ISPosition2D pos, List<Integer> selection) {
		super(EGuiAction.MOVE_TO, selection);
		this.pos = pos;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public ISPosition2D getPosition() {
		return pos;
	}

}
