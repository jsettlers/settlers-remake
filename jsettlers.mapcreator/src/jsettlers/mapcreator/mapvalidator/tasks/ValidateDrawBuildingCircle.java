package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;

/**
 * Draw the building circle to the players array
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
				MapObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof BuildingObject) {
					BuildingObject buildingObject = (BuildingObject) mapObject;
					drawBuildingCircle(x, y, buildingObject);
				}
			}
		}
	}

	private void drawBuildingCircle(int x, int y, BuildingObject buildingObject) {
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
