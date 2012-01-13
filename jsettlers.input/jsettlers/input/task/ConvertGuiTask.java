package jsettlers.input.task;

import java.util.List;

import jsettlers.common.movable.EMovableType;
import jsettlers.input.EGuiAction;

public class ConvertGuiTask extends MovableGuiTask {
	private static final long serialVersionUID = 1L;
	private final EMovableType targetType;

	public ConvertGuiTask(List<Integer> selection, EMovableType targetType) {
		super(EGuiAction.CONVERT, selection);
		this.targetType = targetType;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public EMovableType getTargetType() {
		return targetType;
	}

}
