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
public abstract class OccupyingBuilding extends Building {

	private static final float RADIUS = 40;

	protected OccupyingBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	protected void constructionFinishedEvent() {
		MapShapeFilter occupying = new MapShapeFilter(new MapCircle(super.getPos(), RADIUS), grid.getWidth(), grid.getHeight());
		for (ISPosition2D currPos : occupying) {
			grid.setPlayerAt(currPos, getPlayer());
		}
	}

	@Override
	public int getActionImgIdx() {
		return 0;
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
}
