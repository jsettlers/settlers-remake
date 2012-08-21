package jsettlers.logic.buildings.military;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm.DijkstraContinuableRequest;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.NewMovable;
import random.RandomSingleton;

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

	private static LinkedList<OccupyingBuilding> allOccupyingBuildings = new LinkedList<OccupyingBuilding>();

	private final LinkedList<TowerOccupyer> occupiers;
	private final LinkedList<ESearchType> searchedSoldiers = new LinkedList<ESearchType>();
	private final LinkedList<OccupyerPlace> emptyPlaces = new LinkedList<OccupyerPlace>();

	private DijkstraContinuableRequest request;
	private byte delayCtr;

	private boolean occupiedArea;

	public OccupyingBuilding(EBuildingType type, byte player) {
		super(type, player);

		final OccupyerPlace[] occupyerPlaces = super.getBuildingType().getOccupyerPlaces();
		occupiers = new LinkedList<TowerOccupyer>(); // for testing purposes
		if (occupyerPlaces.length > 0) {

			for (OccupyerPlace currPlace : occupyerPlaces) {
				emptyPlaces.add(currPlace);
				searchedSoldiers.add(currPlace.getType() == ESoldierType.INFANTRY ? ESearchType.SOLDIER_SWORDSMAN : ESearchType.SOLDIER_BOWMAN);
			}
		}

		allOccupyingBuildings.add(this);

		delayCtr = (byte) RandomSingleton.getInt(0, 3);
	}

	@Override
	protected final void constructionFinishedEvent() {
		for (RelativePoint curr : super.getBuildingType().getAttackers()) {
			super.getGrid().getMapObjectsManager()
					.addSimpleMapObject(curr.calculatePoint(super.getPos()), EMapObjectType.ATTACKABLE_TOWER, false, super.getPlayer());
		}
	}

	@Override
	protected final void appearedEvent() {
		occupyArea();
		searchedSoldiers.remove(ESearchType.SOLDIER_SWORDSMAN);
	}

	public final MapCircle getOccupyablePositions() {
		return new MapCircle(super.getPos(), CommonConstants.TOWERRADIUS);
	}

	@Override
	protected final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public final void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected final void subTimerEvent() {
		delayCtr++;
		if (delayCtr > 5) {
			delayCtr = 0;

			if (!searchedSoldiers.isEmpty()) {
				if (request == null) {
					request = new DijkstraContinuableRequest(this, super.getPos().getX(), super.getPos().getY(), (short) 1,
							Constants.TOWER_SEARCH_RADIUS);
				}
				request.setSearchType(searchedSoldiers.peek());

				Path path = super.getGrid().getDijkstra().find(request);
				if (path != null) {
					System.out.println("soldier found");

					NewMovable soldier = super.getGrid().getMovable(path.getTargetPos());
					if (soldier != null && soldier.setOccupyableBuilding(this)) {
						searchedSoldiers.pop();
					}// soldier wasn't at the position
				}
			}
		}
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	protected final void killedEvent() {
		setSelected(false);

		if (occupiedArea) {
			MapCircle occupied = getOccupyablePositions();
			super.getGrid().freeOccupiedArea(occupied, super.getPos());

			int idx = 0;
			for (TowerOccupyer curr : occupiers) {
				curr.soldier.leaveOccupyableBuilding(super.getBuildingArea().get(idx));
				idx++;
			}
			occupiers.clear();
		}

		allOccupyingBuildings.remove(this);
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

		soldier.setSelected(super.isSelected());
	}

	private final void occupyArea() {
		if (!occupiedArea) {
			MapCircle occupying = getOccupyablePositions();
			super.getGrid().occupyArea(occupying, super.getPos(), super.getPlayer());
			occupiedArea = true;
		}
	}

	@Override
	public final void requestFailed(EMovableType movableType) {
		ESearchType searchType = getSearchType(movableType);
		if (searchType != null)
			searchedSoldiers.add(searchType);
	}

	@Override
	public final ShortPoint2D getPosition(IBuildingOccupyableMovable soldier) {
		for (TowerOccupyer curr : occupiers) {
			if (curr.soldier == soldier) {
				return curr.place.getPosition().calculatePoint(super.getPos());
			}
		}
		return null;
	}

	@Override
	public final void setSelected(boolean selected) {
		super.setSelected(selected);
		for (TowerOccupyer curr : occupiers) {
			curr.soldier.setSelected(selected);
		}
	}

	private final ESearchType getSearchType(EMovableType movableType) {
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

	@Override
	public final boolean isOccupied() {
		return occupiers.isEmpty();
	}

	private final static class TowerOccupyer implements IBuildingOccupyer, Serializable {
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

	public final static LinkedList<OccupyingBuilding> getAllOccupyingBuildings() {
		return allOccupyingBuildings;
	}

}
