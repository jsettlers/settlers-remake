package jsettlers.logic;

import java.io.Serializable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * Created by Rudolf Polzer
 */

public class DockPosition implements Serializable {
	private static final long serialVersionUID = 1142611313386931880L;

	private ShortPoint2D coastPosition = null;
	private EDirection direction = null;

	public DockPosition(ShortPoint2D coastPosition, EDirection direction) {
		this.coastPosition = coastPosition;
		this.direction = direction;
	}

	public ShortPoint2D getPosition() {
		return this.coastPosition;
	}

	public EDirection getDirection() {
		return this.direction;
	}
}
