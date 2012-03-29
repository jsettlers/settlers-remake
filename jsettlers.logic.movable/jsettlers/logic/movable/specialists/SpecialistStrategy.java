package jsettlers.logic.movable.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public abstract class SpecialistStrategy extends PathableStrategy {
	private static final long serialVersionUID = -5571213409776666251L;

	private static final byte SEARCH_RADIUS = (byte) 15;

	private ShortPoint2D centerPos;
	private boolean going = false;

	private boolean secondActionDone;

	public SpecialistStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	public final boolean needsPlayersGround() {
		return false;
	}

	@Override
	protected final void setCalculatedPath(Path path) {
		super.getGrid().setMarked(path.getTargetPos(), true);
		super.setCalculatedPath(path);
	}

	@Override
	protected final void pathFinished() {
		if (centerPos == null) {
			centerPos = super.getPos();
		}

		if (canWorkOnCurrPos()) {
			super.setAction(EAction.ACTION1, getAction1Duration());
			going = false;
		} else {
			unmarkTargetPos();
			requestNewPath();
		}
	}

	protected abstract boolean canWorkOnCurrPos();

	protected abstract float getAction1Duration();

	@Override
	protected final boolean actionFinished() {
		if (!super.actionFinished()) {
			if (centerPos != null) {
				if (!going) {
					if (canWorkOnCurrPos()) {
						if (hasTwoActions() && !secondActionDone) {
							secondActionDone = true;
							super.setAction(EAction.ACTION2, getAction2Duration());
							return true; // needs to be done here to prevent unmarking
						} else {
							executeAction();
							secondActionDone = false; // reset for next time
						}
					}
					unmarkTargetPos();
					requestNewPath();
				} else {
					super.setAction(EAction.ACTION1, getAction1Duration());
					going = false;
				}
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}

		return true;
	}

	protected abstract float getAction2Duration();

	protected abstract boolean hasTwoActions();

	protected abstract void executeAction();

	private void stopWorkAndReleaseMarked() {
		unmarkTargetPos();
		centerPos = null;
		going = true;
	}

	private final void unmarkTargetPos() {
		ShortPoint2D targetPos = super.getTargetPos();
		if (targetPos != null) {
			super.getGrid().setMarked(targetPos, false);
		} else {
			super.getGrid().setMarked(super.getPos(), false);
		}
	}

	@Override
	protected void doPreGotoJobActions() {
		stopWorkAndReleaseMarked();
	}

	private final void requestNewPath() {
		ShortPoint2D bestNeighbour = getCloseForeignTile();

		if (bestNeighbour != null) {
			super.getGrid().setMarked(bestNeighbour, true);
			initGoingToPosition(bestNeighbour);
		} else {
			centerPos = super.getPos();
			super.calculateDijkstraPath(centerPos, SEARCH_RADIUS, getSearchType());
		}
		going = true;
	}

	protected void initGoingToPosition(ShortPoint2D bestNeighbour) {
		super.goToTile(bestNeighbour);
	}

	protected abstract ESearchType getSearchType();

	protected abstract ShortPoint2D getCloseForeignTile();

	@Override
	protected final void stopOrStartWorking(boolean stop) {
		if (stop) {
			stopWorkAndReleaseMarked();
			this.centerPos = null;
		} else {
			this.centerPos = super.getPos();
			this.going = false;
		}
		super.stopOrStartWorking(stop);
	}

	@Override
	protected final void pathRequestFailed() {
		stopOrStartWorking(true);
		super.setAction(EAction.NO_ACTION, -1);
	}

	@Override
	protected final boolean isGotoJobable() {
		return true;
	}

	@Override
	protected final boolean isPathStopable() {
		return true;
	}

	@Override
	protected final void killedEvent() {
		unmarkTargetPos();
	}

	protected final ShortPoint2D getCenterPos() {
		return centerPos;
	}

	@Override
	protected final void convertActionEvent() {
		this.unmarkTargetPos();
	}
}
