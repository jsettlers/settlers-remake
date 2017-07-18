/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;

/**
 * Draw the building circle to the players array, does not produce any error message
 * 
 * @author Andreas Butti
 */
public class ValidateDrawBuildingCircle extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateDrawBuildingCircle() {
	}

	@Override
	public void doTest() {
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapDataObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingMapDataObject) {
					BuildingMapDataObject buildingObject = (BuildingMapDataObject) mapObject;
					drawBuildingCircle(x, y, buildingObject);
				}
			}
		}
	}

	private void drawBuildingCircle(int x, int y, BuildingMapDataObject buildingObject) {
		byte player = buildingObject.getPlayerId();
		EBuildingType type = buildingObject.getType();
		if (type == EBuildingType.TOWER || type == EBuildingType.BIG_TOWER || type == EBuildingType.CASTLE) {
			MapCircle circle = new MapCircle(x, y, CommonConstants.TOWER_RADIUS);
			drawCircle(player, circle);
		}
	}

	private void drawCircle(byte player, MapCircle circle) {
		for (ShortPoint2D pos : circle) {
			if (data.contains(pos.x, pos.y) && players[pos.x][pos.y] == -1) {
				players[pos.x][pos.y] = player;
			}
		}
	}
}
