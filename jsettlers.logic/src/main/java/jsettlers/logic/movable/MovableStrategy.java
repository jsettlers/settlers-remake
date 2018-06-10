/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.movable.strategies.BearerMovableStrategy;
import jsettlers.logic.movable.strategies.BricklayerStrategy;
import jsettlers.logic.movable.strategies.BuildingWorkerStrategy;
import jsettlers.logic.movable.strategies.DiggerStrategy;
import jsettlers.logic.movable.strategies.military.BowmanStrategy;
import jsettlers.logic.movable.strategies.military.FerryStrategy;
import jsettlers.logic.movable.strategies.military.InfantryStrategy;
import jsettlers.logic.movable.strategies.specialists.DummySpecialistStrategy;
import jsettlers.logic.movable.strategies.specialists.GeologistStrategy;
import jsettlers.logic.movable.strategies.specialists.PioneerStrategy;
import jsettlers.logic.movable.strategies.trading.CargoShipStrategy;
import jsettlers.logic.movable.strategies.trading.DonkeyStrategy;

/**
 * Abstract super class of all movable strategies.
 *
 * @author Andreas Eberle
 */
public abstract class MovableStrategy implements Serializable {
	private static final long serialVersionUID = 3135655342562634378L;

	protected final Movable movable;

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
			case DONKEY_FARMER:
			case LUMBERJACK:
			case SAWMILLER:
			case SLAUGHTERER:
			case SMITH:
			case STONECUTTER:
			case WATERWORKER:
			case WINEGROWER:
			case HEALER:
			case DOCKWORKER:
				return new BuildingWorkerStrategy(movable);

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
				return new DummySpecialistStrategy(movable);

			case DONKEY:
				return new DonkeyStrategy(movable);

			case FERRY:
				return new FerryStrategy(movable);

			case CARGO_SHIP:
				return new CargoShipStrategy(movable);

			default:
				assert false : "requested movableType: " + movableType + " but have no strategy for this type!";
				return null;
		}
	}

	protected void action() {
	}

	protected final EMaterialType setMaterial(EMaterialType materialType) {
		return movable.setMaterial(materialType);
	}

	protected final void playAction(EMovableAction movableAction, float duration) {
		movable.playAction(movableAction, duration);
	}

	protected final void lookInDirection(EDirection direction) {
		movable.lookInDirection(direction);
	}

	protected final boolean goToPos(ShortPoint2D targetPos) {
		return movable.goToPos(targetPos);
	}

	protected final AbstractMovableGrid getGrid() {
		return movable.grid;
	}

	/**
	 * Tries to go a step in the given direction.
	 *
	 * @param direction
	 * 		direction to go
	 * @param mode
	 * 		The mode used for this operation
	 * @return true if the step can and will immediately be executed. <br>
	 * false if the target position is generally blocked or a movable occupies that position.
	 */
	protected final boolean goInDirection(EDirection direction, EGoInDirectionMode mode) {
		return movable.goInDirection(direction, mode);
	}

	public final void setPosition(ShortPoint2D pos) {
		movable.setPosition(pos);
	}

	protected final void setVisible(boolean visible) {
		movable.setVisible(visible);
	}

	/**
	 * @param dijkstra
	 * 		if true, dijkstra algorithm is used<br>
	 * 		if false, in area finder is used.
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param searchType
	 * @return true if a path has been found.
	 */
	protected final boolean preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
		return movable.preSearchPath(dijkstra, centerX, centerY, radius, searchType);
	}

	protected final ShortPoint2D followPresearchedPath() {
		return movable.followPresearchedPath();
	}

	protected final void enableNothingToDoAction(boolean enable) {
		movable.enableNothingToDoAction(enable);
	}

	protected final boolean fitsSearchType(ShortPoint2D pos, ESearchType searchType) {
		return movable.grid.fitsSearchType(movable, pos.x, pos.y, searchType);
	}

	protected final boolean fitsSearchType(int x, int y, ESearchType searchType) {
		return movable.grid.fitsSearchType(movable, x, y, searchType);
	}

	protected final boolean isValidPosition(ShortPoint2D position) {
		return movable.grid.isValidPosition(movable, position.x, position.y);
	}

	protected final boolean isValidPosition(int x, int y) {
		return movable.grid.isValidPosition(movable, x, y);
	}

	public final ShortPoint2D getPosition() {
		return movable.getPosition();
	}

	protected final void abortPath() {
		movable.abortPath();
	}

	/**
	 * Checks preconditions before the next path step can be gone.
	 *
	 * @param pathTarget
	 * 		Target of the current path.
	 * @param step
	 * 		The number of the current step where 1 means the first step.
	 * @return true if the path should be continued<br>
	 * false if it must be stopped.
	 */
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return true;
	}

	/**
	 * This method is called when a movable is killed or converted to another strategy and can be used for finalization work in the strategy.
	 *
	 * @param pathTarget
	 * 		if the movable is currently walking on a path, this is the target of the path<br>
	 * 		else it is null.
	 */
	protected void strategyKilledEvent(ShortPoint2D pathTarget) { // used in overriding methods
	}

	/**
	 * @param oldPosition
	 * 		The position the movable was positioned before the new path has been calculated and the first step on the new path has been done.
	 * @param oldTargetPos
	 * 		The target position of the old path or null if no old path was set.
	 * @param targetPos
	 * 		The new target position.
	 */
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
	}

	/**
	 * This method may only be called if this movable shall be informed about a movable that's in it's search radius.
	 *
	 * @param other
	 * 		The other movable.
	 */
	protected void informAboutAttackable(IAttackable other) {
	}

	protected boolean canBeControlledByPlayer() {
		return false;
	}

	protected boolean isAttackable() {
		return movable.getMovableType().attackable;
	}

	protected Path findWayAroundObstacle(ShortPoint2D position, Path path) {
		if (!path.hasOverNextStep()) { // if path has no position left
			return path;
		}

		EDirection direction = EDirection.getApproxDirection(position, path.getOverNextPos());

		AbstractMovableGrid grid = movable.grid;

		EDirection rightDir = direction.getNeighbor(-1);
		EDirection leftDir = direction.getNeighbor(1);

		ShortPoint2D straightPos = direction.getNextHexPoint(position);
		ShortPoint2D twoStraightPos = direction.getNextHexPoint(position, 2);

		ShortPoint2D rightPos = rightDir.getNextHexPoint(position);
		ShortPoint2D rightStraightPos = direction.getNextHexPoint(rightPos);
		ShortPoint2D straightRightPos = rightDir.getNextHexPoint(straightPos);

		ShortPoint2D leftPos = leftDir.getNextHexPoint(position);
		ShortPoint2D leftStraightPos = direction.getNextHexPoint(leftPos);
		ShortPoint2D straightLeftPos = leftDir.getNextHexPoint(straightPos);

		ShortPoint2D overNextPos = path.getOverNextPos();

		LinkedList<ShortPoint2D[]> possiblePaths = new LinkedList<>();

		if (twoStraightPos.equals(overNextPos)) {
			if (isValidPosition(rightPos) && isValidPosition(rightStraightPos)) {
				possiblePaths.add(new ShortPoint2D[]{
					rightPos,
					rightStraightPos});
			} else if (isValidPosition(leftPos) && isValidPosition(leftStraightPos)) {
				possiblePaths.add(new ShortPoint2D[]{
					leftPos,
					leftStraightPos});
			} else {
				// TODO @Andreas Eberle maybe calculate a new path
			}
		}

		if (rightStraightPos.equals(overNextPos) && isValidPosition(rightPos)) {
			possiblePaths.add(new ShortPoint2D[]{rightPos});
		}
		if (leftStraightPos.equals(overNextPos) && isValidPosition(leftPos)) {
			possiblePaths.add(new ShortPoint2D[]{leftPos});
		}

		if ((straightRightPos.equals(overNextPos) || straightLeftPos.equals(overNextPos))
			&& isValidPosition(straightPos) && grid.hasNoMovableAt(straightPos.x, straightPos.y)) {
			possiblePaths.add(new ShortPoint2D[]{straightPos});

		} else {
			// TODO @Andreas Eberle maybe calculate a new path
		}

		// try to find a way without a movable or with a pushable movable.
		for (ShortPoint2D[] pathPrefix : possiblePaths) { // check if any of the paths is free of movables
			ShortPoint2D firstPosition = pathPrefix[0];
			ILogicMovable movable = grid.getMovableAt(firstPosition.x, firstPosition.y);
			if (movable == null || movable.isProbablyPushable(this.movable)) {
				path.goToNextStep();
				return new Path(path, pathPrefix);
			}
		}

		return path;
	}

	protected void stopOrStartWorking(boolean stop) {
	}

	protected void sleep(short sleepTime) {
		movable.sleep(sleepTime);
	}

	protected void pathAborted(ShortPoint2D pathTarget) {
	}

	/**
	 * This method is called before a material is dropped during a {@link EMovableType}.DROP action.
	 *
	 * @return If true is returned, the dropped material is offered, if false, it isn't.
	 */
	protected boolean droppingMaterial() {
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

	/**
	 * @return If true, the hit is received, if false, the hit is ignored.
	 */
	protected boolean receiveHit() {
		return true;
	}

	protected void tookMaterial() {
	}

	public EBuildingType getBuildingType() {
		return null;
	}

	protected boolean addPassenger(ILogicMovable movable) {
		return false;
	}

	protected List<? extends ILogicMovable> getPassengers() {
		return Collections.emptyList();
	}

	protected void unloadFerry() {
	}

	protected EMaterialType getCargoType(int stack) {
		return null;
	}

	protected int getCargoCount(int stack) {
		return 0;
	}

	protected int getNumberOfCargoStacks() {
		return 0;
	}
}
