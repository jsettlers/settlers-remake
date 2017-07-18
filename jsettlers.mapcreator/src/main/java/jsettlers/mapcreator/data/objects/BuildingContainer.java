/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.mapcreator.data.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.IBuildingOccupier;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.mapcreator.data.LandscapeConstraint;

public class BuildingContainer implements ObjectContainer, IBuilding, LandscapeConstraint, IBuilding.IMill, IBuilding.IOccupied {

	private final BuildingMapDataObject buildingObject;
	private final ShortPoint2D position;
	private final IPlayer player;

	public BuildingContainer(BuildingMapDataObject buildingObject, ShortPoint2D position) {
		this.buildingObject = buildingObject;
		this.position = position;
		this.player = new IPlayer.DummyPlayer(buildingObject.getPlayerId());
	}

	@Override
	public BuildingMapDataObject getMapObject() {
		return buildingObject;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return buildingObject.getType().getProtectedTiles();
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.BUILDING;
	}

	@Override
	public float getStateProgress() {
		return 1;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	@Override
	public EBuildingType getBuildingType() {
		return buildingObject.getType();
	}

	@Override
	public Set<ELandscapeType> getAllowedLandscapes() {
		return buildingObject.getType().getGroundTypes();
	}

	@Override
	public boolean needsFlattenedGround() {
		return buildingObject.getType().needsFlattenedGround();
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return true;
	}

	@Override
	public List<? extends IBuildingOccupier> getOccupiers() {
		return new LinkedList<>();
	}

	@Override
	public boolean isRotating() {
		return true;
	}

	@Override
	public ESelectionType getSelectionType() {
		return ESelectionType.BUILDING;
	}

	@Override
	public List<IBuildingMaterial> getMaterials() {
		return Collections.emptyList();
	}

	@Override
	public int getSearchedSoldiers(ESoldierClass type) {
		return 0;
	}

	@Override
	public int getComingSoldiers(ESoldierClass type) {
		return 0;
	}

	@Override
	public EPriority getPriority() {
		return EPriority.DEFAULT;
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		return new EPriority[0];
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}

	@Override
	public boolean cannotWork() {
		return false;
	}
}
