package jsettlers.logic.objects.arrow;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.hex.HexGrid;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.MovableTimer;

public class ArrowObject extends AbstractHexMapObject implements IArrowMapObject, ITimerable {
	private static final float SECONDS_PER_TILE = 0.6f;

	private final ISPosition2D target;
	private final ISPosition2D source;
	private final float progressIncrease;
	private final float hitStrength;
	private short decomposeCounter = 0;

	private float progress;

	public ArrowObject(ISPosition2D pos, ISPosition2D source, float hitStrength) {
		this.target = pos;
		this.source = source;
		this.hitStrength = hitStrength;
		MovableTimer.add(this);

		progressIncrease = (float) (1 / (SECONDS_PER_TILE * Math.hypot(source.getX() - pos.getX(), source.getY() - pos.getY())));
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.ARROW;
	}

	@Override
	public float getStateProgress() {
		return progress;
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
	public void timerEvent() {
		if (decomposeCounter == 0) {
			progress += progressIncrease;

			if (progress >= 1) {
				progress = 1;
				decomposeCounter = 1;

				IHexMovable enemy = HexGrid.get().getMovable(target);
				if (enemy != null) {
					enemy.hit(hitStrength);
					kill(); // the arrow hit the enemy, so it can't be on the map
				}
			}
		} else {
			decomposeCounter++;
			if (decomposeCounter >= Constants.ARROW_DECOMPOSE_INTERRUPTS) {
				kill();
			}
		}
	}

	@Override
	public void kill() {
		MovableTimer.remove(this);
		HexGrid.get().removeMapObject(target, this);
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.getApproxDirection(source, target);
	}

	@Override
	public ISPosition2D getTarget() {
		return target;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

}
