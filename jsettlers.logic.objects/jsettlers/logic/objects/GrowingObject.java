package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;

/**
 * This is an abstract class used for growing objects.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class GrowingObject extends ProgressingObject {
	private static final long serialVersionUID = -5886720986614326428L;

	private EMapObjectType state;

	private final EMapObjectType adult;
	private final EMapObjectType dead;

	protected GrowingObject(ISPosition2D pos, EMapObjectType growing, EMapObjectType adult, EMapObjectType dead) {
		super(pos);
		this.adult = adult;
		this.dead = dead;

		this.state = growing;
		super.setDuration(getGrowthDuration());
	}

	protected abstract float getGrowthDuration();

	public boolean isDead() {
		return this.state == dead;
	}

	public boolean isAdult() {
		return this.state == adult;
	}

	@Override
	public boolean canBeCut() {
		return isAdult();
	}

	@Override
	public boolean cutOff() {
		super.setDuration(getDecomposeDuration());
		this.state = dead;
		return true;
	}

	protected abstract float getDecomposeDuration();

	@Override
	public EMapObjectType getObjectType() {
		return this.state;
	}

	@Override
	protected void changeState() {
		if (state == adult) {
			state = dead;
		} else if (state == dead) {
		} else {
			state = adult;
		}
	}
}
