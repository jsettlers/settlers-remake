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
package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.RelativeToRealPointIterable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.objects.MapObjectsManager;
import jsettlers.logic.player.Player;

/**
 * This is a mine building. It's ground won't be flattened.
 *
 * @author Andreas Eberle
 *
 */
public final class MineBuilding extends ResourceBuilding {
	private static final long serialVersionUID = 9201058266194063092L;
	private static final byte[] workPackagesForFoodByOrder = { 10, 4, 2 };

	private byte feedWorkPackages = 10; // remaining work packages gained by eating food.

	public MineBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
	}

	@Override
	public boolean tryTakingFood(EMaterialType[] foodOrder) {
		if (feedWorkPackages <= 0) {
			for (int i = 0; i < foodOrder.length; i++) { // check the types of food by order
				if (super.popMaterialFromStack(foodOrder[i])) {
					feedWorkPackages = workPackagesForFoodByOrder[i];
					break;
				}
			}
		}

		if (feedWorkPackages > 0) {
			feedWorkPackages--;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean tryTakingResource() {
		RelativePoint[] blockedPositions = super.getBuildingType().getBlockedTiles();
		int randomPositionIndex = MatchConstants.random().nextInt(blockedPositions.length);
		ShortPoint2D randomPosition = blockedPositions[randomPositionIndex].calculatePoint(super.pos);

		boolean resourceTaken = super.grid.tryTakingResource(randomPosition, getProducedResource());
		super.productivityActionExecuted(resourceTaken);
		return resourceTaken;
	}

	private EResourceType getProducedResource() {
		switch (super.getBuildingType()) {
		case COALMINE:
			return EResourceType.COAL;
		case IRONMINE:
			return EResourceType.IRONORE;
		case GOLDMINE:
			return EResourceType.GOLDORE;
		default:
			throw new IllegalArgumentException("Unknown building type for a mine: " + super.getBuildingType());
		}
	}

	@Override
	protected boolean shouldBeFlatened() {
		return false;
	}

	@Override
	protected void placeAdditionalMapObjects(IBuildingsGrid grid, ShortPoint2D pos, boolean place) {
		if (place) {
			MapObjectsManager objectsManager = grid.getMapObjectsManager();
			for (ShortPoint2D currPos : new FreeMapArea(pos, super.getBuildingType().getProtectedTiles())) {
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_COAL);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_GOLD);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_IRON);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_BRIMSTONE);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_GEMSTONE);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_NOTHING);
			}
		}
	}

	@Override
	public int getRemainingResourceAmount() {
		return super.grid.getAmountOfResource(getProducedResource(),
				new RelativeToRealPointIterable(super.getBuildingType().getBlockedTiles(), super.pos));
	}
}
