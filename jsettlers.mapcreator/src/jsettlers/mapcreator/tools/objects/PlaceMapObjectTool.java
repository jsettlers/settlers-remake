package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.GridCircleShape;
import jsettlers.mapcreator.tools.shapes.PointShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class PlaceMapObjectTool implements Tool {
	private static final ShapeType[] SHAPES = new ShapeType[] {
			new PointShape(), new GridCircleShape(),
	};

	private final MapObject object;

	public PlaceMapObjectTool(MapObject object) {
		this.object = object;
	}

	@Override
	public String getName() {
		if (object instanceof MapStoneObject) {
			return String.format(EditorLabels.getLabel("stonedescr"),
					((MapStoneObject) object).getCapacity());
		} else if (object instanceof MapTreeObject) {
			return EditorLabels.getLabel("treedescr");
		} else if (object instanceof MapDecorationObject) {
			return String.format(
					EditorLabels.getLabel("commondescr"),
					EditorLabels.getLabel("object_"
							+ ((MapDecorationObject) object).getType()));
		} else {
			return String.format(EditorLabels.getLabel("commondescr"), object
					.getClass().getSimpleName());
		}
	}

	@Override
	public ShapeType[] getShapes() {
		return SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
			ShortPoint2D end, double uidx) {

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
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
	}
}
