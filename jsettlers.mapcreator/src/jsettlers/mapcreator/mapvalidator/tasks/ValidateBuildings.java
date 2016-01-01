package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;

/**
 * Validate all buildings, check player, ground and position
 * 
 * @author Andreas Butti
 */
public class ValidateBuildings extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateBuildings() {
	}

	@Override
	public void doTest() {
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingObject) {
					ShortPoint2D start = new ShortPoint2D(x, y);
					BuildingObject buildingObject = (BuildingObject) mapObject;
					testBuilding(x, y, start, buildingObject);
				}
			}
		}
	}

	private void testBuilding(int x, int y, ShortPoint2D start, BuildingObject buildingObject) {
		EBuildingType type = buildingObject.getType();
		int height = data.getLandscapeHeight(x, y);
		for (RelativePoint p : type.getProtectedTiles()) {
			ShortPoint2D pos = p.calculatePoint(start);
			if (!data.contains(pos.x, pos.y)) {
				testFailed("Building " + type + " outside map", pos);
			} else if (!MapData.listAllowsLandscape(type.getGroundtypes(), data.getLandscape(pos.x, pos.y))) {
				testFailed("Building " + type + " cannot be placed on " + data.getLandscape(pos.x, pos.y), pos);
			} else if (players[pos.x][pos.y] != buildingObject.getPlayerId()) {
				testFailed("Building " + type + " of player " + buildingObject.getPlayerId() + ", but is on " + players[x][y] + "'s land", pos);
			} else if (type.getGroundtypes()[0] != ELandscapeType.MOUNTAIN && data.getLandscapeHeight(pos.x, pos.y) != height) {
				testFailed("Building " + type + " of player " + buildingObject.getPlayerId() + " must be on flat ground", pos);
			}
		}
	}

}
