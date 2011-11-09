package jsettlers.logic.movable.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class PioneerStrategy extends PathableStrategy {
	private static final byte SEARCH_RADIUS = (byte) 15;

	private ISPosition2D centerPos;
	private boolean going = false;

	public PioneerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	public boolean needsPlayersGround() {
		return false;
	}

	@Override
	protected void setCalculatedPath(Path path) {
		super.getGrid().setMarked(path.getTargetPos(), true);
		super.setCalculatedPath(path);
	}

	@Override
	protected void pathFinished() {
		if (centerPos == null) {
			centerPos = super.getPos();
		}

		if (super.getGrid().getPlayerAt(super.getPos()) != super.getPlayer()) {
			super.setAction(EAction.ACTION1, Constants.PIONEER_ACTION_DURATION);
			going = false;
		} else {
			requestNewPath();
		}
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (centerPos != null) {
				if (!going) {
					unmarkTargetPos();
					super.getGrid().changePlayerAt(super.getPos(), super.getPlayer());
					requestNewPath();
				} else {
					super.setAction(EAction.ACTION1, Constants.PIONEER_ACTION_DURATION);
					going = false;
				}
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}

		return true;
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.PIONEER;
	}

	@Override
	protected void doPreGotoJobActions() {
		stopWorkAndReleaseMarked();
	}

	private void stopWorkAndReleaseMarked() {
		unmarkTargetPos();
		centerPos = null;
		going = true;
	}

	@Override
	protected boolean isGotoJobable() {
		return true;
	}

	private void requestNewPath() {
		ISPosition2D bestNeighbour = getCloseForeignTile();

		if (bestNeighbour != null) {
			super.getGrid().setMarked(bestNeighbour, true);
			super.goToTile(bestNeighbour);
		} else {
			centerPos = super.getPos();
			super.calculateDijkstraPath(centerPos, SEARCH_RADIUS, ESearchType.FOREIGN_GROUND);
		}
		going = true;
	}

	private ISPosition2D getCloseForeignTile() {
		byte myPlayer = super.getPlayer();
		ISPosition2D bestNeighbour = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point

		// TODO: look at more tiles (radius 3)
		for (EDirection sateliteDir : EDirection.values()) {
			ISPosition2D satelitePos = sateliteDir.getNextHexPoint(super.getPos());

			if (super.getGrid().isInBounds(satelitePos) && super.getGrid().getPlayerAt(satelitePos) != myPlayer
					&& !super.getGrid().isBlocked(this, satelitePos.getX(), satelitePos.getY()) && !super.getGrid().isMarked(satelitePos)) {
				double distance = Math.hypot(satelitePos.getX() - centerPos.getX(), satelitePos.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbour = satelitePos;
				}
			}

		}
		return bestNeighbour;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		if (stop) {
			this.centerPos = null;
			super.setAction(EAction.NO_ACTION, -1);
			stopWorkAndReleaseMarked();
		} else {
			this.centerPos = super.getPos();
			this.going = false;
		}
	}

	@Override
	protected void pathRequestFailed() {
		stopOrStartWorking(true);
		super.setAction(EAction.NO_ACTION, -1);
	}

	@Override
	protected void killedEvent() {
		unmarkTargetPos();
	}

	private void unmarkTargetPos() {
		ISPosition2D targetPos = super.getTargetPos();
		if (targetPos != null) {
			super.getGrid().setMarked(targetPos, false);
		} else {
			super.getGrid().setMarked(super.getPos(), false);
		}
	}
}
