package jsettlers.common.buildings;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.RelativePoint;

/**
 * This is the bricklayer position for building a building.
 * 
 * @author michael
 */
public class RelativeBricklayer {
	private final EDirection direction;
	private final RelativePoint position;

	public RelativeBricklayer(int dx, int dy, EDirection direction) {
		this.direction = direction;
		this.position = new RelativePoint(dx, dy);
	}

	public EDirection getDirection() {
	    return direction;
    }

	public RelativePoint getPosition() {
	    return position;
    }
}
