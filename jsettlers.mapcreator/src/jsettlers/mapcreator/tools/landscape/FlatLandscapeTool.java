package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * makes a flat space on the landscape
 * 
 * 
 * @author michael
 */
public class FlatLandscapeTool implements Tool {

	private byte[][] old;
	private double influencefactor = .3;

	public FlatLandscapeTool() {
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("flaten");
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

		long heightsum = 0;
		long heightweights = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				heightsum += influences[x][y] * old[x][y];
				heightweights += influences[x][y];
			}
		}

		double desired = (double) heightsum / heightweights;

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				if (influences[x][y] == 0) {
					continue;
				}
				int oldheight = map.getLandscapeHeight(x, y);
				double influence = influencefactor * influences[x][y] / Byte.MAX_VALUE;
				int newheight =
						(int) (influence * desired + (1 - influence)
								* old[x][y]);
				if (desired < old[x][y]) {
					if (newheight < oldheight) {
						map.setHeight(x, y, newheight);
					}
				} else {
					if (newheight > oldheight) {
						map.setHeight(x, y, newheight);
					}
				}
			}
		}
	}

	@Override
	public void start(MapData map, ShapeType shape, ShortPoint2D pos) {
		old = new byte[map.getWidth()][map.getHeight()];

		for (int x = 0; x < old.length; x++) {
			for (int y = 0; y < old[x].length; y++) {
				old[x][y] = map.getLandscapeHeight(x, y);
			}
		}

	}
}
