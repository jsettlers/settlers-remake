package jsettlers.logic.objects.arrow;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.objects.ProgressingObject;

public class ArrowObject extends ProgressingObject implements IArrowMapObject {
	private static final long serialVersionUID = 1702902724559733166L;

	private static final float SECONDS_PER_TILE = 0.03f;

	public static final float DECOMPOSE_DELAY = 60;

	private final ISPosition2D source;
	private final float hitStrength;

	private final IHexMovable target;

	public ArrowObject(IHexMovable target, ISPosition2D source, float hitStrength) {
		super(target.getPos());
		this.target = target;

		this.source = source;
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
	public ISPosition2D getSource() {
		return source;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.getApproxDirection(source, super.getPos());
	}

	@Override
	public ISPosition2D getTarget() {
		return super.getPos();
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		if (target.getPos().equals(this.getTarget())) {
			target.hit(hitStrength);
		}
	}
}
