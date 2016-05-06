/*******************************************************************************
 * Copyright (c) 2015
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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.mapcreator.data.LandscapeConstraint;

public class BuildingContainer implements ObjectContainer, IBuilding, LandscapeConstraint, IBuilding.IMill, IBuilding.IOccupyed {

	private final BuildingObject object;
	private final ShortPoint2D pos;

	public BuildingContainer(BuildingObject object, ShortPoint2D pos) {
		this.object = object;
		this.pos = pos;
	}

	@Override
	public BuildingObject getMapObject() {
		return object;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return object.getType().getProtectedTiles();
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
	public byte getPlayerId() {
		return object.getPlayerId();
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
		return pos;
	}

	@Override
	public EBuildingType getBuildingType() {
		return object.getType();
	}

	@Override
	public ELandscapeType[] getAllowedLandscapes() {
		return object.getType().getGroundtypes();
	}

	@Override
	public boolean needsFlatGround() {
		return object.getType().getGroundtypes()[0] != ELandscapeType.MOUNTAIN;
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
	public List<? extends IBuildingOccupyer> getOccupyers() {
		return new LinkedList<IBuildingOccupyer>();
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
