package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.constants.Constants;
import random.RandomSingleton;

/**
 * 
 * @author Andreas Eberle
 * 
 */
class WatchingBehavior extends SoldierBehavior implements IFightingBehaviorUser {
	private static final long serialVersionUID = 8738428286789781287L;

	WatchingBehavior(ISoldierBehaviorable soldier) {
		super(soldier);
		super.setAction(EAction.NO_ACTION, -1);

		this.delayCtr = RandomSingleton.getInt(0, DELAY / 2);
	}

	static final int DELAY = Constants.MOVABLE_INTERRUPTS_PER_SECOND * 3;
	private static final short SEARCH_RADIUS = 15;
	private static final int BOWMAN_FIRE_RADIUS = 18;

	private int delayCtr;
	private boolean enemyFoundLastTime = false;

	private static long sum = 0;
	private static int ctr = 0;

	@Override
	public SoldierBehavior calculate(ISPosition2D pos, IPathCalculateable pathCalcable) {
		if (enemyFoundLastTime || delayCtr > DELAY) {
			delayCtr = 0;

			StopWatch watch = new MilliStopWatch();
			watch.start();
			Path path = super.getGrid().getDijkstra().find(pathCalcable, pos.getX(), pos.getY(), (short) 1, SEARCH_RADIUS, ESearchType.ENEMY);
			watch.stop();
			sum += watch.getDiff();
			ctr++;
			if (ctr >= 10000) {
				System.out.println("soldier searches needed in avg: " + ((float) sum) / ctr);
				sum = 0;
				ctr = 0;
			}

			if (path != null) {
				enemyFoundLastTime = true;
				if (canHit(pos, path.getTargetPos())) {
					super.setAction(EAction.NO_ACTION, -1);
					return new FightingBehavior(getSoldier(), path.getTargetPos(), this);
				} else {
					super.goToTile(path.getFirst());
					return this;
				}
			} else {
				enemyFoundLastTime = false;
				super.setAction(EAction.NO_ACTION, -1);
				return this;
			}
		} else {
			super.setAction(EAction.NO_ACTION, -1);
			delayCtr++;
			return this;
		}
	}

	private boolean canHit(ISPosition2D pos, ISPosition2D enemyPos) {
		switch (super.soldier.getSoldierType()) {
		case BOWMAN:
			return Math.hypot(pos.getX() - enemyPos.getX(), pos.getY() - enemyPos.getY()) <= BOWMAN_FIRE_RADIUS;
		case INFANTARY:
			return EDirection.getDirection(pos, enemyPos) != null;
		default:
			return false;
		}
	}

	@Override
	public void pathRequestFailed() {
	}

	@Override
	public SoldierBehavior getFinishedBehavior() {
		return this;
	}

	@Override
	public void setFinishedMovableAction() {
		super.setAction(EAction.NO_ACTION, -1);
	}

}
