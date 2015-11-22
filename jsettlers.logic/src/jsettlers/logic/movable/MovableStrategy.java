/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.AbstractStrategyGrid;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.strategies.BearerMovableStrategy;
import jsettlers.logic.movable.strategies.BricklayerStrategy;
import jsettlers.logic.movable.strategies.BuildingWorkerStrategy;
import jsettlers.logic.movable.strategies.DiggerStrategy;
import jsettlers.logic.movable.strategies.soldiers.BowmanStrategy;
import jsettlers.logic.movable.strategies.soldiers.InfantryStrategy;
import jsettlers.logic.movable.strategies.specialists.DummySpecialistStrategy;
import jsettlers.logic.movable.strategies.specialists.GeologistStrategy;
import jsettlers.logic.movable.strategies.specialists.PioneerStrategy;
import jsettlers.logic.player.Player;

/**
 * Abstract super class of all movable strategies.
 *
 * @author Andreas Eberle
 *
 */
public abstract class MovableStrategy implements Serializable {
	private static final long serialVersionUID = 3135655342562634378L;

	private final Movable movable;

	protected MovableStrategy(Movable movable) {
		this.movable = movable;
	}

	public static MovableStrategy getStrategy(Movable movable, EMovableType movableType) {
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
		case WINEGROWER:
		case HEALER:
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
		case MAGE:
		case DONKEY:
			return new DummySpecialistStrategy(movable);

		default:
			assert false : "requested movableType: " + movableType + " but have no strategy for this type!";
			return null;
		}
	}

	protected void action() {
	}

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

	protected final AbstractStrategyGrid getStrategyGrid() {
		return movable.getStrategyGrid();
	}

	/**
	 * Tries to go a step in the given direction.
	 *
	 * @param direction
	 *            direction to go
	 * @param force
	 *            If true, the step will be forced and the method will always return true.
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	protected final boolean goInDirection(EDirection direction, boolean force) {
		return movable.goInDirection(direction, force);
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

	public final void setPosition(ShortPoint2D pos) {
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

	protected final Player getPlayer() {
		return movable.getPlayer();
	}

	protected Movable getMovable() {
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
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return true;
	}

	/**
	 * This method is called when a movable is killed or converted to another strategy and can be used for finalization work in the strategy.
	 *
	 * @param pathTarget
	 *            if the movable is currently walking on a path, this is the target of the path<br>
	 *            else it is null.
	 */
	protected void strategyKilledEvent(ShortPoint2D pathTarget) { // used in overriding methods
	}

	/**
	 *
	 * @param oldPosition
	 *            The position the movable was positioned before the new path has been calculated and the first step on the new path has been done.
	 * @param oldTargetPos
	 *            The target position of the old path or null if no old path was set.
	 * @param targetPos
	 *            The new target position.
	 */
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
	}

	/**
	 * This method may only be called if this movable shall be informed about a movable that's in it's search radius.
	 *
	 * @param other
	 *            The other movable.
	 */
	protected void informAboutAttackable(IAttackable other) {
	}

	protected boolean isMoveToAble() {
		return false;
	}

	protected Path findWayAroundObstacle(EDirection direction, ShortPoint2D position, Path path) {
		if (!path.hasOverNextStep()) { // if path has no position left
			return path;
		}

		AbstractStrategyGrid grid = movable.getStrategyGrid();

		EDirection leftDir = direction.getNeighbor(-1);
		EDirection rightDir = direction.getNeighbor(1);

		ShortPoint2D leftPos = leftDir.getNextHexPoint(position);
		ShortPoint2D leftStraightPos = direction.getNextHexPoint(leftPos);

		ShortPoint2D rightPos = rightDir.getNextHexPoint(position);
		ShortPoint2D rightStraightPos = direction.getNextHexPoint(rightPos);
		ShortPoint2D twoStraight = direction.getNextHexPoint(position, 2);

		ShortPoint2D overNextPos = path.getOverNextPos();

		if (twoStraight.equals(overNextPos)) {
			if (isValidPosition(leftPos) && grid.hasNoMovableAt(leftPos.x, leftPos.y) && isValidPosition(leftStraightPos)) {
				path.goToNextStep();
				path = new Path(path, leftPos, leftStraightPos);
			} else if (isValidPosition(rightPos) && grid.hasNoMovableAt(rightPos.x, rightPos.y) && isValidPosition(rightStraightPos)) {
				path.goToNextStep();
				path = new Path(path, rightPos, rightStraightPos);
			} else {
				// TODO @Andreas Eberle maybe calculate a new path
			}
		} else if (leftStraightPos.equals(overNextPos) && grid.hasNoMovableAt(leftPos.x, leftPos.y)) {
			path.goToNextStep();
			path = new Path(path, leftPos);
		} else if (rightStraightPos.equals(overNextPos) && grid.hasNoMovableAt(rightPos.x, rightPos.y)) {
			path.goToNextStep();
			path = new Path(path, rightPos);
		} else {
			// TODO @Andreas Eberle maybe calculate a new path
		}

		return path;
	}

	protected void stopOrStartWorking(boolean stop) {
	}

	protected void sleep(short waitTime) {
		movable.wait(waitTime);
	}

	protected void pathAborted(ShortPoint2D pathTarget) {
	}

	public boolean offerDroppedMaterial() {
		return true;
	}

	protected boolean take(EMaterialType materialToTake, boolean takeFromMap) {
		return movable.take(materialToTake, takeFromMap);
	}

	protected void drop(EMaterialType materialToDrop) {
		movable.drop(materialToDrop);
	}

	protected boolean isOnOwnGround() {
		return movable.isOnOwnGround();
	}
}
