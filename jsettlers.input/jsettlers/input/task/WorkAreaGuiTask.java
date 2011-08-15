package jsettlers.input.task;

import jsettlers.common.position.ISPosition2D;
import jsettlers.input.EGuiAction;

public class WorkAreaGuiTask extends SimpleGuiTask {
	private static final long serialVersionUID = 1L;

	private final ISPosition2D pos;
	private final ISPosition2D buildingPos;

	public WorkAreaGuiTask(EGuiAction guiAction, ISPosition2D pos, ISPosition2D buildingPos) {
		super(guiAction);
		this.pos = pos;
		this.buildingPos = buildingPos;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public ISPosition2D getPosition() {
		return pos;
	}

	public ISPosition2D getBuildingPos() {
		return buildingPos;
	}

}
