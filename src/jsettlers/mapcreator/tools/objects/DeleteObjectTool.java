package jsettlers.mapcreator.tools.objects;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.LineCircleShape;
import jsettlers.mapcreator.tools.shapes.PointShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class DeleteObjectTool implements Tool {

	private static final ShapeType[] SHAPE_TYPES = new ShapeType[] {
    		new PointShape(),
    		new LineCircleShape(),
    };

	@Override
	public String getName() {
		return EditorLabels.getLabel("deletedescr");
	}

	@Override
	public ShapeType[] getShapes() {
		return SHAPE_TYPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
	        ShortPoint2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);
		
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (influences[x][y]> 0) {
					map.deleteObject(x, y);
				}
			}
		}
	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
	}

}
