package jsettlers.logic.movable.soldiers;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.GotoJob;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public abstract class AbstractSoldierStrategy extends PathableStrategy implements IBuildingOccupyableMovable {
	private static final long serialVersionUID = 9000857936712315432L;

	private final EMovableType type;

	private int delayCtr = Integer.MAX_VALUE;
	private ISPosition2D enemyPos;

	private IOccupyableBuilding tower = null;
	private ESoldierState state = ESoldierState.WATCHING;

	protected AbstractSoldierStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable);
		this.type = type;
	}

	@Override
	protected void gotHitEvent() {
		super.abortPath();
	}

	protected abstract short getSearchRadius();

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			switch (state) {
			case WATCHING:
				checkEnemies();
				break;
			case IN_TOWER:
				break;

			default:
				System.err.println("AbstractSoldierStrategy.noActionEvent(): state=" + state);
				break;
			}
		}
		return true;
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			switch (state) {
			case WATCHING:
				checkEnemies();
				break;

			default:
				System.out.println("AbstractSoldierStrategy.actionFinished(): state=" + state);
				super.setAction(EAction.NO_ACTION, -1);
				break;
			}
		}

		return true;
	}

	@Override
	protected void pathFinished() {
		switch (state) {
		case WATCHING:
			super.setAction(EAction.NO_ACTION, -1);
			break;
		case GO_TO_TOWER:
			state = ESoldierState.IN_TOWER;
			super.setVisible(false);
			super.setSleeping(true);
			tower.setSoldier(this);
			break;
		}
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

	private void checkForEnemies() {
		if (enemyPos != null || delayCtr > Constants.MOVABLE_INTERRUPTS_PER_SECOND * 2) {
			delayCtr = 0;

			Path path = super.getGrid().getDijkstra()
					.find(this, super.getPos().getX(), super.getPos().getY(), (short) 1, getSearchRadius(), ESearchType.ENEMY);
			if (path != null)
				enemyPos = path.getTargetPos();
			else
				enemyPos = null;
		} else {
			delayCtr++;
			enemyPos = null;
		}
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
			if (super.getGrid().isBlocked(this, nextPos.getX(), nextPos.getY())) {
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
	public EMovableType getMovableType() {
		return type;
	}

	@Override
	protected boolean isGotoJobable() {
		return state != ESoldierState.GO_TO_TOWER;
	}

	@Override
	protected void pathRequestFailed() {
		switch (state) {
		case GO_TO_TOWER:
			tower = null;
			state = ESoldierState.WATCHING;
			System.out.println("path request failed");
			break;
		}
	}

	@Override
	protected boolean isPathStopable() {
		return state != ESoldierState.GO_TO_TOWER;
	}

	@Override
	public void setOccupyableBuilding(IOccupyableBuilding building) {
		super.setGotoJob(new GotoJob(building.getDoor()));
		this.tower = building;
		this.state = ESoldierState.GO_TO_TOWER;
	}

	@Override
	protected final void killedEvent() {
		if (tower != null) {
			tower.requestFailed(getMovableType());
		}
	}

	@Override
	public final boolean canOccupyBuilding() {
		return tower == null;
	}

	@Override
	public final Movable getMovable() {
		return super.getMovable();
	}

	/**
	 * enum to define the states of a soldier.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private static enum ESoldierState {
		GO_TO_TOWER,
		WATCHING,
		IN_TOWER
	}

}
