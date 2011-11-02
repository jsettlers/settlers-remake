package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.movable.bearer.BearerStrategy;
import jsettlers.logic.movable.construction.BricklayerStrategy;
import jsettlers.logic.movable.construction.DiggerStrategy;
import jsettlers.logic.movable.soldiers.BowmanStrategy;
import jsettlers.logic.movable.soldiers.PikemanStrategy;
import jsettlers.logic.movable.soldiers.SwordsmanStrategy;
import jsettlers.logic.movable.specialists.PioneerStrategy;
import jsettlers.logic.movable.workers.BuildingWorkerStrategy;

/**
 * This is the strategy that tells a movable the next steps it has to do.
 * 
 * @author michael
 * 
 */
public abstract class MovableStrategy {
	protected final Movable movable;
	private final IMovableGrid grid;

	/**
	 * Creates a new strategy.
	 * 
	 * @param grid
	 * 
	 * @param movable
	 */
	protected MovableStrategy(IMovableGrid grid, Movable movable) {
		this.grid = grid;
		this.movable = movable;
	}

	protected abstract EMovableType getMovableType();

	/**
	 * Gets the default strategy for the given type of worker.
	 * 
	 * @param grid
	 * 
	 * @param type
	 * @param movable
	 * @return
	 */
	static MovableStrategy getTypeStrategy(IMovableGrid grid, EMovableType type, Movable movable) {
		switch (type) {
		case BEARER:
			return new BearerStrategy(grid, movable);

		case PIONEER:
			return new PioneerStrategy(grid, movable);

		case LUMBERJACK:
		case SAWMILLER:
		case MINER:
		case STONECUTTER:
		case FARMER:
		case FISHERMAN:
		case FORESTER:
		case MELTER:
		case MILLER:
		case SMITH:
		case BAKER:
		case PIG_FARMER:
		case CHARCOAL_BURNER:
		case SLAUGHTERER:
		case WATERWORKER:
			return new BuildingWorkerStrategy(grid, movable, type);

		case SWORDSMAN_L1:
		case SWORDSMAN_L2:
		case SWORDSMAN_L3:
			return new SwordsmanStrategy(grid, movable, type);

		case BOWMAN_L1:
		case BOWMAN_L2:
		case BOWMAN_L3:
			return new BowmanStrategy(grid, movable, type);

		case PIKEMAN_L1:
		case PIKEMAN_L2:
		case PIKEMAN_L3:
			return new PikemanStrategy(grid, movable, type);

		case BRICKLAYER:
			return new BricklayerStrategy(grid, movable);

		case DIGGER:
			return new DiggerStrategy(grid, movable);

		default:
			assert false : "have no strategy for this type of movable: " + type;
			return null;
		}
	}

	/**
	 * is called after the action set by setAction() has been finished
	 * 
	 * @return true if the action has been consumed by the method call (used for subclasses)
	 */
	protected abstract boolean actionFinished();

	protected void goToTile(ISPosition2D pos) {
		this.movable.goToTile(pos);
	}

	protected void setAction(EAction action, float duration) {
		this.movable.setAction(action, duration);
	}

	protected void setMaterial(EMaterialType material) {
		this.movable.setMaterial(material);
	}

	protected ISPosition2D getPos() {
		return this.movable.getPos();
	}

	protected byte getPlayer() {
		return this.movable.getPlayer();
	}

	protected abstract void setGotoJob(GotoJob gotoJob);

	protected void convertTo(EMovableType movableType) {
		this.movable.setMaterial(EMaterialType.NO_MATERIAL);
		this.movable.setStrategy(getTypeStrategy(grid, movableType, movable));
	}

	protected void setPos(ISPosition2D pos) {
		this.movable.setPos(pos);
	}

	protected void setVisible(boolean visible) {
		this.movable.setVisible(visible);
	}

	protected void setDirection(EDirection direction) {
		this.movable.setDirection(direction);
	}

	protected abstract void stopOrStartWorking(boolean stop);

	protected void gotHitEvent() {
	}

	protected abstract boolean noActionEvent();

	protected void setWaiting(float time) {
		movable.setWaiting(time);
	}

	protected IMovableGrid getGrid() {
		return grid;
	}
}
