package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;

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
		addHeader("building.header");

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
				addErrorMessage("building.outside-map", pos, Labels.getName(type));
			} else if (!MapData.listAllowsLandscape(type.getGroundtypes(), data.getLandscape(pos.x, pos.y))) {
				ELandscapeType landscape = data.getLandscape(pos.x, pos.y);
				String landscapeName = EditorLabels.getLabel("landscape." + landscape.name());
				addErrorMessage("building.wrong-landscape", pos, Labels.getName(type), landscapeName);
			} else if (players[pos.x][pos.y] != buildingObject.getPlayerId()) {
				addErrorMessage("building.wrong-land", pos, Labels.getName(type), buildingObject.getPlayerId(), players[x][y]);
			} else if (type.getGroundtypes()[0] != ELandscapeType.MOUNTAIN && data.getLandscapeHeight(pos.x, pos.y) != height) {
				addErrorMessage("building.flat-ground", pos, Labels.getName(type), buildingObject.getPlayerId());
			}
		}
	}

}
