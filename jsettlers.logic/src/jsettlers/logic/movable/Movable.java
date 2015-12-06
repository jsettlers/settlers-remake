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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.graphics.messages.SimpleMessage;
import jsettlers.input.IGuiMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.AbstractStrategyGrid;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IAttackableMovable;
import jsettlers.logic.movable.interfaces.IDebugable;
import jsettlers.logic.movable.interfaces.IIDable;
import jsettlers.logic.movable.strategies.FleeStrategy;
import jsettlers.logic.movable.strategies.soldiers.SoldierStrategy;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * Central Movable class of JSettlers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Movable implements IScheduledTimerable, IPathCalculatable, IIDable, IDebugable, Serializable, IViewDistancable, IGuiMovable,
		IAttackableMovable {
	private static final long serialVersionUID = 2472076796407425256L;
	private static final HashMap<Integer, Movable> movablesByID = new HashMap<Integer, Movable>();
	private static final ConcurrentLinkedQueue<Movable> allMovables = new ConcurrentLinkedQueue<Movable>();
	private static int nextID = Integer.MIN_VALUE;

	private final AbstractMovableGrid grid;
	private final int id;

	private EMovableState state = EMovableState.DOING_NOTHING;

	private EMovableType movableType;
	private MovableStrategy strategy;
	private Player player;

	private EMaterialType materialType = EMaterialType.NO_MATERIAL;
	private EAction movableAction = EAction.NO_ACTION;
	private EDirection direction;

	private int animationStartTime;
	private short animationDuration;

	private ShortPoint2D position;

	private ShortPoint2D moveToRequest = null;
	private Path path;

	private float health;
	private boolean visible = true;
	private boolean enableNothingToDo = true;
	private Movable pushedFrom;

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

		this.direction = EDirection.values[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];

		RescheduleTimer.add(this, Constants.MOVABLE_INTERRUPT_PERIOD);

		this.id = nextID++;
		movablesByID.put(this.id, this);
		allMovables.offer(this);

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
		movablesByID.put(this.id, this);
		allMovables.add(this);
		nextID = Math.max(nextID, this.id + 1);
	}

	/**
	 * Tests if this movable can receive moveTo requests and if so, directs it to go to the given position.
	 * 
	 * @param targetPosition
	 */
	public final void moveTo(ShortPoint2D targetPosition) {
		if (movableType.isMoveToAble() && strategy.isMoveToAble()) {
			this.moveToRequest = targetPosition;
		}
	}

	public void leavePosition() {
		if (!enableNothingToDo) {
			return;
		}

		int offset = MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS);

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.values[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (goInDirection(currDir, false)) {
				break;
			} else {
				Movable movableAtPos = grid.getMovableAt(currDir.getNextTileX(position.x), currDir.getNextTileY(position.y));
				if (movableAtPos != null) {
					movableAtPos.push(this);
				}
			}
		}
	}

	@Override
	public int timerEvent() {
		if (health <= 0) {
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
			if (this.movableAction != EAction.RAISE_UP) {
				break;
			} // TAKE and DROP are finished if we get here and we the action is RAISE_UP, otherwise continue with second part.

		case WAITING:
		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
			state = EMovableState.DOING_NOTHING; // the action is finished, as the time passed
			movableAction = EAction.NO_ACTION;
			break;
		default:
			break;
		}

		if (moveToRequest != null) {
			switch (state) {
			case PATHING:
				// if we're currently pathing, stop former pathing and calculate a new path
				setState(EMovableState.DOING_NOTHING);
				this.movableAction = EAction.NO_ACTION;
				this.path = null;

			case DOING_NOTHING:
				ShortPoint2D oldTargetPos = path != null ? path.getTargetPos() : null;
				ShortPoint2D oldPos = position;
				boolean foundPath = goToPos(moveToRequest); // progress is reset in here
				moveToRequest = null;

				if (foundPath) {
					this.strategy.moveToPathSet(oldPos, oldTargetPos, path.getTargetPos());
					return animationDuration; // we already follow the path and initiated the walking
				} else {
					break;
				}

			default:
				break;
			}
		}

		switch (state) {
		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
			setState(EMovableState.DOING_NOTHING);
			this.movableAction = EAction.NO_ACTION;
			break;

		case PATHING:
			pathingAction();
			break;

		case TAKE:
			grid.takeMaterial(position, takeDropMaterial);
			setMaterial(takeDropMaterial);
			playAnimation(EAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION);
			break;
		case DROP:
			if (takeDropMaterial != null && takeDropMaterial != EMaterialType.NO_MATERIAL) {
				grid.dropMaterial(position, takeDropMaterial, strategy.offerDroppedMaterial());
			}
			setMaterial(EMaterialType.NO_MATERIAL);
			playAnimation(EAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION);
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
		if (!path.hasNextStep() || !strategy.checkPathStepPreconditions(path.getTargetPos(), path.getStep())) {
			// if path is finished, or canceled by strategy return from here
			setState(EMovableState.DOING_NOTHING);
			movableAction = EAction.NO_ACTION;
			path = null;
			checkPlayerOfCurrentPosition(); // TODO: this should be in timerEvent
			return;
		}

		direction = EDirection.getDirection(position.x, position.y, path.nextX(), path.nextY());

		Movable blockingMovable = grid.getMovableAt(path.nextX(), path.nextY());
		if (blockingMovable == null) { // if we can go on to the next step
			if (!grid.isValidNextPathPosition(this, path.getNextPos(), path.getTargetPos())) { // next position is invalid
				Path newPath = grid.calculatePathTo(this, path.getTargetPos()); // try to find a new path
				if (newPath == null) { // no path found
					setState(EMovableState.DOING_NOTHING);
					movableAction = EAction.NO_ACTION;
					strategy.pathAborted(path.getTargetPos()); // inform strategy
					path = null;
					return;
				} else {
					this.path = newPath; // continue with new path
				}
			}

			goSinglePathStep();
		} else { // step not possible, so try it next time
			movableAction = EAction.NO_ACTION;
			boolean pushedSuccessful = blockingMovable.push(this);
			if (!pushedSuccessful) {
				path = strategy.findWayAroundObstacle(direction, position, path);
				animationDuration = Constants.MOVABLE_INTERRUPT_PERIOD; // recheck shortly
			} else if (movableAction == EAction.NO_ACTION) {
				animationDuration = Constants.MOVABLE_INTERRUPT_PERIOD; // recheck shortly
			} // else: push initiated our next step
		}
	}

	private void goSinglePathStep() {
		initGoingSingleStep(path.getNextPos());
		path.goToNextStep();
	}

	private void initGoingSingleStep(ShortPoint2D position) {
		playAnimation(EAction.WALKING, movableType.getStepDurationMs());
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
			if (this.goInDirection(EDirection.getApproxDirection(0, 0, dx, dy), false)) {
				return true;
			} else {
				return false;
			}
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
	private boolean push(Movable pushingMovable) {
		if (health <= 0) {
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
				if (pushingMovable.path == null || !pushingMovable.path.hasNextStep()) {
					return false; // the other movable just pushed to get space, we can't do anything for it here.
				} else { // exchange positions
					EDirection directionToPushing = EDirection.getDirection(position, pushingMovable.getPos());
					pushingMovable.goSinglePathStep(); // if no free direction found, exchange the positions of the movables
					goInDirection(directionToPushing, false);
					return true;
				}
			}

		case PATHING:
			if (pushingMovable.path == null || !pushingMovable.path.hasNextStep()) {
				return false; // the other movable just pushed to get space, so we can't do anything for it in this state.
			}

			if (animationStartTime + animationDuration <= MatchConstants.clock().getTime() && this.path.hasNextStep()) {
				ShortPoint2D nextPos = path.getNextPos();
				if (pushingMovable.position == nextPos) { // two movables going in opposite direction and wanting to exchange positions
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
							pushingMovable = pushingMovable.pushedFrom;
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

	private boolean goToRandomDirection(Movable pushingMovable) {
		int offset = MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS);
		EDirection pushedFromDir = EDirection.getDirection(this.getPos(), pushingMovable.getPos());

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.values[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (currDir != pushedFromDir && goInDirection(currDir, false)) {
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
	final void playAction(EAction movableAction, float duration) {
		assert state == EMovableState.DOING_NOTHING : "can't do playAction() if state isn't DOING_NOTHING. curr state: " + state;

		playAnimation(movableAction, (short) (duration * 1000));
		setState(EMovableState.PLAYING_ACTION);
		this.soundPlayed = false;
	}

	private void playAnimation(EAction movableAction, short duration) {
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

			playAnimation(EAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION);
			setState(EMovableState.TAKE);
			return true;
		} else {
			return false;
		}
	}

	final void drop(EMaterialType materialToDrop) {
		this.takeDropMaterial = materialToDrop;

		playAnimation(EAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION);
		setState(EMovableState.DROP);
	}

	/**
	 * 
	 * @param sleepTime
	 *            time to sleep in milliseconds
	 */
	final void wait(short sleepTime) {
		assert state == EMovableState.DOING_NOTHING : "can't do sleep() if state isn't DOING_NOTHING. curr state: " + state;

		playAnimation(EAction.NO_ACTION, sleepTime);
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
	 * @param force
	 *            If true, the step will be forced and the method will always return true.
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	final boolean goInDirection(EDirection direction, boolean force) {
		ShortPoint2D pos = direction.getNextHexPoint(position);
		if (force || (grid.isValidPosition(this, pos) && grid.hasNoMovableAt(pos.x, pos.y))) {
			this.direction = direction;
			initGoingSingleStep(pos);
			setState(EMovableState.GOING_SINGLE_STEP);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Forces the movable to go a step in the given direction (if it is not blocked).
	 * 
	 * @param direction
	 *            direction to go
	 */
	final void forceGoInDirection(EDirection direction) {
		ShortPoint2D targetPos = direction.getNextHexPoint(position);
		this.direction = direction;
		setState(EMovableState.PATHING);
		this.followPath(new Path(targetPos));
	}

	/**
	 * 
	 * @return {@link AbstractStrategyGrid} that can be used by the strategy to gain informations from the grid.
	 */
	public final AbstractStrategyGrid getStrategyGrid() {
		return grid;
	}

	final void setPos(ShortPoint2D position) {
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
	 * @return
	 */
	final boolean preSearchPath(boolean dikjstra, short centerX, short centerY, short radius, ESearchType searchType) {
		assert state == EMovableState.DOING_NOTHING : "this method can only be invoked in state DOING_NOTHING";

		if (dikjstra) {
			this.path = grid.searchDijkstra(this, centerX, centerY, radius, searchType);
		} else {
			this.path = grid.searchInArea(this, centerX, centerY, radius, searchType);
		}

		return path != null;
	}

	final void followPresearchedPath() {
		assert this.path != null : "path mustn't be null to be able to followPresearchedPath()!";
		followPath(this.path);
	}

	final void enableNothingToDoAction(boolean enable) {
		this.enableNothingToDo = enable;
	}

	final boolean isValidPosition(ShortPoint2D position) {
		return grid.isValidPosition(this, position);
	}

	void abortPath() {
		setState(EMovableState.DOING_NOTHING);
		movableAction = EAction.NO_ACTION;
		path = null;
	}

	boolean isOnOwnGround() {
		return grid.getPlayerAt(position) == player;
	}

	private void followPath(Path path) {
		this.path = path;
		setState(EMovableState.PATHING);
		this.movableAction = EAction.NO_ACTION;
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
	 * Used for networking to identify movables over the network.
	 * 
	 * @param id
	 *            id to be looked for
	 * @return returns the movable with the given ID<br>
	 *         or null if the id can not be found
	 */
	public final static Movable getMovableByID(int id) {
		return movablesByID.get(id);
	}

	public final static ConcurrentLinkedQueue<Movable> getAllMovables() {
		return allMovables;
	}

	public static void resetState() {
		allMovables.clear();
		movablesByID.clear();
		nextID = Integer.MIN_VALUE;
	}

	/**
	 * kills this movable.
	 */
	@Override
	public final void kill() {
		if (health <= -100) {
			return; // this movable already died.
		}

		grid.leavePosition(this.position, this);
		this.health = -200;
		this.strategy.strategyKilledEvent(path != null ? path.getTargetPos() : null);

		movablesByID.remove(this.getID());
		allMovables.remove(this);

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
	public final EAction getAction() {
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

		this.health = (this.health * newMovableType.getHealth()) / this.movableType.getHealth();
		this.movableType = newMovableType;
		setStrategy(MovableStrategy.getStrategy(this, newMovableType));
	}

	private void setStrategy(MovableStrategy newStrategy) {
		this.strategy.strategyKilledEvent(path != null ? path.getTargetPos() : null);
		this.strategy = newStrategy;
		this.movableAction = EAction.NO_ACTION;
		setState(EMovableState.DOING_NOTHING);
	}

	public final boolean setOccupyableBuilding(IOccupyableBuilding building) {
		if (canOccupyBuilding()) {
			return ((SoldierStrategy) strategy).setOccupyableBuilding(building);
		} else {
			return false;
		}
	}

	public final boolean canOccupyBuilding() {
		return movableType.getSelectionType() == ESelectionType.SOLDIERS;
	}

	@Override
	public final boolean isAttackable() {
		return movableType.isMoveToAble();
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
		this.health -= hitStrength;
		if (health <= 0) {
			this.kill();
		}

		player.showMessage(SimpleMessage.attacked(attackingPlayer, attackerPos));
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

	private static enum EMovableState {
		PLAYING_ACTION,
		PATHING,
		DOING_NOTHING,
		GOING_SINGLE_STEP,
		WAITING,

		TAKE,
		DROP,

		/**
		 * This state may only be used for debugging reasons!
		 */
		DEBUG_STATE
	}

}
