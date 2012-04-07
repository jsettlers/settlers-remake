package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.LandscapeConstraint;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.objects.ObjectContainer;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.DataTester;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class FixHeightsTool implements Tool {
	@Override
	public String getName() {
		return EditorLabels.getLabel("fixheights");
	}

	@Override
	public ShapeType[] getShapes() {
		return LandscapeHeightTool.LANDSCAPE_SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ShortPoint2D start,
	        ShortPoint2D end, double uidx) {
		byte[][] influences = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influences, start, end);

		for (int x = 0; x < map.getWidth() - 1; x++) {
			for (int y = 0; y < map.getWidth() - 1; y++) {
				if (influences[x][y] > 0) {
					fixResources(map, x, y);
					fix(map, x, y, x + 1, y);
					fix(map, x, y, x + 1, y + 1);
					fix(map, x, y, x, y + 1);
				}
			}
		}

		for (int x = map.getWidth() - 2; x >= 0; x--) {
			for (int y = map.getWidth() - 2; y >= 0; y--) {
				if (influences[x][y] > 0) {
					fix(map, x, y, x + 1, y);
					fix(map, x, y, x + 1, y + 1);
					fix(map, x, y, x, y + 1);
				}
			}
		}
	}

	private static void fixResources(MapData map, int x, int y) {
	    if (map.getResourceAmount((short) x, (short) y) > 0) {
	    	if (!DataTester.mayHoldResource(map.getLandscape(x, y), map.getResourceType((short) x, (short)  y))) {
	    		map.decreaseResourceTo(x, y, (byte) 0);
	    	}
	    }
    }

	private static void fix(MapData map, int x, int y, int x2, int y2) {
		byte h1 = map.getLandscapeHeight(x, y);
		byte h2 = map.getLandscapeHeight(x2, y2);
		ELandscapeType l1 = map.getLandscape(x, y);
		ELandscapeType l2 = map.getLandscape(x2, y2);

		int maxHeightDiff = DataTester.getMaxHeightDiff(l1, l2);

		ObjectContainer container1 = map.getMapObjectContainer(x, y);
		if (container1 instanceof LandscapeConstraint
		        && ((LandscapeConstraint) container1).needsFlatGround()) {
			maxHeightDiff = 0;
		}
		ObjectContainer container2 = map.getMapObjectContainer(x2, y2);
		if (container2 instanceof LandscapeConstraint
		        && ((LandscapeConstraint) container2).needsFlatGround()) {
			maxHeightDiff = 0;
		}

		if (h1 - h2 > maxHeightDiff) {
			// h1 too big
			map.setHeight(x, y, h2 + maxHeightDiff);
		} else if (h2 - h1 > maxHeightDiff) {
			// h2 too big
			map.setHeight(x2, y2, h1 + maxHeightDiff);
		}
	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {

	}

}
