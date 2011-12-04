package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;

/**
 * Temporary tower building.
 * 
 * <p>
 * prototype!
 * 
 * @author michael
 * 
 */
public class OccupyingBuilding extends Building {
	private static final long serialVersionUID = 5267249978497095473L;

	private static final float RADIUS = 40;

	protected OccupyingBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	protected void constructionFinishedEvent() {
		MapShapeFilter occupying = getOccupyablePositions();
		grid.occupyArea(occupying, super.getPos(), super.getPlayer());
	}

	private MapShapeFilter getOccupyablePositions() {
		return new MapShapeFilter(new MapCircle(super.getPos(), RADIUS), grid.getWidth(), grid.getHeight());
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected void subTimerEvent() {
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
	}

	@Override
	public boolean isOccupied() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void killedEvent() {
		MapShapeFilter occupied = getOccupyablePositions();
		grid.freeOccupiedArea(occupied, super.getPos());
	}
}
