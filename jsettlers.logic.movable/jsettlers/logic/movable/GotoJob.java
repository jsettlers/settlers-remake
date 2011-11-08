package jsettlers.logic.movable;

import jsettlers.common.position.ISPosition2D;

public class GotoJob {

	private final ISPosition2D pos;

	public GotoJob(ISPosition2D pos) {
		this.pos = pos;
	}

	public ISPosition2D getPosition() {
		return pos;
	}

}
