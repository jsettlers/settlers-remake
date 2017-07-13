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
package jsettlers.mapcreator.mapvalidator.tasks.error;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.result.fix.DeleteObjectFix;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;

/**
 * Validate all buildings, check player, ground and position
 * 
 * @author Andreas Butti
 */
public class ValidateBuildings extends AbstractValidationTask {

	/**
	 * Fix for wrong placed buildings
	 */
	private DeleteObjectFix fix = new DeleteObjectFix();

	/**
	 * Constructor
	 */
	public ValidateBuildings() {
	}

	@Override
	public void doTest() {
		addHeader("building.header", fix);

		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapDataObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingMapDataObject) {
					testBuilding(x, y, (BuildingMapDataObject) mapObject);
				}
			}
		}
	}

	/**
	 * Test if the Building is valid at this location
	 * 
	 * @param x
	 *            X pos
	 * @param y
	 *            Y Pos
	 * @param buildingObject
	 *            Building
	 */
	private void testBuilding(int x, int y, BuildingMapDataObject buildingObject) {
		EBuildingType type = buildingObject.getType();
		int height = data.getLandscapeHeight(x, y);
		ShortPoint2D start = new ShortPoint2D(x, y);

		for (RelativePoint p : type.getProtectedTiles()) {
			ShortPoint2D pos = p.calculatePoint(start);
			if (!data.contains(pos.x, pos.y)) {
				addErrorMessage("building.outside-map", pos, Labels.getName(type));
				fix.addInvalidObject(pos);
			} else if (!type.getGroundTypes().contains(data.getLandscape(pos.x, pos.y))) {
				ELandscapeType landscape = data.getLandscape(pos.x, pos.y);
				String landscapeName = EditorLabels.getLabel("landscape." + landscape.name());
				addErrorMessage("building.wrong-landscape", pos, Labels.getName(type), landscapeName);
				fix.addInvalidObject(pos);
			} else if (players[pos.x][pos.y] != buildingObject.getPlayerId()) {
				addErrorMessage("building.wrong-land", pos, Labels.getName(type), buildingObject.getPlayerId(), players[x][y]);
				fix.addInvalidObject(pos);
			} else if (type.needsFlattenedGround() && data.getLandscapeHeight(pos.x, pos.y) != height) {
				addErrorMessage("building.flat-ground", pos, Labels.getName(type), buildingObject.getPlayerId());
				fix.addInvalidObject(pos);
			}
		}
	}

}
