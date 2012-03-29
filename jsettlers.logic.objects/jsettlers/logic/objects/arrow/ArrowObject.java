package jsettlers.logic.objects.arrow;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
import jsettlers.logic.objects.ProgressingSoundableObject;

public final class ArrowObject extends ProgressingSoundableObject implements IArrowMapObject {
	private static final long serialVersionUID = 1702902724559733166L;

	private static final float SECONDS_PER_TILE = 0.03f;
	public static final float DECOMPOSE_DELAY = 60;

	private final short sourceX;
	private final short sourceY;
	private final float hitStrength;

	private final IHexMovable target;

	public ArrowObject(IHexMovable target, ShortPoint2D source, float hitStrength) {
		super(target.getPos());
		this.target = target;

		this.sourceX = source.getX();
		this.sourceY = source.getY();
		this.hitStrength = hitStrength;

		super.setDuration((float) (SECONDS_PER_TILE * Math.hypot(source.getX() - target.getPos().getX(), source.getY() - target.getPos().getY())));
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.ARROW;
	}

	@Override
	public boolean cutOff() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.getApproxDirection(getSourceX(), getSourceY(), getTargetX(), getTargetY());
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		if (target.getPos().getX() == getTargetX() && target.getPos().getY() == getTargetY()) {
			target.hit(hitStrength);
		}
	}

	@Override
	public short getSourceX() {
		return sourceX;
	}

	@Override
	public short getSourceY() {
		return sourceY;
	}

	@Override
	public short getTargetX() {
		return target.getPos().getX();
	}

	@Override
	public short getTargetY() {
		return target.getPos().getY();
	}
}
