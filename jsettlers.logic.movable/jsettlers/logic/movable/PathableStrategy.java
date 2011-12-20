package jsettlers.logic.movable;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;

public abstract class PathableStrategy extends MovableStrategy implements IPathCalculateable {
	private static final long serialVersionUID = -358317723044015560L;

	protected PathableStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	private Path path;
	private GotoJob gotoJob;

	@Override
	public byte getPlayer() { // needs to be public due to the interface
		return super.getPlayer();
	}

	@Override
	public ISPosition2D getPos() { // needs to be public due to the interface
		return super.getPos();
	}

	@Override
	public abstract boolean needsPlayersGround();

	@Override
	protected boolean noActionEvent() {
		return checkGotoJob();
	}

	@Override
	protected boolean actionFinished() {
		return checkGotoJob() || goPathStep();
	}

	private boolean checkGotoJob() {
		if (gotoJob != null) {
			calculatePathTo(gotoJob.getPosition());
			gotoJob = null;
			return true;
		} else {
			return false;
		}
	}

	private boolean goPathStep() {
		if (path != null) {
			if (!path.isFinished()) {
				if (checkGoStepPrecondition()) {
					ISPosition2D nextTile = path.nextStep();
					super.goToTile(nextTile);
				} else {
					abortPath();
					pathAbortedEvent();
				}
			} else {
				path = null;
				pathFinished();
			}
			return true;
		} else
			return false;
	}

	/**
	 * This methods can be used by subclasses if they abort a path via {@link #checkGoStepPrecondition()} to handle the abort.
	 * <p />
	 * NOTE: THIS METHOD MUST SET a new action to the movable.
	 */
	protected void pathAbortedEvent() {
		super.setAction(EAction.NO_ACTION, -1);
	}

	/**
	 * This method can be used by subclasses to do checks and maybe interrupt a path before the movable does the next step.
	 * 
	 * @return true if the next step can be done,<br>
	 *         false if the path should be aborted. ( {@link #pathAbortedEvent() } will be called)
	 */
	protected boolean checkGoStepPrecondition() {
		return true;
	}

	protected abstract void pathFinished();

	protected void abortPath() {
		this.path = null;
	}

	protected boolean isFollowingPath() {
		return path != null && !path.isFinished();
	}

	protected void calculatePathTo(ISPosition2D target) {
		Path path = super.getGrid().getAStar().findPath(this, target);
		initCalculatedPath(path);
	}

	private void initCalculatedPath(Path path) {
		if (path == null) {
			this.path = null;
			pathRequestFailed();
		} else {
			setCalculatedPath(path);
			goPathStep();
		}
	}

	/**
	 * This method sets the given path as the path this movable is following.<br>
	 * NOTE: this method can be overridden to delay setting of the path or getting an event when a path is set.
	 * 
	 * @param path
	 *            path to be set
	 */
	protected void setCalculatedPath(Path path) {
		this.path = path;
	}

	protected void calculateDijkstraPath(ISPosition2D centerPos, short maxRadius, ESearchType type) {
		Path path = super.getGrid().getDijkstra().find(this, centerPos.getX(), centerPos.getY(), (short) 1, maxRadius, type);
		initCalculatedPath(path);
	}

	protected void calculateInAreaPath(ISPosition2D centerPos, short searchRadius, ESearchType type) {
		ISPosition2D targetPos = super.getGrid().getInAreaFinder().find(this, centerPos.getX(), centerPos.getY(), searchRadius, type);
		if (targetPos == null) {
			pathRequestFailed();
		} else {
			calculatePathTo(targetPos);
		}
	}

	protected abstract void pathRequestFailed();

	@Override
	protected final void setGotoJob(GotoJob job) {
		if (isGotoJobable()) {
			doPreGotoJobActions();
			this.gotoJob = job;
		}
	}

	protected void doPreGotoJobActions() {
	}

	protected abstract boolean isGotoJobable();

	@Override
	protected void leaveBlockedPosition() {
		calculateDijkstraPath(super.getPos(), (short) 200, ESearchType.NON_BLOCKED_OR_PROTECTED);
	}

	/**
	 * Gives the position the movable is currently walking to.
	 * 
	 * @return position this movable is walking to<br>
	 *         null if the movable is currently not following any path.
	 */
	protected ISPosition2D getTargetPos() {
		if (path != null) {
			return path.getTargetPos();
		} else {
			return null;
		}
	}

	/**
	 * NOTE: subclasses overriding this method MUST call super.stopOrStartWorking(boolean)!!
	 */
	@Override
	protected void stopOrStartWorking(boolean stop) {
		if (stop && isFollowingPath() && isPathStopable()) {
			abortPath();
		}
	}

	/**
	 * This method specifies what to do if this {@link PathableStrategy} is currently following a path and stopOrStartWorking(boolean) is called.
	 * 
	 * @return if the result is true, the path will be abort<br>
	 *         if false, nothing will happend.
	 */
	protected abstract boolean isPathStopable();

}
