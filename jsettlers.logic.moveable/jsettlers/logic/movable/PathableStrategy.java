package jsettlers.logic.movable;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.map.hex.HexGrid;

public abstract class PathableStrategy extends MovableStrategy implements IPathCalculateable {
	private static HexAStar astar = null;

	private static HexAStar getAStar() {
		if (astar == null) {
			astar = new HexAStar(HexGrid.get());
		}
		return astar;
	}

	private static DijkstraAlgorithm dijkstra = null;

	protected static DijkstraAlgorithm getDijkstra() {
		if (dijkstra == null) {
			dijkstra = new DijkstraAlgorithm(HexGrid.get());
		}
		return dijkstra;
	}

	private static InAreaFinder inAreaFinder = null;

	private static InAreaFinder getInAreaFinder() {
		if (inAreaFinder == null) {
			inAreaFinder = new InAreaFinder(HexGrid.get());
		}
		return inAreaFinder;
	}

	protected PathableStrategy(Movable movable) {
		super(movable);
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
		Path path = getAStar().findPath(this, target);
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

	protected void calculateDijkstraPath(ISPosition2D centerPos, short searchRadius, ESearchType type) {
		ISPosition2D targetPos = getDijkstra().find(this, centerPos.getX(), centerPos.getY(), searchRadius, type);
		if (targetPos == null) {
			pathRequestFailed();
		} else {
			calculatePathTo(targetPos);
		}
	}

	protected void calculateInAreaPath(ISPosition2D centerPos, short searchRadius, ESearchType type) {
		ISPosition2D targetPos = getInAreaFinder().find(this, centerPos.getX(), centerPos.getY(), searchRadius, type);
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
