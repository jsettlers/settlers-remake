package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class HeightAdder implements Tool {

	private static final int INCREASE_HEIGHT = 5;
	private final boolean subtract;

	private int[][] alreadyadded = null;

	public HeightAdder(boolean subtract) {
		this.subtract = subtract;
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel(subtract ? "decreaseheightdescr"
		        : "increaseheightdescr");
	}

	@Override
	public ShapeType[] getShapes() {
		return LandscapeHeightTool.LANDSCAPE_SHAPES;
	}

	@Override
	public void start(MapData data, ShapeType shape, ISPosition2D pos) {
		// do nothing.
		alreadyadded = new int[data.getWidth()][data.getHeight()];
	}

	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D start,
	        ISPosition2D end, double uidx) {
		if (alreadyadded == null) {
			alreadyadded = new int[map.getWidth()][map.getHeight()];
		}

		byte[][] influence = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(influence, start, end);
		int factor = subtract ? -1 : 1;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				int dheight =
				        (INCREASE_HEIGHT * influence[x][y] / Byte.MAX_VALUE);
				if (dheight == 0) {
					continue;
				}

				int apply;

				if (alreadyadded[x][y] > dheight) {
					apply = 0;
				} else {
					apply = dheight - alreadyadded[x][y];
					alreadyadded[x][y] = dheight;
				}

				int newheight = (factor * apply + map.getLandscapeHeight(x, y));
				map.setHeight(x, y, newheight);
			}
		}
	}

}
