package jsettlers.logic.buildings;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
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
public class OccupyingBuilding extends Building implements IBuilding.IOccupyed {
	private static final long serialVersionUID = 5267249978497095473L;

	private static final float RADIUS = 40;

	private final LinkedList<IBuildingOccupyer> list;

	protected OccupyingBuilding(EBuildingType type, byte player) {
		super(type, player);
		final OccupyerPlace[] occupyerPlaces = super.getBuildingType().getOccupyerPlaces();

		list = new LinkedList<IBuildingOccupyer>(); // for testing purposes
		if (occupyerPlaces.length > 0) {
			list.add(new IBuildingOccupyer() {
				@Override
				public OccupyerPlace getPlace() {
					return occupyerPlaces[0];
				}

				@Override
				public EMovableType getMovableType() {
					return EMovableType.SWORDSMAN_L1;
				}
			});
		}
	}

	@Override
	protected void constructionFinishedEvent() {
		MapShapeFilter occupying = getOccupyablePositions();
		super.getGrid().occupyArea(occupying, super.getPos(), super.getPlayer());
	}

	private MapShapeFilter getOccupyablePositions() {
		return new MapShapeFilter(new MapCircle(super.getPos(), RADIUS), super.getGrid().getWidth(), super.getGrid().getHeight());
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
	protected void killedEvent() {
		MapShapeFilter occupied = getOccupyablePositions();
		super.getGrid().freeOccupiedArea(occupied, super.getPos());
	}

	@Override
	public List<IBuildingOccupyer> getOccupyers() {
		return list;
	}
}
