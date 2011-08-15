package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;
import random.RandomSingleton;

/**
 * This is an abstract class used for growing objects.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class GrowingObject extends AbstractHexMapObject implements ITimerable {
	private float progress = 0;

	private EMapObjectType state;

	private final EMapObjectType growing;
	private final EMapObjectType adult;
	private final EMapObjectType dead;

	private final IMapObjectRemovableGrid grid;

	protected GrowingObject(IMapObjectRemovableGrid grid, EMapObjectType growing, EMapObjectType adult, EMapObjectType dead) {
		this.grid = grid;
		this.growing = growing;
		this.adult = adult;
		this.dead = dead;

		Timer100Milli.add(this);
		state = growing;
		progress = (float) (RandomSingleton.nextD() * 0.1);
	}

	@Override
	public void timerEvent() {
		if (state == growing) {
			progress += getGrowthIncrease();
			if (progress >= 1) {
				state = adult;
				progress = 0;
			}
		} else if (state == adult) {
		} else {
			progress += Constants.TREE_DECOMPOSE_PER_INTERRUPT;

			if (progress >= 1) {
				kill();
			}
		}
	}

	protected abstract float getGrowthIncrease();

	public boolean isDead() {
		return state == dead;
	}

	public boolean isAdult() {
		return state == adult;
	}

	@Override
	public boolean canBeCut() {
		return isAdult();
	}

	@Override
	public boolean cutOff() {
		this.state = dead;
		this.progress += 0.1F;
		return false;
	}

	@Override
	public void kill() {
		Timer100Milli.remove(this);
		grid.removeMapObject(pos, this);
	}

	@Override
	public EMapObjectType getObjectType() {
		return state;
	}

	@Override
	public float getStateProgress() {
		return progress;
	}

}
