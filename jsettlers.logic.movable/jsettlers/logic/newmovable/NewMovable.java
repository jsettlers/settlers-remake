package jsettlers.logic.newmovable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import jsettlers.logic.algorithms.fogofwar.IViewDistancable;
import jsettlers.logic.algorithms.path.IPathCalculatable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.interfaces.AbstractNewMovableGrid;
import jsettlers.logic.newmovable.interfaces.AbstractStrategyGrid;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.newmovable.interfaces.IAttackableMovable;
import jsettlers.logic.newmovable.interfaces.IDebugable;
import jsettlers.logic.newmovable.interfaces.IIDable;
import jsettlers.logic.newmovable.strategies.FleeStrategy;
import jsettlers.logic.newmovable.strategies.soldiers.SoldierStrategy;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;
import networklib.synchronic.random.RandomSingleton;

/**
 * Central Movable class of JSettlers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NewMovable implements IScheduledTimerable, IPathCalculatable, IIDable, IDebugable, Serializable, IViewDistancable, IGuiMovable,
		IAttackableMovable {
	private static final long serialVersionUID = 2472076796407425256L;
	private static final float WALKING_PROGRESS_INCREASE = 1.0f / (Constants.MOVABLE_STEP_DURATION * Constants.MOVABLE_INTERRUPTS_PER_SECOND);
	private static final HashMap<Integer, NewMovable> movablesByID = new HashMap<Integer, NewMovable>();
	private static final ConcurrentLinkedQueue<NewMovable> allMovables = new ConcurrentLinkedQueue<NewMovable>();
	private static int nextID = Integer.MIN_VALUE;

	private final AbstractNewMovableGrid grid;
	private final int id;

	private ENewMovableState state = ENewMovableState.DOING_NOTHING;

	private EMovableType movableType;
	private NewMovableStrategy strategy;
	private Player player;

	private EMaterialType materialType = EMaterialType.NO_MATERIAL;
	private EAction movableAction = EAction.NO_ACTION;
	private EDirection direction;

	private float progress;
	private float progressIncrease;

	private ShortPoint2D position;

	private ShortPoint2D moveToRequest = null;
	private Path path;

	private float health = 1.0f;
	private boolean visible = true;
	private boolean enableNothingToDo = true;
	private NewMovable pushedFrom;

	private boolean isRightstep = false;
	private float doingNothingProbablity = 0.06f;
	private int delayCtr = 0;

	private transient boolean selected = false;
	private transient boolean soundPlayed = false;

	public NewMovable(AbstractNewMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		this.grid = grid;
		this.position = position;
		this.player = player;
		this.strategy = NewMovableStrategy.getStrategy(this, movableType);
		this.movableType = movableType;

		this.direction = EDirection.values[RandomSingleton.getInt(0, 5)];

		RescheduleTimer.add(this, Constants.MOVABLE_INTERRUPT_DELAY);

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

		int offset = RandomSingleton.getInt(0, EDirection.NUMBER_OF_DIRECTIONS - 1);

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.values[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (goInDirection(currDir)) {
				break;
			} else {
				NewMovable movableAtPos = grid.getMovableAt(currDir.getNextTileX(position.x), currDir.getNextTileY(position.y));
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

		switch (state) {

		case DOING_NOTHING:
			if (visible && enableNothingToDo) {
				doingNothingAction();
			}
			break;

		case GOING_SINGLE_STEP:
		case PLAYING_ACTION:
			progressCurrentAction();
			break;

		case PATHING:
			pathingAction();
			break;

		default:
			break;
		}

		if (moveToRequest != null) {
			switch (state) {
			case PATHING:
				if (progress < 1) {
					break;
				} // if we are pathing and finished a step, calculate new path
				setState(ENewMovableState.DOING_NOTHING); // this line is needed for assertions

			case DOING_NOTHING:
				ShortPoint2D targetPos = path != null ? path.getTargetPos() : null;
				ShortPoint2D oldPos = position;
				boolean foundPath = goToPos(moveToRequest); // progress is reset in here
				moveToRequest = null;

				if (foundPath) {
					this.strategy.moveToPathSet(oldPos, targetPos, path.getTargetPos());
				}

				break;

			default:
				break;
			}
		}

		if (state == ENewMovableState.DOING_NOTHING) {
			return strategy.action();
		} else {
			return Constants.MOVABLE_INTERRUPT_DELAY;
		}
	}

	private void pathingAction() {
		if (progress >= 1) {
			if (path.isFinished() || !strategy.checkPathStepPreconditions(path.getTargetPos(), path.getStep())) {
				// if path is finished, or canceled by strategy return from here
				setState(ENewMovableState.DOING_NOTHING);
				movableAction = EAction.NO_ACTION;
				path = null;
				checkPlayerOfCurrentPosition();
				return;
			}

			direction = EDirection.getDirection(position.x, position.y, path.nextX(), path.nextY());

			if (grid.hasNoMovableAt(path.nextX(), path.nextY())) { // if we can go on to the next step
				goSinglePathStep();
			} else { // step not possible, so try it next time
				boolean pushedSuccessful = grid.getMovableAt(path.nextX(), path.nextY()).push(this);
				if (!pushedSuccessful) {
					delayCtr++;
					if (delayCtr > 4) {
						delayCtr = 0;
						path = strategy.findWayAroundObstacle(direction, position, path);
					}
				}
				return;
			}
		} else {
			progress += WALKING_PROGRESS_INCREASE;
		}
	}

	private void goSinglePathStep() {
		initGoingSingleStep(path.getNextPos());
		path.goToNextStep();
	}

	private void initGoingSingleStep(ShortPoint2D position) {
		movableAction = EAction.WALKING;
		progress -= 1;
		grid.leavePosition(this.position, this);
		grid.enterPosition(position, this, false);
		this.position = position;
		isRightstep = !isRightstep;
	}

	private void progressCurrentAction() {
		progress += progressIncrease;
		if (progress >= 1) {
			setState(ENewMovableState.DOING_NOTHING);
			this.movableAction = EAction.NO_ACTION;
		}
	}

	private void doingNothingAction() {
		if (grid.isBlocked(position.x, position.y)) {
			Path newPath = grid.searchDijkstra(this, position.x, position.y, (short) 50, ESearchType.NON_BLOCKED_OR_PROTECTED);
			if (newPath == null) {
				kill();
			} else {
				followPath(newPath);
			}
		} else {
			float random = RandomSingleton.nextF();
			if (random <= doingNothingProbablity) {
				flockToDecentralize();
			} else if (random >= 1 - Constants.MOVABLE_TURN_PROBABILITY) {
				lookInDirection(direction.getNeighbor(2 * RandomSingleton.getInt(0, 1) - 1));
			}
		}
	}

	private void flockToDecentralize() {
		ShortPoint2D decentVector = grid.calcDecentralizeVector(position.x, position.y);
		int dx = direction.gridDeltaX + decentVector.x;
		int dy = direction.gridDeltaY + decentVector.y;

		if (ShortPoint2D.getOnGridDist(dx, dy) >= 2) {
			this.goInDirection(EDirection.getApproxDirection(0, 0, dx, dy));
			doingNothingProbablity = Math.min(doingNothingProbablity + 0.02f, 0.1f);
		} else {
			doingNothingProbablity = Math.max(doingNothingProbablity - 0.02f, 0.06f);
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
	private boolean push(NewMovable pushingMovable) {
		if (health <= 0) {
			return false;
		}

		switch (state) {
		case DOING_NOTHING:
			if (!enableNothingToDo) { // don't go to random direction if movable shouldn't do something in DOING_NOTHING
				return false;
			}

			if (!goToRandomDirection(pushingMovable)) { // try to find free direction
				if (pushingMovable.path == null || pushingMovable.path.isFinished()) {
					return false; // the other movable just pushed to get space, we can't do anything for it here.
				} else { // exchange positions
					EDirection directionToPushing = EDirection.getDirection(position, pushingMovable.getPos());
					pushingMovable.goSinglePathStep(); // if no free direction found, exchange movables positions
					forceGoInDirection(directionToPushing);
					return true;
				}
			} else {
				return false;
			}

		case PATHING:
			if (pushingMovable.path == null || pushingMovable.path.isFinished()) {
				return false; // the other movable just pushed to get space, so we can't do anything for it in this state.
			}

			if (this.progress >= 1 && !this.path.isFinished()) {
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
			return false; // we can't do anything

		case DEBUG_STATE:
			return false;

		default:
			assert false : "got pushed in unhandled state: " + state;
			return false;
		}
	}

	private boolean goToRandomDirection(NewMovable pushingMovable) {
		int offset = RandomSingleton.getInt(0, EDirection.NUMBER_OF_DIRECTIONS - 1);
		EDirection pushedFromDir = EDirection.getDirection(this.getPos(), pushingMovable.getPos());

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.values[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (currDir != pushedFromDir && goInDirection(currDir)) {
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
	 *            duration the animation should last (in seconds).
	 */
	final void playAction(EAction movableAction, float duration) {
		assert state == ENewMovableState.DOING_NOTHING : "can't do playAction() if state isn't DOING_NOTHING. curr state: " + state;

		this.movableAction = movableAction;
		setState(ENewMovableState.PLAYING_ACTION);
		this.progressIncrease = 1.0f / (duration * Constants.MOVABLE_INTERRUPTS_PER_SECOND);
		this.progress = 0;
		this.soundPlayed = false;
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
		assert state == ENewMovableState.DOING_NOTHING : "can't do goToPos() if state isn't DOING_NOTHING. curr state: " + state;

		Path path = grid.calculatePathTo(this, targetPos);
		if (path == null) {
			return false;
		} else {
			followPath(path);
			return true;
		}
	}

	/**
	 * Tries to go a step in the given direction.
	 * 
	 * @param direction
	 *            direction to go
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	final boolean goInDirection(EDirection direction) {
		ShortPoint2D pos = direction.getNextHexPoint(position);
		if (grid.isValidPosition(this, pos) && grid.hasNoMovableAt(pos.x, pos.y)) {
			initGoingSingleStep(pos);
			this.direction = direction;
			progressIncrease = WALKING_PROGRESS_INCREASE;
			setState(ENewMovableState.GOING_SINGLE_STEP);
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
		this.followPath(new Path(targetPos));
		setState(ENewMovableState.PATHING);
	}

	/**
	 * 
	 * @return {@link AbstractStrategyGrid} that can be used by the strategy to gain informations from the grid.
	 */
	final AbstractStrategyGrid getStrategyGrid() {
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
		assert state == ENewMovableState.DOING_NOTHING : "this method can only be invoked in state DOING_NOTHING";

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
		setState(ENewMovableState.DOING_NOTHING);
		movableAction = EAction.NO_ACTION;
		path = null;
	}

	private void followPath(Path path) {
		this.path = path;
		setState(ENewMovableState.PATHING);
		this.movableAction = EAction.NO_ACTION;
		progress = 1;
		pathingAction();
	}

	/**
	 * Sets the state to the given one and resets the movable to a clean start of this state.
	 * 
	 * @param newState
	 */
	private void setState(ENewMovableState newState) {
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
	public final static NewMovable getMovableByID(int id) {
		return movablesByID.get(id);
	}

	public final static ConcurrentLinkedQueue<NewMovable> getAllMovables() {
		return allMovables;
	}

	public static void dropAllMovables() {
		allMovables.clear();
		movablesByID.clear();
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
		return progress;
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
		System.out.println("debug");
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

		this.movableType = newMovableType;
		setStrategy(NewMovableStrategy.getStrategy(this, newMovableType));
	}

	private void setStrategy(NewMovableStrategy newStrategy) {
		this.strategy.strategyKilledEvent(path != null ? path.getTargetPos() : null);
		this.strategy = newStrategy;
		setState(ENewMovableState.DOING_NOTHING);
		this.progress = 0;
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

	private static enum ENewMovableState {
		PLAYING_ACTION,
		PATHING,
		DOING_NOTHING,
		GOING_SINGLE_STEP,

		/**
		 * This state may only be used for debugging reasons!
		 */
		DEBUG_STATE
	}

}
