package jsettlers.logic.movable.soldiers;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public abstract class AbstractSoldierStrategy extends PathableStrategy {

	private int delayCtr = Integer.MAX_VALUE;
	private ISPosition2D enemyPos;
	private final EMovableType type;

	protected AbstractSoldierStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable);
		this.type = type;
	}

	private void checkForEnemies() {
		if (enemyPos != null || delayCtr > Constants.MOVABLE_INTERRUPTS_PER_SECOND * 2) {
			delayCtr = 0;

			enemyPos = super.getGrid().getDijkstra().find(this, super.getPos().getX(), super.getPos().getY(), getSearchRadius(), ESearchType.ENEMY);
		} else {
			delayCtr++;
			enemyPos = null;
		}
	}

	@Override
	protected void gotHitEvent() {
		super.abortPath();
	}

	protected abstract short getSearchRadius();

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			checkEnemies();
		}
		return true;
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			checkEnemies();
		}

		return true;
	}

	private void checkEnemies() {
		checkForEnemies();

		if (enemyPos != null) {
			if (!canHit(enemyPos)) {
				EDirection enemyDir = EDirection.getApproxDirection(super.getPos(), enemyPos);
				if (enemyDir != null) {
					ISPosition2D nextPos = getNextPos(enemyDir);

					if (nextPos != null) {
						super.goToTile(nextPos);
						return;
					}
				}
			} else {
				executeHit(enemyPos);
				return;
			}
		}
		super.setAction(EAction.NO_ACTION, -1);
	}

	protected abstract boolean canHit(ISPosition2D enemyPos);

	protected abstract void executeHit(ISPosition2D enemyPos);

	/**
	 * Calculates the next position in the given direction.<br>
	 * If the next position is blocked, a neighbor direction will be taken.<br>
	 * If all neighbor tiles are blocked, this method returns null.
	 * 
	 * @param dir
	 * @return
	 */
	private ISPosition2D getNextPos(EDirection dir) {
		ISPosition2D nextPos;
		int ctr = 0; // to prevent endless loop
		do {
			nextPos = dir.getNextHexPoint(super.getPos());
			if (super.getGrid().isBlocked(nextPos.getX(), nextPos.getY())) {
				dir = dir.getNeighbor(-1);
				nextPos = null;
			}
			ctr++;
		} while (nextPos == null && ctr < 6);
		return nextPos;
	}

	@Override
	public boolean needsPlayersGround() {
		return false;
	}

	@Override
	protected void pathFinished() {
		super.setAction(EAction.NO_ACTION, -1);
		// nothing to do here
	}

	@Override
	protected EMovableType getMovableType() {
		return type;
	}

	@Override
	protected boolean isGotoJobable() {
		return true;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		if (stop) {
			super.abortPath();
		}
	}

	@Override
	protected void pathRequestFailed() {
	}
}
