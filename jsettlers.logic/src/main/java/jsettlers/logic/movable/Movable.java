/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.io.IOException;
import java.io.ObjectInputStream;

import jsettlers.algorithms.path.Path;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.graphics.messages.SimpleMessage;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.movable.strategies.FleeStrategy;
import jsettlers.logic.movable.strategies.soldiers.SoldierStrategy;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * Central Movable class of JSettlers.
 *
 * @author Andreas Eberle
 *
 */
public final class Movable implements ILogicMovable {
	private static final long serialVersionUID = 2472076796407425256L;


	protected final AbstractMovableGrid grid;
	private final int id;

	private EMovableState state = EMovableState.DOING_NOTHING;

	private EMovableType movableType;
	private MovableStrategy strategy;
	private final Player player;

	private EMaterialType materialType = EMaterialType.NO_MATERIAL;
	private EMovableAction movableAction = EMovableAction.NO_ACTION;
	private EDirection direction;

	private int animationStartTime;
	private short animationDuration;

	private ShortPoint2D position;

	private ShortPoint2D requestedTargetPosition = null;
	private Path path;

	private float health;
	private boolean visible = true;
	private boolean enableNothingToDo = true;
	private ILogicMovable pushedFrom;

	private boolean isRightstep = false;
	private int flockDelay = 700;

	private EMaterialType takeDropMaterial;

	private transient boolean selected = false;
	private transient boolean soundPlayed = false;

	public Movable(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		this.grid = grid;
		this.position = position;
		this.player = player;
		this.strategy = MovableStrategy.getStrategy(this, movableType);
		this.movableType = movableType;
		this.health = movableType.getHealth();

		this.direction = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];

		RescheduleTimer.add(this, Constants.MOVABLE_INTERRUPT_PERIOD);

		this.id = MovableDataManager.getNextID();
		MovableDataManager.movablesByID().put(this.id, this);
		MovableDataManager.allMovables().offer(this);

		grid.enterPosition(position, this, true);
	}

	/**
	 * This method overrides the standard deserialize method to restore the movablesByID map and the nextID.
	 *
	 * @param ois
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private final void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		MovableDataManager.movablesByID().put(this.id, this);
		MovableDataManager.allMovables().add(this);
		MovableDataManager.setNextID(this.id + 1);
	}

	/**
	 * Tests if this movable can receive moveTo requests and if so, directs it to go to the given position.
	 *
	 * @param targetPosition
	 */
	public final void moveTo(ShortPoint2D targetPosition) {
		if (movableType.isPlayerControllable() && strategy.canBeControlledByPlayer() && !alreadyWalkingToPosition(targetPosition)) {
			this.requestedTargetPosition = targetPosition;
		}
	}

	private boolean alreadyWalkingToPosition(ShortPoint2D targetPosition) {
		return this.state == EMovableState.PATHING && this.path.getTargetPos().equals(targetPosition);
	}

	public void leavePosition() {
		if (state != EMovableState.DOING_NOTHING || !enableNothingToDo) {
			return;
		}

		int offset = MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS);

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.VALUES[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (goInDirection(currDir, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE)) {
				break;
			} else {
				ILogicMovable movableAtPos = grid.getMovableAt(currDir.getNextTileX(position.x), currDir.getNextTileY(position.y));
				if (movableAtPos != null) {
					movableAtPos.push(this);
				}
			}
		}
	}

	@Override
	public int timerEvent() {
		if (state == EMovableState.DEAD) {
			return -1;
		}

		switch (state) { // ensure animation is finished, if not, reschedule
		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
		case TAKE:
		case DROP:
		case PATHING:
		case WAITING:
			int remainingAnimationTime = animationStartTime + animationDuration - MatchConstants.clock().getTime();
			if (remainingAnimationTime > 0) {
				return remainingAnimationTime;
			}
			break;
		default:
			break;
		}

		switch (state) {
		case TAKE:
		case DROP:
			if (this.movableAction != EMovableAction.RAISE_UP) {
				break;
			} // TAKE and DROP are finished if we get here and we the action is RAISE_UP, otherwise continue with second part.

		case WAITING:
		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
			state = EMovableState.DOING_NOTHING; // the action is finished, as the time passed
			movableAction = EMovableAction.NO_ACTION;

		case PATHING:
		case DOING_NOTHING:
			if (visible) {
				checkPlayerOfCurrentPosition();
			}
			break;

		default:
			break;
		}

		if (requestedTargetPosition != null) {
			if (strategy.canBeControlledByPlayer()) {
				switch (state) {
				case PATHING:
					// if we're currently pathing, stop former pathing and calculate a new path
					setState(EMovableState.DOING_NOTHING);
					this.movableAction = EMovableAction.NO_ACTION;
					this.path = null;

				case DOING_NOTHING:
					ShortPoint2D oldTargetPos = path != null ? path.getTargetPos() : null;
					ShortPoint2D oldPos = position;
					boolean foundPath = goToPos(requestedTargetPosition); // progress is reset in here
					requestedTargetPosition = null;

					if (foundPath) {
						this.strategy.moveToPathSet(oldPos, oldTargetPos, path.getTargetPos());
						return animationDuration; // we already follow the path and initiated the walking
					} else {
						break;
					}

				default:
					break;
				}
			} else {
				requestedTargetPosition = null;
			}
		}

		switch (state) {
		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
			setState(EMovableState.DOING_NOTHING);
			this.movableAction = EMovableAction.NO_ACTION;
			break;

		case PATHING:
			pathingAction();
			break;

		case TAKE:
			grid.takeMaterial(position, takeDropMaterial);
			setMaterial(takeDropMaterial);
			playAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION);
			strategy.tookMaterial();
			break;
		case DROP:
			if (takeDropMaterial != null && takeDropMaterial.isDroppable()) {
				boolean offerMaterial = strategy.droppingMaterial();
				grid.dropMaterial(position, takeDropMaterial, offerMaterial, false);
			}
			setMaterial(EMaterialType.NO_MATERIAL);
			playAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION);
			break;

		default:
			break;
		}

		if (state == EMovableState.DOING_NOTHING) { // if movable is currently doing nothing
			strategy.action(); // let the strategy work

			if (state == EMovableState.DOING_NOTHING) { // if movable is still doing nothing after strategy, consider doingNothingAction()
				if (visible && enableNothingToDo) {
					return doingNothingAction();
				} else {
					return Constants.MOVABLE_INTERRUPT_PERIOD;
				}
			}
		}

		return animationDuration;
	}

	private void pathingAction() {
		if (path == null || !path.hasNextStep() || !strategy.checkPathStepPreconditions(path.getTargetPos(), path.getStep())) {
			// if path is finished, or canceled by strategy return from here
			setState(EMovableState.DOING_NOTHING);
			movableAction = EMovableAction.NO_ACTION;
			path = null;
			return;
		}

		ILogicMovable blockingMovable = grid.getMovableAt(path.nextX(), path.nextY());
		if (blockingMovable == null) { // if we can go on to the next step
			if (grid.isValidNextPathPosition(this, path.getNextPos(), path.getTargetPos())) { // next position is valid
				goSinglePathStep();

			} else { // next position is invalid
				movableAction = EMovableAction.NO_ACTION;
				animationDuration = Constants.MOVABLE_INTERRUPT_PERIOD; // recheck shortly
				Path newPath = grid.calculatePathTo(this, path.getTargetPos()); // try to find a new path

				if (newPath == null) { // no path found
					setState(EMovableState.DOING_NOTHING);

					strategy.pathAborted(path.getTargetPos()); // inform strategy
					path = null;
				} else {
					this.path = newPath; // continue with new path
					if (grid.hasNoMovableAt(path.nextX(), path.nextY())) { // path is valid, but maybe blocked (leaving blocked area)
						goSinglePathStep();
					}
				}
			}

		} else { // step not possible, so try it next time
			movableAction = EMovableAction.NO_ACTION;
			boolean pushedSuccessfully = blockingMovable.push(this);
			if (!pushedSuccessfully) {
				path = strategy.findWayAroundObstacle(position, path);
				animationDuration = Constants.MOVABLE_INTERRUPT_PERIOD; // recheck shortly
			} else if (movableAction == EMovableAction.NO_ACTION) {
				animationDuration = Constants.MOVABLE_INTERRUPT_PERIOD; // recheck shortly
			} // else: push initiated our next step
		}
	}

	@Override
	public void goSinglePathStep() {
		initGoingSingleStep(path.getNextPos());
		path.goToNextStep();
	}

	@Override
	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	public ILogicMovable getPushedFrom() {
		return pushedFrom;
	}

	private void initGoingSingleStep(ShortPoint2D position) {
		direction = EDirection.getDirection(this.position, position);
		playAnimation(EMovableAction.WALKING, movableType.getStepDurationMs());
		grid.leavePosition(this.position, this);
		grid.enterPosition(position, this, false);
		this.position = position;
		isRightstep = !isRightstep;
	}

	private int doingNothingAction() {
		if (grid.isBlockedOrProtected(position.x, position.y)) {
			Path newPath = grid.searchDijkstra(this, position.x, position.y, (short) 50, ESearchType.NON_BLOCKED_OR_PROTECTED);
			if (newPath == null) {
				kill();
				return -1;
			} else {
				followPath(newPath);
				return animationDuration;
			}
		} else {
			if (flockToDecentralize()) {
				return animationDuration;
			} else {
				int turnDirection = MatchConstants.random().nextInt(-8, 8);
				if (Math.abs(turnDirection) <= 1) {
					lookInDirection(direction.getNeighbor(turnDirection));
				}
			}

			return flockDelay;
		}
	}

	/**
	 * Tries to walk the movable into a position where it has a minimum distance to others.
	 *
	 * @return true if the movable moves to flock, false if no flocking is required.
	 */
	private boolean flockToDecentralize() {
		ShortPoint2D decentVector = grid.calcDecentralizeVector(position.x, position.y);

		EDirection randomDirection = direction.getNeighbor(MatchConstants.random().nextInt(-1, 1));
		int dx = randomDirection.gridDeltaX + decentVector.x;
		int dy = randomDirection.gridDeltaY + decentVector.y;

		if (ShortPoint2D.getOnGridDist(dx, dy) >= 2) {
			flockDelay = Math.max(flockDelay - 100, 500);
			return this.goInDirection(EDirection.getApproxDirection(0, 0, dx, dy), EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE);
		} else {
			flockDelay = Math.min(flockDelay + 100, 1000);
			return false;
		}
	}

	/**
	 * A call to this method indicates this movable that it shall leave it's position to free the position for another movable.
	 *
	 * @param pushingMovable
	 *            The movable pushing at this movable. This should be the movable that want's to get the position!
	 * @return true if this movable will move out of it's way in the near future <br>
	 *         false if this movable doesn't move.
	 */
	@Override
	public boolean push(ILogicMovable pushingMovable) {
		if (state == EMovableState.DEAD) {
			return false;
		}

		switch (state) {
		case DOING_NOTHING:
			if (!enableNothingToDo) { // don't go to random direction if movable shouldn't do something in DOING_NOTHING
				return false;
			}

			if (goToRandomDirection(pushingMovable)) { // try to find free direction
				return true; // if we found a free direction, go there and tell the pushing one we'll move

			} else { // if we didn't find a direction, check if it's possible to exchange positions
				if (pushingMovable.getPath() == null || !pushingMovable.getPath().hasNextStep()) {
					return false; // the other movable just pushed to get space, we can't do anything for it here.

				} else if (pushingMovable.getMovableType().isPlayerControllable()
						|| strategy.isValidPosition(pushingMovable.getPos())) { // exchange positions
					EDirection directionToPushing = EDirection.getDirection(position, pushingMovable.getPos());
					pushingMovable.goSinglePathStep(); // if no free direction found, exchange the positions of the movables
					goInDirection(directionToPushing, EGoInDirectionMode.GO_IF_ALLOWED_WAIT_TILL_FREE);
					return true;

				} else { // exchange not possible, as the location is not valid.
					return false;
				}
			}

		case PATHING:
			if (path == null || pushingMovable.getPath() == null || !pushingMovable.getPath().hasNextStep()) {
				return false; // the other movable just pushed to get space, so we can't do anything for it in this state.
			}

			if (animationStartTime + animationDuration <= MatchConstants.clock().getTime() && this.path.hasNextStep()) {
				ShortPoint2D nextPos = path.getNextPos();
				if (pushingMovable.getPosition() == nextPos) { // two movables going in opposite direction and wanting to exchange positions
					pushingMovable.goSinglePathStep();
					this.goSinglePathStep();

				} else {
					if (grid.hasNoMovableAt(nextPos.x, nextPos.y)) {
						// this movable isn't blocked, so just let it's pathingAction() handle this
					} else if (pushedFrom == null) {
						try {
							this.pushedFrom = pushingMovable;
							return grid.getMovableAt(nextPos.x, nextPos.y).push(this);
						} finally {
							this.pushedFrom = null;
						}
					} else {
						while (pushingMovable != this) {
							pushingMovable.goSinglePathStep();
							pushingMovable = pushingMovable.getPushedFrom();
						}
						this.goSinglePathStep();
					}
				}
			}
			return true;

		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
		case TAKE:
		case DROP:
		case WAITING:
			return false; // we can't do anything

		case DEBUG_STATE:
			return false;

		default:
			assert false : "got pushed in unhandled state: " + state;
			return false;
		}
	}

	@Override
	public Path getPath() {
		return path;
	}

	public boolean isProbablyPushable(ILogicMovable pushingMovable) {
		switch (state) {
		case DOING_NOTHING:
			return true;
		case PATHING:
			return path != null && pushingMovable.getPath() != null && pushingMovable.getPath().hasNextStep();
		default:
			return false;
		}
	}

	private boolean goToRandomDirection(ILogicMovable pushingMovable) {
		int offset = MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS);
		EDirection pushedFromDir = EDirection.getDirection(this.getPos(), pushingMovable.getPos());

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.VALUES[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (currDir != pushedFromDir && goInDirection(currDir, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the material this movable is carrying to the given one.
	 *
	 * @param materialType
	 * @return {@link EMaterialType} that has been set before.
	 */
	final EMaterialType setMaterial(EMaterialType materialType) {
		assert materialType != null : "MaterialType may not be null";
		EMaterialType former = this.materialType;
		this.materialType = materialType;
		return former;
	}

	/**
	 * Lets this movable execute the given action with given duration.
	 *
	 * @param movableAction
	 *            action to be animated.
	 * @param duration
	 *            duration the animation should last (in seconds). // TODO change to milliseconds
	 */
	final void playAction(EMovableAction movableAction, float duration) {
		assert state == EMovableState.DOING_NOTHING : "can't do playAction() if state isn't DOING_NOTHING. curr state: " + state;

		playAnimation(movableAction, (short) (duration * 1000));
		setState(EMovableState.PLAYING_ACTION);
		this.soundPlayed = false;
	}

	private void playAnimation(EMovableAction movableAction, short duration) {
		this.animationStartTime = MatchConstants.clock().getTime();
		this.animationDuration = duration;
		this.movableAction = movableAction;
	}

	/**
	 *
	 * @param materialToTake
	 * @return true if the animation will be executed.
	 */
	final boolean take(EMaterialType materialToTake, boolean takeFromMap) {
		if (!takeFromMap || grid.canTakeMaterial(position, materialToTake)) {
			this.takeDropMaterial = materialToTake;

			playAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION);
			setState(EMovableState.TAKE);
			return true;
		} else {
			return false;
		}
	}

	final void drop(EMaterialType materialToDrop) {
		this.takeDropMaterial = materialToDrop;

		playAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION);
		setState(EMovableState.DROP);
	}

	/**
	 *
	 * @param sleepTime
	 *            time to sleep in milliseconds
	 */
	final void sleep(short sleepTime) {
		assert state == EMovableState.DOING_NOTHING : "can't do sleep() if state isn't DOING_NOTHING. curr state: " + state;

		playAnimation(EMovableAction.NO_ACTION, sleepTime);
		setState(EMovableState.WAITING);
	}

	/**
	 * Lets this movable look in the given direction.
	 *
	 * @param direction
	 */
	final void lookInDirection(EDirection direction) {
		this.direction = direction;
	}

	/**
	 * Lets this movable go to the given position.
	 *
	 * @param targetPos
	 *            position to move to.
	 * @return true if it was possible to calculate a path to the given position<br>
	 *         false if it wasn't possible to get a path.
	 */
	final boolean goToPos(ShortPoint2D targetPos) {
		assert state == EMovableState.DOING_NOTHING : "can't do goToPos() if state isn't DOING_NOTHING. curr state: " + state;

		Path path = grid.calculatePathTo(this, targetPos);
		if (path == null) {
			return false;
		} else {
			followPath(path);
			return this.path != null;
		}
	}

	/**
	 * Tries to go a step in the given direction.
	 *
	 * @param direction
	 *            direction to go
	 * @param mode
	 *            Use the given mode to go.<br>
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	final boolean goInDirection(EDirection direction, EGoInDirectionMode mode) {
		ShortPoint2D targetPosition = direction.getNextHexPoint(position);

		switch (mode) {
		case GO_IF_ALLOWED_WAIT_TILL_FREE: {
			this.direction = direction;
			setState(EMovableState.PATHING);
			this.followPath(new Path(targetPosition));
			return true;
		}
		case GO_IF_ALLOWED_AND_FREE:
			if ((grid.isValidPosition(this, targetPosition.x, targetPosition.y) && grid.hasNoMovableAt(targetPosition.x, targetPosition.y))) {
				initGoingSingleStep(targetPosition);
				setState(EMovableState.GOING_SINGLE_STEP);
				return true;
			} else {
				break;
			}
		case GO_IF_FREE:
			if (grid.isFreePosition(targetPosition)) {
				initGoingSingleStep(targetPosition);
				setState(EMovableState.GOING_SINGLE_STEP);
				return true;
			} else {
				break;
			}
		}
		return false;
	}

	final void setPosition(ShortPoint2D position) {
		if (visible) {
			grid.leavePosition(this.position, this);
			grid.enterPosition(position, this, true);
		}

		this.position = position;
	}

	final void setVisible(boolean visible) {
		if (this.visible == visible) { // nothing to change
		} else if (this.visible) { // is visible and gets invisible
			grid.leavePosition(position, this);
		} else {
			grid.enterPosition(position, this, true);
		}

		this.visible = visible;
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
	 * @return true if a path has been found.
	 */
	final boolean preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
		assert state == EMovableState.DOING_NOTHING : "this method can only be invoked in state DOING_NOTHING";

		if (dijkstra) {
			this.path = grid.searchDijkstra(this, centerX, centerY, radius, searchType);
		} else {
			this.path = grid.searchInArea(this, centerX, centerY, radius, searchType);
		}

		return path != null;
	}

	final ShortPoint2D followPresearchedPath() {
		assert this.path != null : "path mustn't be null to be able to followPresearchedPath()!";
		followPath(this.path);
		return path.getTargetPos();
	}

	final void enableNothingToDoAction(boolean enable) {
		this.enableNothingToDo = enable;
	}

	void abortPath() {
		path = null;
	}

	boolean isOnOwnGround() {
		return grid.getPlayerAt(position) == player;
	}

	private void followPath(Path path) {
		this.path = path;
		setState(EMovableState.PATHING);
		this.movableAction = EMovableAction.NO_ACTION;
		pathingAction();
	}

	/**
	 * Sets the state to the given one and resets the movable to a clean start of this state.
	 *
	 * @param newState
	 */
	private void setState(EMovableState newState) {
		this.state = newState;
	}

	/**

	 * kills this movable.
	 */
	@Override
	public final void kill() {
		if (state == EMovableState.DEAD) {
			return; // this movable already died.
		}

		grid.leavePosition(this.position, this);
		this.health = -200;
		this.strategy.strategyKilledEvent(path != null ? path.getTargetPos() : null);
		this.state = EMovableState.DEAD;
		this.selected = false;

		MovableDataManager.movablesByID().remove(this.getID());
		MovableDataManager.allMovables().remove(this);

		grid.addSelfDeletingMapObject(position, EMapObjectType.GHOST, Constants.GHOST_PLAY_DURATION, player);
	}

	@Override
	public final byte getPlayerId() {
		return player.playerId;
	}

	/**
	 * Gets the player object of this movable.
	 *
	 * @return The player object of this movable.
	 */
	public final Player getPlayer() {
		return player;
	}

	@Override
	public final boolean isSelected() {
		return selected;
	}

	@Override
	public final void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public final void stopOrStartWorking(boolean stop) {
		strategy.stopOrStartWorking(stop);
	}

	@Override
	public final ESelectionType getSelectionType() {
		return movableType.getSelectionType();
	}

	@Override
	public final void setSoundPlayed() {
		this.soundPlayed = true;
	}

	@Override
	public final boolean isSoundPlayed() {
		return soundPlayed;
	}

	@Override
	public final EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public final EMovableAction getAction() {
		return movableAction;
	}

	@Override
	public final EDirection getDirection() {
		return direction;
	}

	@Override
	public final float getMoveProgress() {
		return ((float) (MatchConstants.clock().getTime() - animationStartTime)) / animationDuration;
	}

	@Override
	public final EMaterialType getMaterial() {
		return materialType;
	}

	@Override
	public final ShortPoint2D getPos() {
		return position;
	}

	@Override
	public final float getHealth() {
		return health;
	}

	@Override
	public final boolean isRightstep() {
		return isRightstep;
	}

	@Override
	public final boolean needsPlayersGround() {
		return movableType.needsPlayersGround();
	}

	@Override
	public final short getViewDistance() {
		return Constants.MOVABLE_VIEW_DISTANCE;
	}

	@Override
	public final void debug() {
		System.out.println("debug: " + this);
	}

	@Override
	public final int getID() {
		return id;
	}

	/**
	 * Converts this movable to a movable of the given {@link EMovableType}.
	 *
	 * @param newMovableType
	 */
	public final void convertTo(EMovableType newMovableType) {
		if (newMovableType == EMovableType.BEARER && !player.equals(grid.getPlayerAt(position))) {
			return; // can't convert to bearer if the ground does not belong to the player
		}
		if (!(movableType == EMovableType.BEARER || (movableType == EMovableType.PIONEER && newMovableType == EMovableType.BEARER) || movableType == newMovableType)) {
			System.err.println("Tried invalid conversion from " + movableType + " to " + newMovableType);
			return; // can't convert between this types
		}

		this.health = (this.health * newMovableType.getHealth()) / this.movableType.getHealth();
		this.movableType = newMovableType;
		setVisible(true); // ensure the movable is visible
		setStrategy(MovableStrategy.getStrategy(this, newMovableType));
	}

	private void setStrategy(MovableStrategy newStrategy) {
		this.strategy.strategyKilledEvent(path != null ? path.getTargetPos() : null);
		this.strategy = newStrategy;
		this.movableAction = EMovableAction.NO_ACTION;
		setState(EMovableState.DOING_NOTHING);
		grid.notifyAttackers(position, this, true);
	}

	public final IBuildingOccupyableMovable setOccupyableBuilding(IOccupyableBuilding building) {
		if (canOccupyBuilding()) {
			return ((SoldierStrategy) strategy).setOccupyableBuilding(building);
		} else {
			return null;
		}
	}

	public final boolean canOccupyBuilding() {
		return movableType.getSelectionType() == ESelectionType.SOLDIERS;
	}

	@Override
	public final boolean isAttackable() {
		return strategy.isAttackable();
	}

	/**
	 * This method may only be called if this movable shall be informed about a movable that's in it's search radius.
	 *
	 * @param other
	 *            The other movable.
	 */
	@Override
	public final void informAboutAttackable(IAttackable other) {
		strategy.informAboutAttackable(other);
	}

	@Override
	public final void receiveHit(float hitStrength, ShortPoint2D attackerPos, byte attackingPlayer) {
		if (strategy.receiveHit()) {
			this.health -= hitStrength;
			if (health <= 0) {
				this.kill();
			}
		}

		player.showMessage(SimpleMessage.attacked(attackingPlayer, attackerPos));
	}

	@Override
	public boolean isTower() {
		return false;
	}

	private void checkPlayerOfCurrentPosition() {
		checkPlayerOfPosition(grid.getPlayerAt(position));
	}

	public void checkPlayerOfPosition(Player playerOfPosition) {
		if (playerOfPosition != player && movableType.needsPlayersGround() && strategy.getClass() != FleeStrategy.class) {
			setStrategy(new FleeStrategy(this));
		}
	}

	@Override
	public String toString() {
		return "Movable: " + id + " position: " + position + " player: " + player.playerId + " movableType: " + movableType
				+ " direction: " + direction + " material: " + materialType;
	}

	private enum EMovableState {
		PLAYING_ACTION,
		PATHING,
		DOING_NOTHING,
		GOING_SINGLE_STEP,
		WAITING,

		TAKE,
		DROP,

		DEAD,

		/**
		 * This state may only be used for debugging reasons!
		 */
		DEBUG_STATE
	}

}
