package jsettlers.mapcreator.tools;

import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.main.DataTester;

public class FixHeightsTool implements Tool {
	@Override
	public String getName() {
		return "fix heights";
	}

	@Override
	public ShapeType[] getShapes() {
		return LandscapeHeightTool.LANDSCAPE_SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D start,
	        ISPosition2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);

		for (int x = 0; x < map.getWidth() - 1; x++) {
			for (int y = 0; y < map.getWidth() - 1; y++) {
				if (influences[x][y] > 0) {
					fix(map, x, y, x + 1, y);
					fix(map, x, y, x + 1, y + 1);
					fix(map, x, y, x, y + 1);
				}
			}
		}
	}

	private static void fix(MapData map, int x, int y, int x2, int y2) {
		byte h1 = map.getLandscapeHeight(x, y);
		byte h2 = map.getLandscapeHeight(x2, y2);
		if (h1 - h2 > DataTester.MAX_HEIGHT_DIFF) {
			// h1 too big
			map.setHeight(x, y, h2 + DataTester.MAX_HEIGHT_DIFF);
		} else if (h2 - h1 > DataTester.MAX_HEIGHT_DIFF) {
			// h2 too big
			map.setHeight(x2, y2, h1 + DataTester.MAX_HEIGHT_DIFF);
		}
	}

	@Override
	public void start(MapData data, ShapeType shape, ISPosition2D pos) {

	}

}
