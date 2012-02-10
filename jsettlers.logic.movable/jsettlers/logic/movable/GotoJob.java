package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.common.position.ISPosition2D;

/**
 * @author Andreas Eberle
 * 
 */
public class GotoJob implements Serializable {
	private static final long serialVersionUID = -4993999530134194299L;

	private final ISPosition2D pos;

	public GotoJob(ISPosition2D pos) {
		this.pos = pos;
	}

	public ISPosition2D getPosition() {
		return pos;
	}

}
