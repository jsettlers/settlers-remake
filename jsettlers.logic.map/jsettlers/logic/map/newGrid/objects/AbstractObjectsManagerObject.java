package jsettlers.logic.map.newGrid.objects;

import java.io.Serializable;

import jsettlers.common.position.ISPosition2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class AbstractObjectsManagerObject extends AbstractHexMapObject implements Serializable {
	private static final long serialVersionUID = 6013184372588966504L;

	private final short x;
	private final short y;

	protected AbstractObjectsManagerObject(ISPosition2D pos) {
		this.x = pos.getX();
		this.y = pos.getY();
	}

	protected abstract void changeState();

	public final short getX() {
		return x;
	}

	public final short getY() {
		return y;
	}

}
