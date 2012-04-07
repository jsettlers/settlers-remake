package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.LineCircleShape;
import jsettlers.mapcreator.tools.shapes.LineShape;
import jsettlers.mapcreator.tools.shapes.NoisyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.PointShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class PlaceResource implements Tool, ResourceTool {

	private final EResourceType type;

	private static final ShapeType[] SHAPES = new ShapeType[] {
	        new PointShape(),
	        new LineShape(),
	        new LineCircleShape(),
	        new NoisyLineCircleShape(),
	};

	public PlaceResource(EResourceType type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type == null ? EditorLabels.getLabel("remove_resource") : String
		        .format(EditorLabels.getLabel("place_resource"),
		                Labels.getName(type));
	}

	@Override
	public ShapeType[] getShapes() {
		return SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
	        ShortPoint2D end, double uidx) {
		byte[][] influence = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influence, start, end);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (type != null) {
					map.addResource(x, y, type, influence[x][y]);
				} else {
					map.decreaseResourceTo(x, y,
					        (byte) (Byte.MAX_VALUE - influence[x][y]));
				}
			}
		}

	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
	}

}
