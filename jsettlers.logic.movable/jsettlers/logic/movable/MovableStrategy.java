package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.movable.bearer.BearerStrategy;
import jsettlers.logic.movable.construction.BricklayerStrategy;
import jsettlers.logic.movable.construction.DiggerStrategy;
import jsettlers.logic.movable.soldiers.BowmanStrategy;
import jsettlers.logic.movable.soldiers.PikemanStrategy;
import jsettlers.logic.movable.soldiers.SwordsmanStrategy;
import jsettlers.logic.movable.specialists.GeologistStrategy;
import jsettlers.logic.movable.specialists.PioneerStrategy;
import jsettlers.logic.movable.specialists.ThiefStrategy;
import jsettlers.logic.movable.workers.BuildingWorkerStrategy;

/**
 * This is the strategy that tells a movable the next steps it has to do.
 * 
 * @author michael
 * 
 */
public abstract class MovableStrategy implements Serializable, IPathCalculateable {
	private static final long serialVersionUID = 7544560296926609429L;

	private final Movable movable;
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

		case GEOLOGIST:
			return new GeologistStrategy(grid, movable);

		case THIEF:
			return new ThiefStrategy(grid, movable);

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
	 * is called after the action set by setAction() has been finished.<br>
	 * NOTE: THIS METHOD MUST SET A NEW EAction!!
	 * 
	 * @return true if the action has been consumed by the method call (used for subclasses)
	 */
	protected abstract boolean actionFinished();

	protected void goToTile(ShortPoint2D pos) {
		this.movable.goToTile(pos);
	}

	protected void setAction(EAction action, float duration) {
		this.movable.setAction(action, duration);
	}

	protected final void setMaterial(EMaterialType material) {
		this.movable.setMaterial(material);
	}

	@Override
	public ShortPoint2D getPos() {
		return this.movable.getPos();
	}

	@Override
	public byte getPlayer() {
		return this.movable.getPlayer();
	}

	protected abstract void setGotoJob(Movable leader, GotoJob gotoJob);

	protected final void convertTo(EMovableType movableType) {
		this.movable.setMaterial(EMaterialType.NO_MATERIAL);
		this.movable.setStrategy(getTypeStrategy(grid, movableType, movable));
	}

	protected final void setPos(ShortPoint2D pos) {
		this.movable.setPos(pos);
	}

	protected void setVisible(boolean visible) {
		this.movable.setVisible(visible);
	}

	protected final void setDirection(EDirection direction) {
		this.movable.setDirection(direction);
	}

	protected abstract void stopOrStartWorking(boolean stop);

	protected void gotHitEvent() {
	}

	protected abstract boolean noActionEvent();

	protected final void setWaiting(float time) {
		movable.setWaiting(time);
	}

	protected IMovableGrid getGrid() {
		return grid;
	}

	protected void leaveBlockedPosition() {
	}

	protected void killedEvent() {
	}

	protected final EMaterialType getMaterial() {
		return movable.getMaterial();
	}

	protected final void setSleeping(boolean sleep) {
		movable.setState(sleep ? EMovableState.SLEEPING : EMovableState.NO_ACTION);
	}

	public void setDontMove(boolean dontMove) {
		movable.setState(dontMove ? EMovableState.DONT_MOVE : EMovableState.NO_ACTION);
	}

	protected boolean canOccupyBuilding() {
		return false;
	}

	public final Movable getMovable() {
		return movable;
	}

	protected void setSelected(boolean selected) {
		movable.setSelected(selected);
	}

	/**
	 * This method is called when a movable changes it's strategy due to a user action.
	 */
	protected void convertActionEvent() {
	}
}
