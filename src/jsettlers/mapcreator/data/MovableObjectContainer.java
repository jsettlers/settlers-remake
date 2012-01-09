package jsettlers.mapcreator.data;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

public class MovableObjectContainer implements ObjectContainer, IMovable {

	private final MovableObject movableObject;
	private ShortPoint2D pos;

	public MovableObjectContainer(MovableObject movableObject, int x, int y) {
		this.movableObject = movableObject;
		this.pos = new ShortPoint2D(x, y);
	}

	@Override
	public MapObject getMapObject() {
		return movableObject;
	}

	@Override
	public byte getPlayer() {
		return movableObject.getPlayer();
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	public EMovableType getMovableType() {
		return movableObject.getType();
	}

	@Override
	public EAction getAction() {
		return EAction.NO_ACTION;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.SOUTH_WEST;
	}

	@Override
	public float getMoveProgress() {
		return 0;
	}

	@Override
	public EMaterialType getMaterial() {
		return EMaterialType.NO_MATERIAL;
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	@Override
	public float getHealth() {
		return 1;
	}

	@Override
	public boolean isRightstep() {
		return false;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] { new RelativePoint(0, 0) };
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return true;
	}

}
