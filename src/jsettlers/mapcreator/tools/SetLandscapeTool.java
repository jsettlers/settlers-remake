package jsettlers.mapcreator.tools;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;

public class SetLandscapeTool implements Tool {

	private static final ShapeType[] SHAPES = new ShapeType[] {
		new PointShape(),
		new NoisyLineCircleShape(),
		new FuzzyLineCircleShape(),
		new LineCircleShape(),
	};
	
	private static final ShapeType[] RIVERSHAPES = new ShapeType[] {
		new PointShape(),
	};

	private final ELandscapeType type;

	private final boolean isRiver;

	public SetLandscapeTool(ELandscapeType type, boolean isRiver) {
		this.type = type;
		this.isRiver = isRiver;
	}

	@Override
	public String getName() {
		return "set landscape to " + type;
	}

	@Override
	public ShapeType[] getShapes() {
		return isRiver ? RIVERSHAPES : SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D start,
	        ISPosition2D end, double uidx) {

		byte[][] placeAt = new byte[map.getWidth()][map.getHeight()];
		shape.setAffectedStatus(placeAt, start, end);

		IMapArea area = new ByteMapArea(placeAt);

		map.fill(type, area);
	}
	
	@Override
	public void start(MapData data, ShapeType shape, ISPosition2D pos) {
	}

}
