package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.RelativePoint;

public interface ObjectContainer {
	public MapObject getMapObject();
	
	public RelativePoint[] getProtectedArea();
}
