package jsettlers.mapcreator.tools;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;

public class PlaceMapObjectTool implements Tool {
	private static final ShapeType[] SHAPES = new ShapeType[] {
		new PointShape(),
		new GridCircleShape(),
	};

	private final MapObject object;

	public PlaceMapObjectTool(MapObject object) {
		this.object = object;
	}

	@Override
	public String getName() {
		if (object instanceof MapStoneObject) {
			return "place " + ((MapStoneObject) object).getCapacity()
			        + " stones";
		} else if (object instanceof MapTreeObject) {
			return "place tree";
		} else {
			return "place " + object.getClass().getSimpleName();
		}
	}

	@Override
	public ShapeType[] getShapes() {
		return SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D start,
	        ISPosition2D end, double uidx) {

		byte[][] placeAt = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(placeAt, start, end);

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (placeAt[x][y] > Byte.MAX_VALUE / 2) {
					map.placeObject(getObject(), x, y);
				}
			}
		}
	}

	public MapObject getObject() {
		return object;
	}

	@Override
	public void start(MapData data, ShapeType shape, ISPosition2D pos) {
	}
}
