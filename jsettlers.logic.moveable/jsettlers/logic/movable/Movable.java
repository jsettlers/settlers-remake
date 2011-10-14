package jsettlers.logic.movable;

import java.util.HashMap;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.player.ActivePlayer;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.MovableTimer;
import random.RandomSingleton;

public class Movable implements IHexMovable, ITimerable, IMovable, IIDable, IDebugable {
	private static int nextID = Integer.MIN_VALUE;
	private static final HashMap<Integer, Movable> movablesByID = new HashMap<Integer, Movable>();

	private final int id;

	private final IMovableGrid grid;
	private ISPosition2D pos;
	private byte player;
	private float health = 1.0f;
	private EDirection direction;

	private float progress = 1f;
	private EAction action = EAction.NO_ACTION;
	private EMovableState state = EMovableState.NO_ACTION;

	private EMaterialType material = EMaterialType.NO_MATERIAL;

	private boolean selected;

	private MovableStrategy strategy;
	private float progressIncrease = 0.1f;
	private ISPosition2D nextTile;
	private IHexMovable pushedFrom;

	public Movable(IMovableGrid grid, ISPosition2D pos, EMovableType type, byte player) {
		this.grid = grid;
		this.pos = pos;
		this.setDirection(EDirection.values()[RandomSingleton.getInt(0, 5)]);
		this.player = player;
		this.strategy = MovableStrategy.getTypeStrategy(grid, type, this);

		this.id = getNewID();

		MovableTimer.add(this);
		movablesByID.put(id, this);
	}

	private int getNewID() {
		return nextID++;
	}

	@Override
	public byte getPlayer() {

		return player;
	}

	@Override
	public EMovableType getMovableType() {
		return strategy.getMovableType();
	}

	@Override
	public EAction getAction() {
		return action;
	}

	@Override
	public EDirection getDirection() {
		return direction;
	}

	@Override
	public float getMoveProgress() {
		return progress;
	}

	@Override
	public EMaterialType getMaterial() {
		return material;
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	@Override
	public void kill() {
		MovableTimer.remove(this);
		this.health = 0;
		grid.movableLeft(pos, this);
		movablesByID.remove(this.getID());
		ActivePlayer.get().reduceOwned(player, this.strategy.getMovableType());

		grid.getMapObjectsManager().addSelfDeletingMapObject(pos, EMapObjectType.GHOST, 1, player);
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void hit(float strength) {
		health -= strength * 0.1f;
		if (health <= 0) {
			this.kill();
		} else {
			strategy.gotHitEvent();
		}
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public void push(IHexMovable from) {
		if (from == null) {
			if (state == EMovableState.NO_ACTION) {
				goToRandomDirection(null);
			}
			return;
		}

		switch (state) {
		case NO_ACTION:
			goToRandomDirection(from);
			break;

		case WAITING_FOR_FREE_TILE:
			if (from.getPos().equals(this.nextTile)) {
				if (from.getNextTile() != null) { // if the other one didn't just push us in nothingToDo action
					// exchange the two movables
					initGoingToNextTile();
					from.initGoingToNextTile();
				}

			} else {
				IHexMovable movableOnNextTile = grid.getMovable(nextTile);
				if (movableOnNextTile != null) { // next tile is free, we can move!
					state = EMovableState.PUSHED_AND_WAITING;
					pushedFrom = from;
					movableOnNextTile.push(this);
					pushedFrom = null;

					if (state == EMovableState.PUSHED_AND_WAITING)
						state = EMovableState.WAITING_FOR_FREE_TILE;
				}
			}
			break;

		case PUSHED_AND_WAITING:
			if (pushedFrom.getPos().equals(from.getPos())) {
				while (from != this) {
					from.initGoingToNextTile();
					from = from.getPushedFrom();
				}
				this.initGoingToNextTile();
			} else {
				pushedFrom = from;
			}
			break;

		case EXECUTING_ACTION:
		case FINISHED_ACTION:
			// no reaktion here
			break;
		}
	}

	private void goToRandomDirection(IHexMovable from) {
		// choose a random direction and go to it
		int offset = RandomSingleton.getInt(0, 5);
		int directions = EDirection.values().length;

		for (int i = 0; i < directions; i++) {
			ISPosition2D newPos = EDirection.values()[(i + offset) % directions].getNextHexPoint(pos);
			if (!grid.isBlocked(newPos.getX(), newPos.getY()) && grid.getMovable(newPos) == null) {
				goToTile(newPos);
				return;
			}
		}

		if (from != null) {
			this.goToTile(EDirection.getApproxDirection(pos, from.getPos()).getNextHexPoint(pos));
			// this ensures that there is no issue with an movable that pushed and left its position -> direction == null
		}
	}

	@Override
	public void initGoingToNextTile() {
		grid.movableLeft(pos, this);
		grid.movableEntered(this.nextTile, this);
		this.pos = this.nextTile;
		this.nextTile = null;
		this.progress = 0;
		this.action = EAction.WALKING;
		this.state = EMovableState.EXECUTING_ACTION;
		this.progressIncrease = getProgressIncrease(Constants.MOVABLE_STEP_DURATION);
	}

	@Override
	public void timerEvent() {
		// System.out.println(state + "\t" + action + "\t\t" + pos + "\t" + nextTile + "\t" + progress);
		switch (state) {
		case NO_ACTION:
			nothingTodoAction();
			strategy.noActionEvent();
			break;

		case PUSHED_AND_WAITING:
		case WAITING_FOR_FREE_TILE:
			IHexMovable movableOnNextTile = grid.getMovable(nextTile);
			if (movableOnNextTile == null) { // next tile is free, we can move!
				initGoingToNextTile();
			} else {
				movableOnNextTile.push(this);
				break;
			}

		case EXECUTING_ACTION:
			progress += progressIncrease;
			if (progress >= 1) {
				state = EMovableState.FINISHED_ACTION;
				strategy.actionFinished();
				if (state == EMovableState.FINISHED_ACTION) {
					System.out.println("blöd");
					strategy.actionFinished();
				}
			}
			break;

		case FINISHED_ACTION:
			System.out.println("finished_action");
			break;

		}
	}

	private void nothingTodoAction() {
		if (RandomSingleton.nextF() < Constants.MOVABLE_TURN_PROBABILITY) {
			this.setDirection(direction.getNeighbor(RandomSingleton.getInt(-1, 1)));
		} else if (RandomSingleton.nextF() < Constants.MOVABLE_NO_ACTION_STEP_PROBABILITY) {
			for (EDirection curr : EDirection.values()) { // push all movables around this movable
				IHexMovable movable = grid.getMovable(curr.getNextHexPoint(pos));
				if (movable != null) {
					movable.push(this);
				}
			}
		}
	}

	void goToTile(ISPosition2D nextTile) {
		assert nextTile != null : "next tile mustn't be null!";

		EDirection newDir = EDirection.getDirection(pos, nextTile);
		if (newDir != null) { // TODO @Andreas: check if this is needed
			setDirection(newDir);
			this.nextTile = nextTile;
			this.state = EMovableState.WAITING_FOR_FREE_TILE;
		} else {
			System.out.println();
		}
	}

	/**
	 * @param action
	 *            action to be done
	 * @param duration
	 *            duration of the action<br>
	 *            NOTE: the duration will only be used if the action is of type TAKE, DROP or WALKING
	 */
	void setAction(EAction action, float duration) {
		if (state != EMovableState.FINISHED_ACTION && state != EMovableState.NO_ACTION) {
			throw new IllegalStateException("Current action has not been finished yet: " + this.state + "  " + action + "   " + getMovableType());
		}

		this.action = action;
		this.progressIncrease = getProgressIncrease(duration);

		switch (action) {
		case NO_ACTION:
			this.state = EMovableState.NO_ACTION;
			assert duration < 0 : "when going to no action, the duration needs to be negative!";
			this.progress = 0;
			break;
		case WALKING:
			assert false : "use goToTile to set the movable to walk";
			break;
		case TAKE:
		case DROP:
		case ACTION1:
		case ACTION2:
			this.progress = 0;
			this.state = EMovableState.EXECUTING_ACTION;
			break;
		}

	}

	private float getProgressIncrease(float duration) {
		return 1f / (duration * (Constants.MOVABLE_INTERRUPTS_PER_SECOND));
	}

	void setMaterial(EMaterialType material) {
		assert material != null : "material can't be set to null!!!";
		this.material = material;
	}

	public void setGotoJob(GotoJob gotoJob) {
		// if (nextTile != null) { TODO @Andreas check if this is still needed
		// action = EAction.CALCULATING;
		// progressIncrease = -1;
		// nextTile = null;
		// }
		this.strategy.setGotoJob(gotoJob);
	}

	void setStrategy(MovableStrategy strategy) {
		ActivePlayer.get().reduceOwned(player, this.strategy.getMovableType());
		this.strategy = strategy;
	}

	void setPos(ISPosition2D pos) {
		grid.movableLeft(this.pos, this);
		this.pos = pos;
		this.progress = 0;
	}

	void setVisible(boolean visible) {
		if (visible) {
			grid.movableEntered(pos, this);
		} else {
			grid.movableLeft(pos, this);
		}
	}

	@Override
	public int getID() {
		return id;
	}

	/**
	 * Used for networking, to identify movables over the network.
	 * 
	 * @param id
	 *            id to be looked for
	 * @return returns the movable with the given ID<br>
	 *         or null if the id can not be found
	 */
	public static Movable getMovableByID(int id) {
		return movablesByID.get(id);
	}

	void setDirection(EDirection direction) {
		assert direction != null : "direction can never be null";
		this.direction = direction;
	}

	@Override
	public void debug() {
		System.out.println("debug");
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		strategy.stopOrStartWorking(stop);
	}

	@Override
	public IHexMovable getPushedFrom() {
		return pushedFrom;
	}

	@Override
	public ISPosition2D getNextTile() {
		return nextTile;
	}

	/**
	 * Lets this movable wait for the given period of time. After the time elapsed, actionFinished() will be called.
	 * 
	 * @param time
	 *            time to be waited.
	 */
	void setWaiting(float time) {
		assert state == EMovableState.NO_ACTION || state == EMovableState.FINISHED_ACTION : "can't wait in this state: " + state;

		this.progressIncrease = getProgressIncrease(time);
		this.progress = 0;
		this.action = EAction.NO_ACTION;
		this.state = EMovableState.EXECUTING_ACTION;
	}
}
