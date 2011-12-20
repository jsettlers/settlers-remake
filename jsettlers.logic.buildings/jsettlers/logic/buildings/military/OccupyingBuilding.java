package jsettlers.logic.buildings.military;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm.DijkstraContinuableRequest;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.movable.IHexMovable;

/**
 * Tower building.
 * 
 * <p>
 * 
 * @author Andreas Eberle
 * 
 */
public class OccupyingBuilding extends Building implements IBuilding.IOccupyed, IPathCalculateable, IOccupyableBuilding {
	private static final long serialVersionUID = 5267249978497095473L;

	private static final float RADIUS = 40;

	private final LinkedList<TowerOccupyer> occupiers;
	private final LinkedList<ESearchType> searchedSoldiers = new LinkedList<ESearchType>();
	private final LinkedList<OccupyerPlace> emptyPlaces = new LinkedList<OccupyerPlace>();

	private DijkstraContinuableRequest request;
	private byte delayCtr = 0;

	private boolean occupiedArea;

	public OccupyingBuilding(EBuildingType type, byte player) {
		super(type, player);

		final OccupyerPlace[] occupyerPlaces = super.getBuildingType().getOccupyerPlaces();
		occupiers = new LinkedList<TowerOccupyer>(); // for testing purposes
		if (occupyerPlaces.length > 0) {
			searchedSoldiers.add(ESearchType.SOLDIER_SWORDSMAN);
			searchedSoldiers.add(ESearchType.SOLDIER_BOWMAN);
			searchedSoldiers.add(ESearchType.SOLDIER_BOWMAN);

			for (OccupyerPlace currPlace : occupyerPlaces) {
				emptyPlaces.add(currPlace);
			}
		}
	}

	@Override
	protected final void constructionFinishedEvent() {
	}

	@Override
	protected void appearedEvent() {
		occupyArea();
		searchedSoldiers.remove(ESearchType.SOLDIER_SWORDSMAN);
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
		delayCtr++;
		if (delayCtr > 5) {
			delayCtr = 0;

			if (!searchedSoldiers.isEmpty()) {
				if (request == null) {
					request = new DijkstraContinuableRequest(this, super.getPos().getX(), super.getPos().getY(), (short) 1,
							Constants.TOWER_SOLDIER_SEARCH_AREA);
				}
				request.setSearchType(searchedSoldiers.peek());

				Path path = super.getGrid().getDijkstra().find(request);
				if (path != null) {
					System.out.println("soldier found");

					IHexMovable soldier = super.getGrid().getMovable(path.getTargetPos());
					if (soldier != null && soldier.setOccupyableBuilding(this)) {
						searchedSoldiers.pop();
					}// soldier wasn't at the position
				}
			}
		}
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
	}

	@Override
	protected void killedEvent() {
		if (occupiedArea) {
			MapShapeFilter occupied = getOccupyablePositions();
			super.getGrid().freeOccupiedArea(occupied, super.getPos());

			int idx = 0;
			for (TowerOccupyer curr : occupiers) {
				curr.soldier.leaveOccupyableBuilding(super.getBuildingArea().get(idx));
				idx++;
			}
			occupiers.clear();
		}
	}

	@Override
	public final List<? extends IBuildingOccupyer> getOccupyers() {
		return occupiers;
	}

	@Override
	public final boolean needsPlayersGround() { // soldiers don't need players ground.
		return false;
	}

	@Override
	public final void setSoldier(IBuildingOccupyableMovable soldier) {
		for (OccupyerPlace curr : emptyPlaces) {
			if (curr.getType() == soldier.getSoldierType()) {
				emptyPlaces.remove(curr);
				occupiers.add(new TowerOccupyer(curr, soldier));
				break;
			}
		}

		occupyArea();
	}

	private final void occupyArea() {
		if (!occupiedArea) {
			MapShapeFilter occupying = getOccupyablePositions();
			super.getGrid().occupyArea(occupying, super.getPos(), super.getPlayer());
			occupiedArea = true;
		}
	}

	@Override
	public final ISPosition2D getDoor() {
		return super.getDoor();
	}

	@Override
	public final void requestFailed(EMovableType movableType) {
		ESearchType searchType = getSearchType(movableType);
		if (searchType != null)
			searchedSoldiers.add(searchType);
	}

	@Override
	public final ISPosition2D getPosition(IBuildingOccupyableMovable soldier) {
		for (TowerOccupyer curr : occupiers) {
			if (curr.soldier == soldier) {
				return curr.place.getPosition().calculatePoint(super.getPos());
			}
		}
		return null;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		for (TowerOccupyer curr : occupiers) {
			curr.soldier.setSelected(selected);
		}
	}

	private ESearchType getSearchType(EMovableType movableType) {
		ESearchType searchType;

		switch (movableType) {
		case BOWMAN_L1:
		case BOWMAN_L2:
		case BOWMAN_L3:
			searchType = ESearchType.SOLDIER_BOWMAN;
			break;
		case SWORDSMAN_L1:
		case SWORDSMAN_L2:
		case SWORDSMAN_L3:
			searchType = ESearchType.SOLDIER_SWORDSMAN;
			break;
		case PIKEMAN_L1:
		case PIKEMAN_L2:
		case PIKEMAN_L3:
			searchType = ESearchType.SOLDIER_PIKEMAN;
			break;
		default:
			return null;
		}
		return searchType;
	}

	private class TowerOccupyer implements IBuildingOccupyer, Serializable {
		private static final long serialVersionUID = -1491427078923346232L;

		private final OccupyerPlace place;
		private final IBuildingOccupyableMovable soldier;

		TowerOccupyer(OccupyerPlace place, IBuildingOccupyableMovable soldier) {
			this.place = place;
			this.soldier = soldier;
		}

		@Override
		public OccupyerPlace getPlace() {
			return place;
		}

		@Override
		public IMovable getMovable() {
			return soldier.getMovable();
		}

	}

}
