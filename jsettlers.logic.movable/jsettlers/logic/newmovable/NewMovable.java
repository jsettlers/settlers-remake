package jsettlers.logic.newmovable;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.interfaces.INewMovableGrid;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.MovableTimer;

public final class NewMovable implements ITimerable, IMovable, IPathCalculateable, Serializable {
	private static final long serialVersionUID = 2472076796407425256L;
	private static final float WALKING_PROGRESS_INCREASE = 1.0f / (Constants.MOVABLE_STEP_DURATION * Constants.MOVABLE_INTERRUPTS_PER_SECOND);

	private final INewMovableGrid grid;

	private ENewMovableState state = ENewMovableState.SLEEPING;

	private EMovableType movableType;
	private NewMovableStrategy strategy;

	private EMaterialType materialType = EMaterialType.NO_MATERIAL;
	private EAction movableAction = EAction.NO_ACTION;

	private float progress;

	private float progressIncrease;

	private EDirection direction;

	private ShortPoint2D position;

	private Path path;
	private boolean isRightstep;

	public NewMovable(INewMovableGrid grid, EMovableType movableType) {
		this.grid = grid;
		this.strategy = NewMovableStrategy.getStrategy(this, movableType);
		// The test movable has no images, so display a bearer
		this.movableType = movableType == EMovableType.TEST_MOVABLE ? EMovableType.BEARER : movableType;

		this.direction = EDirection.EAST; // TODO use random direction

		MovableTimer.add(this);
	}

	public final void positionAt(ShortPoint2D position) {
		assert grid.isFreeForMovable(position.getX(), position.getY()) : "given position not free for movable! " + position;

		if (this.position != null) {
			grid.leavePosition(this.position, this);
			setState(ENewMovableState.SLEEPING);
		}
		this.position = position;

		if (position != null) {
			grid.enterPosition(this.position, this);
			setState(ENewMovableState.DOING_NOTHING);
		}
	}

	@Override
	public void timerEvent() {
		switch (state) {
		case SLEEPING:
			return;

		case DOING_NOTHING:
			doingNothingAction();
			break;

		case PLAYING_ACTION:
			playingActionAction();
			break;

		case PATHING:
			pathingAction();
			break;

		}

		if (state == ENewMovableState.DOING_NOTHING) {
			strategy.action();
		}
	}

	private void pathingAction() {
		if (progress >= 1) {
			if (path.isFinished()) { // if path is finished, return from here
				setState(ENewMovableState.DOING_NOTHING);
				movableAction = EAction.NO_ACTION;
				path = null;
				return;
			}

			direction = EDirection.getDirection(position.getX(), position.getY(), path.nextX(), path.nextY());

			if (grid.isFreeForMovable(path.nextX(), path.nextY())) { // if we can go on to the next step
				movableAction = EAction.WALKING;
				progress -= 1;
				grid.leavePosition(position, this);
				this.position = path.getNextPos();
				grid.enterPosition(position, this);
				path.goToNextStep();
				isRightstep = !isRightstep;
			} else { // step not possible, so try it next time
				return;
			}
		} else {
			progress += WALKING_PROGRESS_INCREASE;
		}
	}

	private void playingActionAction() {
		progress += progressIncrease;
		if (progress > 1.01) { // > 1.01 ensures that the image for 100 % is also shown for one cycle
			setState(ENewMovableState.DOING_NOTHING);
			this.movableAction = EAction.NO_ACTION;
		}
	}

	private void doingNothingAction() {
		// TODO Auto-generated method stub

	}

	/**
	 * Converts this movable to a movable of the given {@link EMovableType}.
	 * 
	 * @param movableType
	 */
	final void convertTo(EMovableType movableType) {
		this.movableType = movableType;
		this.strategy = NewMovableStrategy.getStrategy(this, movableType);
		setState(ENewMovableState.DOING_NOTHING);
	}

	/**
	 * Sets the material this movable is carrying to the given one.
	 * 
	 * @param materialType
	 */
	final void setMaterial(EMaterialType materialType) {
		assert materialType != null : "MaterialType may not be null";
		this.materialType = materialType;
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
		this.movableAction = movableAction;
		setState(ENewMovableState.PLAYING_ACTION);
		this.progressIncrease = 1.0f / (duration * Constants.MOVABLE_INTERRUPTS_PER_SECOND);
		this.progress = 0;
	}

	/**
	 * Lets this movable look in the given direction.
	 * 
	 * @param direction
	 */
	final void lookInDirection(EDirection direction) {
		this.direction = direction;
	}

	final boolean goToPos(ShortPoint2D targetPos) {
		Path path = grid.calculatePathTo(this, targetPos);
		if (path == null) {
			return false;
		} else {
			this.path = path;
			setState(ENewMovableState.PATHING);
			this.movableAction = EAction.NO_ACTION;
			progress = 1;
			return true;
		}
	}

	/**
	 * Sets the state to the given one and resets the movable to a clean start of this state.
	 * 
	 * @param newState
	 */
	private final void setState(ENewMovableState newState) {
		this.state = newState;
	}

	@Override
	/**
	 * kills this movable.
	 */
	public void kill() {
		MovableTimer.remove(this);
	}

	@Override
	public byte getPlayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		// TODO Auto-generated method stub
	}

	@Override
	public ESelectionType getSelectionType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSoundPlayed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSoundPlayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public EAction getAction() {
		return movableAction;
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
		return materialType;
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isRightstep() {
		return isRightstep;
	}

	@Override
	public boolean needsPlayersGround() {
		return movableType.needsPlayersGround();
	}

}
