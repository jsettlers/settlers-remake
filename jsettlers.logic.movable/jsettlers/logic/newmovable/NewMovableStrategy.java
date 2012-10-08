package jsettlers.logic.newmovable;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.newmovable.interfaces.IStrategyGrid;
import jsettlers.logic.newmovable.strategies.BearerMovableStrategy;
import jsettlers.logic.newmovable.strategies.BricklayerStrategy;
import jsettlers.logic.newmovable.strategies.BuildingWorkerStrategy;
import jsettlers.logic.newmovable.strategies.DiggerStrategy;
import jsettlers.logic.newmovable.strategies.soldiers.BowmanStrategy;
import jsettlers.logic.newmovable.strategies.soldiers.InfantryStrategy;
import jsettlers.logic.newmovable.strategies.specialists.DummySpecialistStrategy;
import jsettlers.logic.newmovable.strategies.specialists.GeologistStrategy;
import jsettlers.logic.newmovable.strategies.specialists.PioneerStrategy;

/**
 * Abstract super class of all movable strategies.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class NewMovableStrategy implements Serializable {
	private static final long serialVersionUID = 3135655342562634378L;

	private final NewMovable movable;

	protected NewMovableStrategy(NewMovable movable) {
		this.movable = movable;
	}

	public static NewMovableStrategy getStrategy(NewMovable movable, EMovableType movableType) {
		switch (movableType) {
		case BEARER:
			return new BearerMovableStrategy(movable);

		case SWORDSMAN_L1:
		case SWORDSMAN_L2:
		case SWORDSMAN_L3:
		case PIKEMAN_L1:
		case PIKEMAN_L2:
		case PIKEMAN_L3:
			return new InfantryStrategy(movable, movableType);
		case BOWMAN_L1:
		case BOWMAN_L2:
		case BOWMAN_L3:
			return new BowmanStrategy(movable, movableType);

		case BAKER:
		case CHARCOAL_BURNER:
		case FARMER:
		case FISHERMAN:
		case FORESTER:
		case MELTER:
		case MILLER:
		case MINER:
		case PIG_FARMER:
		case LUMBERJACK:
		case SAWMILLER:
		case SLAUGHTERER:
		case SMITH:
		case STONECUTTER:
		case WATERWORKER:
			return new BuildingWorkerStrategy(movable, movableType);

		case DIGGER:
			return new DiggerStrategy(movable);

		case BRICKLAYER:
			return new BricklayerStrategy(movable);

		case PIONEER:
			return new PioneerStrategy(movable);
		case GEOLOGIST:
			return new GeologistStrategy(movable);
		case THIEF:
			return new DummySpecialistStrategy(movable);

		default:
			assert false : "requested movableType: " + movableType + " but have no strategy for this type!";
			return null;
		}
	}

	protected abstract void action();

	protected final void convertTo(EMovableType movableType) {
		movable.convertTo(movableType);
	}

	protected final EMaterialType setMaterial(EMaterialType materialType) {
		return movable.setMaterial(materialType);
	}

	protected final void playAction(EAction movableAction, float duration) { // TODO @Andreas : rename EAction to EMovableAction
		movable.playAction(movableAction, duration);
	}

	protected final void lookInDirection(EDirection direction) {
		movable.lookInDirection(direction);
	}

	protected final boolean goToPos(ShortPoint2D targetPos) {
		return movable.goToPos(targetPos);
	}

	protected final IStrategyGrid getStrategyGrid() {
		return movable.getStrategyGrid();
	}

	/**
	 * Tries to go a step in the given direction.
	 * 
	 * @param direction
	 *            direction to go
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	protected final boolean goInDirection(EDirection direction) {
		return movable.goInDirection(direction);
	}

	/**
	 * Forces the movable to go a step in the given direction (if it is not blocked).
	 * 
	 * @param direction
	 *            direction to go
	 */
	protected final void forceGoInDirection(EDirection direction) {
		movable.forceGoInDirection(direction);
	}

	protected final void setPosition(ShortPoint2D pos) {
		movable.setPos(pos);
	}

	protected final void setVisible(boolean visible) {
		movable.setVisible(visible);
	}

	/**
	 * 
	 * @param dijkstra
	 *            if true, dijkstra algorithm is used<br>
	 *            if false, in area finder is used.
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param searchType
	 * @return
	 */
	protected final boolean preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
		return movable.preSearchPath(dijkstra, centerX, centerY, radius, searchType);
	}

	protected final void followPresearchedPath() {
		movable.followPresearchedPath();
	}

	protected final void enableNothingToDoAction(boolean enable) {
		movable.enableNothingToDoAction(enable);
	}

	protected void setSelected(boolean selected) {
		movable.setSelected(selected);
	}

	protected final boolean fitsSearchType(ShortPoint2D pos, ESearchType searchType) {
		return movable.getStrategyGrid().fitsSearchType(movable, pos, searchType);
	}

	protected final boolean isValidPosition(ShortPoint2D position) {
		return movable.isValidPosition(position);
	}

	public final ShortPoint2D getPos() {
		return movable.getPos();
	}

	protected final EMaterialType getMaterial() {
		return movable.getMaterial();
	}

	protected final byte getPlayer() {
		return movable.getPlayer();
	}

	protected NewMovable getMovable() {
		return movable;
	}

	protected final void abortPath() {
		movable.abortPath();
	}

	/**
	 * Checks preconditions before the next path step can be gone.
	 * 
	 * @param pathTarget
	 *            Target of the current path.
	 * @param step
	 *            The number of the current step where 1 means the first step.
	 * 
	 * @return true if the path should be continued<br>
	 *         false if it must be stopped.
	 */
	protected boolean checkPathStepPreconditions(@SuppressWarnings("unused") ShortPoint2D pathTarget, @SuppressWarnings("unused") int step) {
		return true;
	}

	/**
	 * This method is called when a movable is killed or converted to another strategy and can be used for finalization work in the strategy.
	 * 
	 * @param pathTarget
	 *            if the movable is currently walking on a path, this is the target of the path<br>
	 *            else it is null.
	 */
	protected void strategyKilledEvent(@SuppressWarnings("unused") ShortPoint2D pathTarget) { // used in overriding methods
	}

	protected void moveToPathSet(@SuppressWarnings("unused") ShortPoint2D oldTargetPos, @SuppressWarnings("unused") ShortPoint2D targetPos) {
	}

	/**
	 * This method may only be called if this movable shall be informed about a movable that's in it's search radius.
	 * 
	 * @param other
	 *            The other movable.
	 */
	protected void informAboutAttackable(@SuppressWarnings("unused") IAttackable other) {
	}

	protected boolean isMoveToAble() {
		return true;
	}

	protected Path findWayAroundObstacle(EDirection direction, ShortPoint2D position, Path path) {
		if (!(path.getStep() < path.getLength() - 1)) { // if path has no position left
			return path;
		}

		IStrategyGrid grid = movable.getStrategyGrid();

		EDirection leftDir = direction.getNeighbor(-1);
		EDirection rightDir = direction.getNeighbor(1);

		ShortPoint2D leftPos = leftDir.getNextHexPoint(position);
		ShortPoint2D leftStraightPos = direction.getNextHexPoint(leftPos);

		ShortPoint2D rightPos = rightDir.getNextHexPoint(position);
		ShortPoint2D rightStraightPos = direction.getNextHexPoint(rightPos);
		ShortPoint2D twoStraight = direction.getNextHexPoint(position, 2);

		ShortPoint2D overNextPos = path.getOverNextPos();

		if (twoStraight.equals(overNextPos)) {
			if (isValidPosition(leftPos) && grid.hasNoMovableAt(leftPos.getX(), leftPos.getY()) && isValidPosition(leftStraightPos)) {
				path.goToNextStep();
				path = new Path(path, leftPos, leftStraightPos);
				System.out.println("path replanned!");
			} else if (isValidPosition(rightPos) && grid.hasNoMovableAt(rightPos.getX(), rightPos.getY()) && isValidPosition(rightStraightPos)) {
				path.goToNextStep();
				path = new Path(path, rightPos, rightStraightPos);
				System.out.println("path replanned!");
			} else {
				// TODO @Andreas Eberle maybe calculate a new path
			}
		} else if (leftStraightPos.equals(overNextPos) && grid.hasNoMovableAt(leftPos.getX(), leftPos.getY())) {
			path.goToNextStep();
			path = new Path(path, leftPos);
			System.out.println("path replanned!");
		} else if (rightStraightPos.equals(overNextPos) && grid.hasNoMovableAt(rightPos.getX(), rightPos.getY())) {
			path.goToNextStep();
			path = new Path(path, rightPos);
			System.out.println("path replanned!");
		} else {
			// TODO @Andreas Eberle maybe calculate a new path
		}

		return path;
	}

}
