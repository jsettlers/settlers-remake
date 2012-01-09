package jsettlers.mapcreator.tools;

import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;

public interface Tool extends ToolNode {
	public ShapeType[] getShapes();
	
	public void apply(MapData map, ShapeType shape, ISPosition2D start, ISPosition2D end, double uidx);

	public void start(MapData data, ShapeType shape, ISPosition2D pos);
}
