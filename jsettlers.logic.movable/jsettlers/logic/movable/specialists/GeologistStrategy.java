package jsettlers.logic.movable.specialists;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class GeologistStrategy extends PathableStrategy {
	private static final long serialVersionUID = -5571213409776666251L;

	private static final byte SEARCH_RADIUS = (byte) 15;
	private static final float ACTION_DURATION = 1.4f;

	private ISPosition2D centerPos;
	private boolean going = false;

	public GeologistStrategy(IMovableGrid grid, Movable movable) {
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

		if (super.getGrid().canAddRessourceSign(super.getPos())) {
			super.setAction(EAction.ACTION1, ACTION_DURATION);
			going = false;
		} else {
			unmarkTargetPos();
			requestNewPath();
		}
	}

	@Override
	protected final boolean actionFinished() {
		if (!super.actionFinished()) {
			if (centerPos != null) {
				if (!going) {
					executeAction();
					unmarkTargetPos();
					requestNewPath();
				} else {
					super.setAction(EAction.ACTION1, ACTION_DURATION);
					going = false;
				}
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}

		return true;
	}

	private final void executeAction() {
		if (super.getGrid().canAddRessourceSign(super.getPos())) {
			IMovableGrid grid = super.getGrid();
			short x = super.getPos().getX();
			short y = super.getPos().getY();
			EResourceType resourceType = grid.getResourceTypeAt(x, y);
			byte amount = (byte) Math.max(grid.getResourceAmountAt(x, y), 0);

			grid.getMapObjectsManager().addRessourceSign(super.getPos(), resourceType, amount / 127f);
		}
	}

	private void stopWorkAndReleaseMarked() {
		unmarkTargetPos();
		centerPos = null;
		going = true;
	}

	private final void unmarkTargetPos() {
		ISPosition2D targetPos = super.getTargetPos();
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

	private void requestNewPath() {
		ISPosition2D bestNeighbour = getCloseForeignTile();

		if (bestNeighbour != null) {
			super.getGrid().setMarked(bestNeighbour, true);
			super.goToTile(bestNeighbour);
		} else {
			centerPos = super.getPos();
			super.calculateDijkstraPath(centerPos, SEARCH_RADIUS, ESearchType.MOUNTAIN);
		}
		going = true;
	}

	private ISPosition2D getCloseForeignTile() {
		ISPosition2D bestNeighbour = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point

		// TODO: look at more tiles (radius 3)
		for (ISPosition2D satelitePos : new HexBorderArea(super.getPos(), (short) 1)) {
			if (super.getGrid().isAllowedForMovable(satelitePos.getX(), satelitePos.getY(), this) && canWorkOn(satelitePos)) {
				double distance = Math.hypot(satelitePos.getX() - centerPos.getX(), satelitePos.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbour = satelitePos;
				}
			}

		}
		return bestNeighbour;
	}

	private final boolean canWorkOn(ISPosition2D pos) {
		return super.getGrid().fitsSearchType(pos, ESearchType.MOUNTAIN, this);
	}

	@Override
	protected final void stopOrStartWorking(boolean stop) {
		super.stopOrStartWorking(stop);
		if (stop) {
			this.centerPos = null;
			stopWorkAndReleaseMarked();
		} else {
			this.centerPos = super.getPos();
			this.going = false;
		}
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
	protected final EMovableType getMovableType() {
		return EMovableType.GEOLOGIST;
	}

	@Override
	protected final void killedEvent() {
		unmarkTargetPos();
	}
}
