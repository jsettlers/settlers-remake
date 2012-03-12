package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.FuzzyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.LineCircleShape;
import jsettlers.mapcreator.tools.shapes.NoisyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class LandscapeHeightTool implements Tool {

	public static final ShapeType[] LANDSCAPE_SHAPES = new ShapeType[] {
	        new LineCircleShape(),
	        new FuzzyLineCircleShape(),
	        new NoisyLineCircleShape()
	};
	private ISPosition2D start = new ShortPoint2D(0, 0);
	private byte[][] influences;
	private double[][] carry;

	public LandscapeHeightTool() {
	}

	@Override
	public String getName() {
		return "change height";
	}

	@Override
	public ShapeType[] getShapes() {
		return LANDSCAPE_SHAPES;
	}

	// TODO: this should me done in screen space!
	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D unused,
	        ISPosition2D unused2, double uidx) {
		
		double factor = uidx / 10000f;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				double dheight = factor * influences[x][y] + carry[x][y];
				int apply = (int) dheight;
				carry[x][y] = dheight - apply;
				
				if (apply == 0) {
					continue;
				}
				
				int newheight = (apply + map.getLandscapeHeight(x, y));
				map.setHeight(x, y, newheight);
			}
		}
	}

	@Override
	public void start(MapData map, ShapeType shape, ISPosition2D pos) {
		start = pos;
		influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, start);
		carry = new double[map.getWidth()][map.getHeight()];
	}
}
