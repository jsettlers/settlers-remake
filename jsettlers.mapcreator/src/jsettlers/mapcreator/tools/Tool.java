package jsettlers.mapcreator.tools;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public interface Tool extends ToolNode {
	public ShapeType[] getShapes();
	
	public void apply(MapData map, ShapeType shape, ShortPoint2D start, ShortPoint2D end, double uidx);

	public void start(MapData data, ShapeType shape, ShortPoint2D pos);
}
