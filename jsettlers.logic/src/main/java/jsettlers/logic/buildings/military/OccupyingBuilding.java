/*******************************************************************************
 * Copyright (c) 2015, 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.buildings.military;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.dijkstra.DijkstraAlgorithm.DijkstraContinuableRequest;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupier;
import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.map.ArrayListMap;
import jsettlers.common.utils.collections.map.ArrayListMap.Entry;
import jsettlers.graphics.messages.SimpleMessage;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IAttackableMovable;
import jsettlers.logic.objects.StandardMapObject;
import jsettlers.logic.player.Player;

/**
 * This is a tower building that can request soldiers and let them defend the building.
 *
 * @author Andreas Eberle
 */
public class OccupyingBuilding extends Building implements IBuilding.IOccupied, IPathCalculatable, IOccupyableBuilding, Serializable {
	private static final long serialVersionUID = 5267249978497095473L;

	private static final int TIMER_PERIOD = 500;

	private final LinkedList<OccupierPlace> emptyPlaces = new LinkedList<>();
	private final LinkedList<SoldierRequest> searchedSoldiers = new LinkedList<>();
	private final ArrayListMap<IBuildingOccupyableMovable, SoldierRequest> comingSoldiers = new ArrayListMap<>();
	private final LinkedList<TowerOccupier> sortedOccupiers = new LinkedList<>();
	private final LinkedList<TowerOccupier> toBeReleasedOccupiers = new LinkedList<>();

	private DijkstraContinuableRequest dijkstraRequest;

	private boolean occupiedArea;
	private float doorHealth = 50f;
	private boolean inFight = false;
	private AttackableTowerMapObject attackableTowerObject = null;

	public OccupyingBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);

		initSoldierRequests();
	}

	private void initSoldierRequests() {
		final OccupierPlace[] occupierPlaces = super.getBuildingType().getOccupierPlaces();
		if (occupierPlaces.length > 0) {
			emptyPlaces.addAll(Arrays.asList(occupierPlaces));
			requestSoldiers();
		}
	}

	@Override
	protected final int constructionFinishedEvent() {
		setAttackableTowerObject(true);
		return TIMER_PERIOD + MatchConstants.random().nextInt(200); // adding random prevents simultaneous scan after map creation
	}

	private void setAttackableTowerObject(boolean set) {
		if (set) {
			attackableTowerObject = new AttackableTowerMapObject();
			super.grid.getMapObjectsManager().addAttackableTowerObject(getDoor(), attackableTowerObject);
		} else {
			super.grid.getMapObjectsManager().removeMapObjectType(getDoor().x, getDoor().y, EMapObjectType.ATTACKABLE_TOWER);
			attackableTowerObject = null;
		}
	}

	void changePlayerTo(ShortPoint2D attackerPos) {
		assert sortedOccupiers.isEmpty() : "there cannot be any occupiers in the tower when changing the player.";

		Movable attacker = super.grid.getMovable(attackerPos);
		Player newPlayer = attacker.getPlayer();

		setAttackableTowerObject(false);
		super.showFlag(false);

		resetSoldierSearch();

		super.setPlayer(newPlayer);

		if (occupiedArea) { // free the area if it had been occupied.
			super.grid.changePlayerOfTower(super.pos, newPlayer, getGroundArea());
		} else {
			occupyAreaIfNeeded();
		}

		initSoldierRequests();
		searchedSoldiers.remove(ESearchType.SOLDIER_SWORDSMAN);
		IBuildingOccupyableMovable newOccupier = attacker.setOccupyableBuilding(this);
		comingSoldiers.put(newOccupier, searchedSoldiers.pop());

		doorHealth = 0.1f;
		inFight = false;

		super.showFlag(true);
		setAttackableTowerObject(true);
	}

	private FreeMapArea getGroundArea() {
		return new FreeMapArea(super.pos, getBuildingType().getProtectedTiles());
	}

	private void resetSoldierSearch() {
		dijkstraRequest = null;
		searchedSoldiers.clear();
		emptyPlaces.clear();
		comingSoldiers.clear();
	}

	@Override
	protected final void appearedEvent() {
		occupyAreaIfNeeded();
		searchedSoldiers.remove(ESearchType.SOLDIER_SWORDSMAN);
	}

	@Override
	protected final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected final int subTimerEvent() {
		increaseDoorHealth();
		releaseNextSoldierIfNeeded();
		searchSoldiersIfNeeded();
		return TIMER_PERIOD;
	}

	private void increaseDoorHealth() {
		if (doorHealth < 1 && !inFight) {
			doorHealth = Math.min(1, doorHealth + Constants.TOWER_DOOR_REGENERATION);
		}
	}

	private void releaseNextSoldierIfNeeded() {
		if (!toBeReleasedOccupiers.isEmpty()) {
			Movable movableAtDoor = grid.getMovable(super.getDoor());
			if (movableAtDoor == null) {
				TowerOccupier releasedOccupier = toBeReleasedOccupiers.pop();
				sortedOccupiers.remove(releasedOccupier);
				emptyPlaces.add(releasedOccupier.place);
				IBuildingOccupyableMovable soldier = releasedOccupier.soldier;
				soldier.leaveOccupyableBuilding(super.getDoor());
			} else {
				movableAtDoor.leavePosition();
			}
		}
	}

	private void searchSoldiersIfNeeded() {
		if (!searchedSoldiers.isEmpty()) {
			if (dijkstraRequest == null) {
				dijkstraRequest = new DijkstraContinuableRequest(this, super.pos.x, super.pos.y, (short) 1, Constants.TOWER_SEARCH_RADIUS);
			}

			Set<ESearchType> searchTypes = EnumSet.noneOf(ESearchType.class);
			for (SoldierRequest soldierRequest : searchedSoldiers) {
				searchTypes.add(soldierRequest.getSearchType());
			}
			dijkstraRequest.setSearchTypes(searchTypes);

			Path path = super.grid.getDijkstra().find(dijkstraRequest);
			if (path != null) {
				Movable soldier = super.grid.getMovable(path.getTargetPos());
				if (soldier != null) {
					IBuildingOccupyableMovable occupier = soldier.setOccupyableBuilding(this);
					if (occupier != null) {
						SoldierRequest soldierRequest = removeMatchingSoldierRequest(occupier.getMovableType().getSoldierType());
						comingSoldiers.put(occupier, soldierRequest);
						dijkstraRequest.reset();
					} // else soldier wasn't able to take the job to go to this building
				} // else { soldier wasn't at the position
			}
		}
	}

	private SoldierRequest removeMatchingSoldierRequest(ESoldierType soldierType) {
		for (Iterator<SoldierRequest> iterator = searchedSoldiers.iterator(); iterator.hasNext();) {
			SoldierRequest soldierRequest = iterator.next();
			if (soldierRequest.isOfTypeOrClass(soldierType)) {
				iterator.remove();
				return soldierRequest;
			}
		}
		return null;
	}

	@Override
	protected final void killedEvent() {
		setSelected(false);

		if (occupiedArea) {
			freeArea();

			int idx = 0;
			FreeMapArea buildingArea = super.getBuildingArea();
			for (TowerOccupier curr : sortedOccupiers) {
				addInformableMapObject(curr, false);// if curr is a bowman, this removes the informable map object.

				curr.getSoldier().leaveOccupyableBuilding(buildingArea.get(idx));
				idx++;
			}

			sortedOccupiers.clear();
		}

		if (attackableTowerObject != null && attackableTowerObject.currDefender != null) {
			attackableTowerObject.currDefender.soldier.leaveOccupyableBuilding(attackableTowerObject.getPos());
		}

		setAttackableTowerObject(false);
	}

	private void freeArea() {
		super.grid.freeAreaOccupiedByTower(super.pos);
	}

	@Override
	public final List<? extends IBuildingOccupier> getOccupiers() {
		return sortedOccupiers;
	}

	@Override
	public final boolean needsPlayersGround() { // soldiers don't need players ground.
		return false;
	}

	@Override
	public final OccupierPlace addSoldier(IBuildingOccupyableMovable soldier) {
		SoldierRequest soldierRequest = comingSoldiers.remove(soldier);
		OccupierPlace place = soldierRequest.place;

		TowerOccupier towerOccupier = new TowerOccupier(place, soldier);
		addOccupier(towerOccupier);

		occupyAreaIfNeeded();

		soldier.setSelected(super.isSelected());

		addInformableMapObject(towerOccupier, true);

		return place;
	}

	private void addOccupier(TowerOccupier towerOccupier) {
		sortedOccupiers.add(towerOccupier);
		Collections.sort(sortedOccupiers, new Comparator<TowerOccupier>() {
			@Override
			public int compare(TowerOccupier o1, TowerOccupier o2) {
				return o1.place.getSoldierClass().ordinal - o2.place.getSoldierClass().ordinal;
			}
		});
	}

	@Override
	public void removeSoldier(IBuildingOccupyableMovable soldier) {
		TowerOccupier occupier = null;
		for (TowerOccupier currOccupier : sortedOccupiers) {
			if (currOccupier.soldier == soldier) {
				occupier = currOccupier;
				break;
			}
		}

		// if the soldier is not in the tower, just return
		if (occupier == null) {
			return;
		}

		// remove the soldier and dijkstraRequest a new one
		sortedOccupiers.remove(occupier);
		emptyPlaces.add(occupier.place);
		requestSoldier(occupier.place.getSoldierClass());
	}

	protected TowerOccupier removeSoldier() {
		TowerOccupier removedSoldier = sortedOccupiers.removeFirst();

		addInformableMapObject(removedSoldier, false);

		return removedSoldier;
	}

	/**
	 * Adds or removes the informable map object for the given soldier.
	 *
	 * @param soldier
	 * @param add
	 *            if true, the object is added<br>
	 *            if false, the object is removed.
	 */
	private void addInformableMapObject(TowerOccupier soldier, boolean add) {
		if (soldier.place.getSoldierClass() == ESoldierClass.BOWMAN) {
			ShortPoint2D position = getTowerBowmanSearchPosition(soldier.place);

			if (add) {
				super.grid.getMapObjectsManager().addInformableMapObjectAt(position, soldier.getSoldier().getMovable());
			} else {
				super.grid.getMapObjectsManager().removeMapObjectType(position.x, position.y, EMapObjectType.INFORMABLE_MAP_OBJECT);
			}
		}
	}

	@Override
	public ShortPoint2D getTowerBowmanSearchPosition(OccupierPlace place) {
		ShortPoint2D pos = place.getPosition().calculatePoint(super.pos);
		// FIXME @Andreas Eberle introduce new field in the buildings xml file
		ShortPoint2D position = new ShortPoint2D(pos.x + 3, pos.y + 6);
		return position;
	}

	private final void occupyAreaIfNeeded() {
		if (!occupiedArea) {
			MapCircle occupying = new MapCircle(super.pos, CommonConstants.TOWER_RADIUS);
			super.grid.occupyAreaByTower(super.getPlayer(), occupying, getGroundArea());
			occupiedArea = true;
		}
	}

	@Override
	public final void requestFailed(IBuildingOccupyableMovable soldier) {
		SoldierRequest soldierRequest = comingSoldiers.remove(soldier);
		searchedSoldiers.add(soldierRequest);
	}

	@Override
	public final ShortPoint2D getPosition(IBuildingOccupyableMovable soldier) {
		for (TowerOccupier curr : sortedOccupiers) {
			if (curr.getSoldier() == soldier) {
				return curr.place.getPosition().calculatePoint(super.pos);
			}
		}
		return null;
	}

	@Override
	public final void setSelected(boolean selected) {
		super.setSelected(selected);
		for (TowerOccupier curr : sortedOccupiers) {
			curr.getSoldier().setSelected(selected);
		}

		if (attackableTowerObject != null && attackableTowerObject.currDefender != null) {
			attackableTowerObject.currDefender.getSoldier().setSelected(selected);
		}
	}

	@Override
	public final boolean isOccupied() {
		return !sortedOccupiers.isEmpty() || inFight;
	}

	@Override
	public void towerDefended(IBuildingOccupyableMovable soldier) {
		inFight = false;
		if (attackableTowerObject.currDefender == null) {
			System.err.println("ERROR: WHAT? No defender in a defended tower!");
		} else {
			addOccupier(new TowerOccupier(attackableTowerObject.currDefender.place, soldier));
			attackableTowerObject.currDefender = null;
		}
		doorHealth = 0.1f;
	}

	@Override
	public int getSearchedSoldiers(ESoldierClass soldierClass) {
		int numberOfSearchedSoldiers = 0;
		for (SoldierRequest searchedSoldier : searchedSoldiers) {
			if (searchedSoldier.isOfTypeOrClass(soldierClass)) {
				numberOfSearchedSoldiers++;
			}
		}
		return numberOfSearchedSoldiers;
	}

	@Override
	public int getComingSoldiers(ESoldierClass soldierClass) {
		int numberOfComingSoldiers = 0;
		for (Entry<IBuildingOccupyableMovable, SoldierRequest> comingSoldier : comingSoldiers.entrySet()) {
			if (comingSoldier.getValue().isOfTypeOrClass(soldierClass)) {
				numberOfComingSoldiers++;
			}
		}
		return numberOfComingSoldiers;
	}

	@Override
	public void requestSoldier(Movable soldier) {
		if (!soldier.getMovableType().isSoldier()) {
			return;
		}

		ESoldierClass soldierClass = soldier.getMovableType().getSoldierClass();
		OccupierPlace emptyPlace = getEmptyPlaceForSoldierClass(soldierClass);

		if (emptyPlace == null) {
			return;
		}

		IBuildingOccupyableMovable occupier = soldier.setOccupyableBuilding(this);
		comingSoldiers.put(occupier, new SoldierRequest(soldierClass, emptyPlace));
	}

	public void requestSoldiers() {
		for (OccupierPlace emptyPlace : emptyPlaces) {
			searchedSoldiers.add(new SoldierRequest(emptyPlace.getSoldierClass(), emptyPlace));
		}
		emptyPlaces.clear();
	}

	public void requestSoldier(ESoldierType soldierType) {
		OccupierPlace emptyPlace = getEmptyPlaceForSoldierClass(soldierType.getSoldierClass());
		if (emptyPlace != null) {
			emptyPlaces.remove(emptyPlace);
			searchedSoldiers.add(new SoldierRequest(soldierType, emptyPlace));
		}
	}

	private void requestSoldier(ESoldierClass soldierClass) {
		OccupierPlace emptyPlace = getEmptyPlaceForSoldierClass(soldierClass);
		if (emptyPlace != null) {
			emptyPlaces.remove(emptyPlace);
			searchedSoldiers.add(new SoldierRequest(soldierClass, emptyPlace));
		}
	}

	public void releaseSoldiers() {
		toBeReleasedOccupiers.clear();
		toBeReleasedOccupiers.addAll(sortedOccupiers); // release all but first occupier
		toBeReleasedOccupiers.removeFirst();

		for (SoldierRequest searchedSoldier : searchedSoldiers) {
			emptyPlaces.add(searchedSoldier.place);
		}
		searchedSoldiers.clear();

		for (Entry<IBuildingOccupyableMovable, SoldierRequest> commingSoldierEntry : comingSoldiers.entrySet()) {
			commingSoldierEntry.getKey().leaveOccupyableBuilding(super.getDoor());
			emptyPlaces.add(commingSoldierEntry.getValue().place);
		}
		comingSoldiers.clear();
	}

	public void releaseSoldier(ESoldierType soldierType) {
		for (Iterator<SoldierRequest> iterator = searchedSoldiers.iterator(); iterator.hasNext();) {
			SoldierRequest soldierRequest = iterator.next();
			if (soldierRequest.isOfTypeOrClass(soldierType)) {
				iterator.remove();
				emptyPlaces.add(soldierRequest.place);
			}
		}

		for (Entry<IBuildingOccupyableMovable, SoldierRequest> commingSoldierEntry : comingSoldiers.entrySet()) {
			if (commingSoldierEntry.getValue().isOfTypeOrClass(soldierType)) {
				commingSoldierEntry.getKey().leaveOccupyableBuilding(super.getDoor());
				emptyPlaces.add(commingSoldierEntry.getValue().place);
				comingSoldiers.remove(commingSoldierEntry.getKey());
				return;
			}
		}

		if (sortedOccupiers.size() - toBeReleasedOccupiers.size() > 1) { // always keep one soldier inside
			for (TowerOccupier occupier : sortedOccupiers) {
				if (occupier.soldier.getMovableType().getSoldierType() == soldierType && !toBeReleasedOccupiers.contains(occupier)) {
					toBeReleasedOccupiers.add(occupier);
					return;
				}
			}
		}
	}

	private OccupierPlace getEmptyPlaceForSoldierClass(ESoldierClass soldierClass) {
		for (OccupierPlace place : emptyPlaces) {
			if (place.getSoldierClass() == soldierClass) {
				return place;
			}
		}

		return null;
	}

	/**
	 * This map object lies at the door of a tower and is used to signal soldiers that there is something to attack.
	 *
	 * @author Andreas Eberle
	 */
	public class AttackableTowerMapObject extends StandardMapObject implements IAttackable, IAttackableTowerMapObject {
		private static final long serialVersionUID = -5137593316096740750L;
		private TowerOccupier currDefender;

		public AttackableTowerMapObject() {
			super(EMapObjectType.ATTACKABLE_TOWER, false, OccupyingBuilding.this.getPlayerId());
		}

		@Override
		public ShortPoint2D getPos() {
			return OccupyingBuilding.this.getDoor();
		}

		@Override
		public void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer) {
			Movable attacker = grid.getMovable(attackerPos);
			if (attacker != null && attacker.getPlayer() == getPlayer()) {
				return; // this can happen directly after the tower changed its player
			}

			if (doorHealth > 0) {
				doorHealth -= strength / Constants.DOOR_HIT_RESISTENCY_FACTOR;

				if (doorHealth <= 0) {
					doorHealth = 0;
					inFight = true;

					OccupyingBuilding.this.grid.getMapObjectsManager()
							.addSelfDeletingMapObject(getPos(), EMapObjectType.GHOST, Constants.GHOST_PLAY_DURATION, getPlayer());

					pullNewDefender(attackerPos);
				}
			} else if (currDefender != null) {
				IAttackableMovable movable = currDefender.getSoldier().getMovable();
				movable.receiveHit(strength, attackerPos, attackingPlayer);

				if (movable.getHealth() <= 0) {
					emptyPlaces.add(currDefender.place); // dijkstraRequest a new soldier.
					requestSoldier(currDefender.place.getSoldierClass());

					pullNewDefender(attackerPos);
				}
			}

			OccupyingBuilding.this.getPlayer().showMessage(SimpleMessage.attacked(attackingPlayer, attackerPos));
		}

		private void pullNewDefender(ShortPoint2D attackerPos) {
			if (sortedOccupiers.isEmpty()) {
				currDefender = null;
				changePlayerTo(attackerPos);
			} else {
				currDefender = removeSoldier();
				currDefender.getSoldier().setDefendingAt(getPos());
			}
		}

		@Override
		public float getHealth() {
			if (doorHealth > 0) {
				return doorHealth;
			} else {
				return currDefender == null ? 0 : currDefender.getMovable().getHealth();
			}
		}

		@Override
		public boolean isAttackable() {
			return true;
		}

		@Override
		public IMovable getMovable() {
			return currDefender == null ? null : currDefender.getSoldier().getMovable();
		}

		@Override
		public EMovableType getMovableType() {
			assert false : "This should never have been called";
			return EMovableType.SWORDSMAN_L1;
		}

		@Override
		public void informAboutAttackable(IAttackable attackable) {
		}

		@Override
		public boolean isTower() {
			return true;
		}
	}

	private final static class TowerOccupier implements IBuildingOccupier, Serializable {
		private static final long serialVersionUID = -1491427078923346232L;

		final OccupierPlace place;
		final IBuildingOccupyableMovable soldier;

		TowerOccupier(OccupierPlace place, IBuildingOccupyableMovable soldier) {
			this.place = place;
			this.soldier = soldier;
		}

		@Override
		public OccupierPlace getPlace() {
			return place;
		}

		@Override
		public IMovable getMovable() {
			return soldier.getMovable();
		}

		public IBuildingOccupyableMovable getSoldier() {
			return soldier;
		}
	}

	private static class SoldierRequest implements Serializable {
		final ESoldierClass soldierClass;
		final ESoldierType soldierType;
		final OccupierPlace place;

		SoldierRequest(ESoldierType soldierType, OccupierPlace place) {
			this.soldierType = soldierType;
			soldierClass = null;
			this.place = place;
		}

		SoldierRequest(ESoldierClass soldierClass, OccupierPlace place) {
			this.soldierClass = soldierClass;
			soldierType = null;
			this.place = place;
		}

		public ESearchType getSearchType() {
			if (soldierClass != null) {
				switch (soldierClass) {
				case INFANTRY:
					return ESearchType.SOLDIER_INFANTRY;
				case BOWMAN:
					return ESearchType.SOLDIER_BOWMAN;
				}
			} else {
				switch (soldierType) {
				case SWORDSMAN:
					return ESearchType.SOLDIER_SWORDSMAN;
				case PIKEMAN:
					return ESearchType.SOLDIER_PIKEMAN;
				case BOWMAN:
					return ESearchType.SOLDIER_BOWMAN;
				}
			}
			throw new RuntimeException("Unknown soldier or search type");
		}

		public boolean isOfTypeOrClass(ESoldierType soldierType) {
			return this.soldierType == soldierType || soldierClass == soldierType.getSoldierClass();
		}

		public boolean isOfTypeOrClass(ESoldierClass soldierClass) {
			return this.soldierClass == soldierClass || (this.soldierType != null && this.soldierType.getSoldierClass() == soldierClass);
		}
	}
}
