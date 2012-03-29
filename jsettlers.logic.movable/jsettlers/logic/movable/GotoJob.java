package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * @author Andreas Eberle
 * 
 */
public class GotoJob implements Serializable {
	private static final long serialVersionUID = -4993999530134194299L;

	private final ShortPoint2D pos;

	public GotoJob(ShortPoint2D pos) {
		this.pos = pos;
	}

	public ShortPoint2D getPosition() {
		return pos;
	}

}
