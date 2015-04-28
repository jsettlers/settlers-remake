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
package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;
import jsettlers.logic.player.Player;

/**
 * This is a mine building. It's only difference to a {@link WorkerBuilding} is that it's ground won't be flattened.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MineBuilding extends WorkerBuilding {
	private static final long serialVersionUID = 9201058266194063092L;

	public MineBuilding(EBuildingType type, Player player) {
		super(type, player);
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
}
