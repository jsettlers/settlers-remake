package jsettlers.logic.movable.specialists;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;

public final class GeologistStrategy extends SpecialistStrategy {
	private static final long serialVersionUID = -5571213409776666251L;

	private static final float ACTION_DURATION = 1.4f;

	public GeologistStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	protected final void executeAction() {
		IMovableGrid grid = super.getGrid();
		short x = super.getPos().getX();
		short y = super.getPos().getY();
		EResourceType resourceType = grid.getResourceTypeAt(x, y);
		byte amount = (byte) Math.max(grid.getResourceAmountAt(x, y), 0);

		grid.getMapObjectsManager().addRessourceSign(super.getPos(), resourceType, amount / 127f);
	}

	@Override
	protected final ISPosition2D getCloseForeignTile() {
		ISPosition2D bestNeighbour = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point
		ISPosition2D centerPos = super.getCenterPos();

		// TODO: look at more tiles (radius 3)
		for (ISPosition2D satelitePos : new HexBorderArea(super.getPos(), (short) 2)) {
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

	/**
	 * needs to be overriden here, because we have to go at least two steps.
	 */
	@Override
	protected final void initGoingToPosition(ISPosition2D bestNeighbour) {
		super.calculatePathTo(bestNeighbour);
	}

	protected final boolean canWorkOn(ISPosition2D satelitePos) {
		return super.getGrid().fitsSearchType(satelitePos, getSearchType(), this);
	}

	@Override
	protected final EMovableType getMovableType() {
		return EMovableType.GEOLOGIST;
	}

	@Override
	protected final boolean canWorkOnCurrPos() {
		return super.getGrid().canAddRessourceSign(super.getPos());
	}

	@Override
	protected final float getActionDuration() {
		return ACTION_DURATION;
	}

	@Override
	protected final ESearchType getSearchType() {
		return ESearchType.MOUNTAIN;
	}

}
