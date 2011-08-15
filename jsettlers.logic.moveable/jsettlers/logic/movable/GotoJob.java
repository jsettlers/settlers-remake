package jsettlers.logic.movable;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.IJob;

public class GotoJob implements IJob {

	private final ISPosition2D pos;

	public GotoJob(ISPosition2D pos) {
		this.pos = pos;
	}

	@Override
	public ISPosition2D getFirstPos() {
		return pos;
	}

}
