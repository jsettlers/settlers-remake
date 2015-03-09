package jsettlers.logic.map.newGrid.objects;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class AbstractObjectsManagerObject extends AbstractHexMapObject implements Serializable {
	private static final long serialVersionUID = 6013184372588966504L;

	private final short x;
	private final short y;

	protected AbstractObjectsManagerObject(ShortPoint2D pos) {
		this.x = pos.x;
		this.y = pos.y;
	}

	protected abstract void changeState();

	public final short getX() {
		return x;
	}

	public final short getY() {
		return y;
	}

}
