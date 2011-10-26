package jsettlers.logic.movable;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;

public abstract class PathableStrategy extends MovableStrategy implements IPathCalculateable {

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
			calculatePathTo(gotoJob.getFirstPos());
			gotoJob = null;
			return true;
		} else {
			return false;
		}
	}

	private boolean goPathStep() {
		if (path != null) {
			if (!path.isFinished()) {
				ISPosition2D nextTile = path.nextStep();
				super.goToTile(nextTile);
				return true;
			} else {
				path = null;
				pathFinished();
				return true;
			}
		} else
			return false;
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
			this.gotoJob = job;
		}
	}

	protected abstract boolean isGotoJobable();

}
