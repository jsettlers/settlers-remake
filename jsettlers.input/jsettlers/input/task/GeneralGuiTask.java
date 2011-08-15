package jsettlers.input.task;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.input.EGuiAction;

public class GeneralGuiTask extends SimpleGuiTask {
	private static final long serialVersionUID = 1L;

	private final ISPosition2D pos;
	private final EBuildingType type;

	public GeneralGuiTask(EGuiAction guiAction, ISPosition2D pos, EBuildingType type) {
		super(guiAction);
		this.pos = pos;
		this.type = type;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public ISPosition2D getPosition() {
		return pos;
	}

	public EBuildingType getType() {
		return type;
	}

}
