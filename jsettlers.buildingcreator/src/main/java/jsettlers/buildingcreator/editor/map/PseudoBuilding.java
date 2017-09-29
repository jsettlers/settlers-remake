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
package jsettlers.buildingcreator.editor.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

public class PseudoBuilding implements IBuilding, IBuilding.IMill {
	private final EBuildingType type;
	private final ShortPoint2D pos;

	PseudoBuilding(EBuildingType type, ShortPoint2D pos) {
		this.type = type;
		this.pos = pos;
	}

	@Override
	public EBuildingType getBuildingType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return 1;
	}

	@Override
	public ShortPoint2D getPosition() {
		return pos;
	}

	@Override
	public IPlayer getPlayer() {
		return new IPlayer.DummyPlayer();
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean b) {
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.BUILDING;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public boolean isOccupied() {
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
	public EPriority getPriority() {
		return EPriority.LOW;
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		return new EPriority[0];
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return false;
	}

	@Override
	public boolean isRotating() {
		return false;
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}

	@Override
	public boolean cannotWork() {
		return false;
	}

	@Override
	public ArrayList<EMaterialType> getRemainingOrder() {
		return new ArrayList<>();
	}
}
