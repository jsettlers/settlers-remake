package jsettlers.mapcreator.tools.landscape;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.buffers.ByteMapArea;
import jsettlers.mapcreator.tools.buffers.GlobalShapeBuffer;
import jsettlers.mapcreator.tools.shapes.LineCircleShape;
import jsettlers.mapcreator.tools.shapes.LineShape;
import jsettlers.mapcreator.tools.shapes.NoisyLineCircleShape;
import jsettlers.mapcreator.tools.shapes.PointShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class SetLandscapeTool implements Tool {

	private static final ShapeType[] SHAPES = new ShapeType[] {
	        new PointShape(),
	        new LineShape(),
	        new LineCircleShape(),
	        new NoisyLineCircleShape(),
	};

	private static final ShapeType[] RIVERSHAPES = new ShapeType[] {
	        new PointShape(), new LineShape(),
	};

	private final ELandscapeType type;

	private final boolean isRiver;

	private GlobalShapeBuffer buffer;

	public SetLandscapeTool(ELandscapeType type, boolean isRiver) {
		this.type = type;
		this.isRiver = isRiver;
	}

	@Override
	public String getName() {
		return String.format(EditorLabels.getLabel("landscapedescr"), EditorLabels.getLabel("landscape_"+type));
	}

	@Override
	public ShapeType[] getShapes() {
		return isRiver ? RIVERSHAPES : SHAPES;
	}

	@Override
	public void apply(MapData map, ShapeType shape, ISPosition2D start,
	        ISPosition2D end, double uidx) {
		if (buffer == null) {
			buffer = new GlobalShapeBuffer(map.getWidth(), map.getHeight());
		}

		short startx = start.getX();
		short endx = end.getX();
		short starty = start.getY();
		short endy = end.getY();
		int size = shape.getSize();
		int usedminx = Math.min(startx, endx) - size - 3;
		int usedminy = Math.min(starty, endy) - (int) (size / MapCircle.Y_SCALE) - 3;
		int usedmaxx = Math.max(startx, endx) + size + 3;
		int usedmaxy = Math.max(starty, endy) + (int) (size / MapCircle.Y_SCALE) + 3;
		byte[][] array =
		        buffer.getArray(usedminx, usedminy, usedmaxx, usedmaxy);

		shape.setAffectedStatus(array, start, end);

		IMapArea area = new ByteMapArea(array);

		map.fill(type, area);
	}

	@Override
	public void start(MapData data, ShapeType shape, ISPosition2D pos) {
	}

}
