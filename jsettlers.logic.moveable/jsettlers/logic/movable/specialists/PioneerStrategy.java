package jsettlers.logic.movable.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.hex.HexGrid;
import jsettlers.logic.map.hex.HexTile;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class PioneerStrategy extends PathableStrategy {
	private static final byte SEARCH_RADIUS = (byte) 15;

	private ISPosition2D centerPos;
	private boolean going = false;

	public PioneerStrategy(Movable movable) {
		super(movable);
	}

	@Override
	public boolean needsPlayersGround() {
		return false;
	}

	@Override
	protected void setCalculatedPath(Path path) {
		HexGrid.get().setMarked(super.getPos(), false);
		super.setCalculatedPath(path);
	}

	@Override
	protected void pathFinished() {
		if (centerPos == null) {
			centerPos = super.getPos();
		}

		if (HexGrid.get().getPlayer(super.getPos()) != super.getPlayer()) {
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
					HexGrid.get().setMarked(super.getPos(), false);
					HexGrid.get().setPlayerAt(super.getPos(), super.getPlayer());
					LandmarksCorrectingThread.addLandmarkedPosition(super.getPos());
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
	protected boolean isGotoJobable() {
		centerPos = null;
		going = true;
		return true;
	}

	private void requestNewPath() {
		ISPosition2D bestNeighbour = getCloseForeignTile();

		if (bestNeighbour != null) {
			HexGrid.get().setMarked(bestNeighbour, true);
			super.goToTile(bestNeighbour);
		} else {
			centerPos = super.getPos();
			super.calculateDijkstraPath(centerPos, SEARCH_RADIUS, ESearchType.FOREIGN_GROUND);
		}
		going = true;
	}

	private ISPosition2D getCloseForeignTile() {
		byte myPlayer = super.getPlayer();
		HexTile bestNeighbour = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point

		// TODO: look at more tiles (radius 3)
		for (EDirection sateliteDir : EDirection.values()) {
			ISPosition2D satelitePos = sateliteDir.getNextHexPoint(super.getPos());
			HexTile sateliteTile = HexGrid.get().getTile(satelitePos);
			if (sateliteTile != null && sateliteTile.getPlayer() != myPlayer && !sateliteTile.isBlocked() && !sateliteTile.isMarked()) {
				double distance = Math.hypot(sateliteTile.getX() - centerPos.getX(), sateliteTile.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbour = sateliteTile;
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
		} else {
			centerPos = super.getPos();
			going = true;
		}
	}

	@Override
	protected void pathRequestFailed() {
		stopOrStartWorking(true);
		super.setAction(EAction.NO_ACTION, -1);
	}

}
