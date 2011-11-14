package jsettlers.input.task;

import jsettlers.common.position.ISPosition2D;
import jsettlers.input.EGuiAction;

public class DestroyBuildingAction extends SimpleGuiTask {
	private static final long serialVersionUID = 3607849657705611288L;
	private final ISPosition2D position;

	public DestroyBuildingAction(ISPosition2D position) {
		super(EGuiAction.DESTROY_BUILDING);
		this.position = position;
	}

	@Override
	public void execute() {
		TaskExecutor.get().executeAction(this);
	}

	public ISPosition2D getPosition() {
		return position;
	}
}
