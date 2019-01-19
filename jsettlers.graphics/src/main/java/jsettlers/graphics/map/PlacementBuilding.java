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
package jsettlers.graphics.map;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.player.IPlayer;
import jsettlers.common.player.IPlayer.DummyPlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

/**
 * This is a fake building that is drawn under the mouse while we are in the build mode.
 * 
 * @author ?
 *
 */
public class PlacementBuilding implements IBuilding, IBuilding.IMill, IBuilding.ISoundRequestable {
	private final DummyPlayer player = new DummyPlayer();
	private final EBuildingType typeToPlace;

	/**
	 * Create a new, fake {@link PlacementBuilding}.
	 * 
	 * @param type
	 *            The type.
	 */
	public PlacementBuilding(EBuildingType type) {
		this.typeToPlace = type;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.PLACEMENT_BUILDING;
	}

	@Override
	public EBuildingType getBuildingType() {
		return typeToPlace;
	}

	@Override
	public float getStateProgress() {
		return 1f;
	}

	@Override
	public boolean isRotating() {
		return false;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public boolean isWounded() {
		return false;
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
	public void setSelected(boolean selected) {
	}

	@Override
	public ESelectionType getSelectionType() {
		return null;
	}

	@Override
	public ShortPoint2D getPosition() {
		return null;
	}

	@Override
	public EPriority getPriority() {
		return null;
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		return null;
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public List<IBuildingMaterial> getMaterials() {
		return null;
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return false;
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		if (type == getObjectType()) {
			return this;
		} else {
			return null;
		}
	}

	@Override
	public boolean cannotWork() {
		return false;
	}

	@Override
	public boolean isSoundRequested() {
		return false;
	}

	@Override
	public void requestSound() {

	}
}
