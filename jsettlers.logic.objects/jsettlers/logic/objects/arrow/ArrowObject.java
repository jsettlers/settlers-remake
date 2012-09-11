package jsettlers.logic.objects.arrow;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.objects.ProgressingSoundableObject;

public final class ArrowObject extends ProgressingSoundableObject implements IArrowMapObject {
	private static final long serialVersionUID = 1702902724559733166L;

	private static final float SECONDS_PER_TILE = 0.024f;
	public static final float MIN_DECOMPOSE_DELAY = 60;

	private final short sourceX;
	private final short sourceY;
	private final float hitStrength;

	private final IArrowAttackableGrid grid;

	public ArrowObject(IArrowAttackableGrid grid, ShortPoint2D targetPos, ShortPoint2D shooterPos, float hitStrength) {
		super(targetPos);
		this.grid = grid;

		this.sourceX = shooterPos.getX();
		this.sourceY = shooterPos.getY();
		this.hitStrength = hitStrength;

		super.setDuration((float) (SECONDS_PER_TILE * Math.hypot(shooterPos.getX() - targetPos.getX(), shooterPos.getY() - targetPos.getY())));
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
		grid.hitWithArrowAt(this);
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
		return super.getX();
	}

	@Override
	public short getTargetY() {
		return super.getY();
	}

	public float getHitStrength() {
		return hitStrength;
	}
}
