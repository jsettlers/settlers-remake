package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is an abstract class used for growing objects.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class GrowingObject extends ProgressingObject {
	private static final long serialVersionUID = -5886720986614326428L;

	private EMapObjectType state;

	protected GrowingObject(ShortPoint2D pos, EMapObjectType growing) {
		super(pos);

		this.state = growing;
		super.setDuration(getGrowthDuration());
	}

	protected abstract float getGrowthDuration();

	public boolean isDead() {
		return this.state == getDeadState();
	}

	protected abstract EMapObjectType getDeadState();

	public boolean isAdult() {
		return this.state == getAdultState();
	}

	protected abstract EMapObjectType getAdultState();

	@Override
	public boolean canBeCut() {
		return isAdult();
	}

	@Override
	public boolean cutOff() {
		super.setDuration(getDecomposeDuration());
		this.state = getDeadState();
		return true;
	}

	protected abstract float getDecomposeDuration();

	@Override
	public EMapObjectType getObjectType() {
		return this.state;
	}

	@Override
	protected void changeState() {
		if (state == getAdultState()) {
			state = getDeadState();
		} else if (state == getDeadState()) {
		} else {
			state = getAdultState();
		}
	}
}
