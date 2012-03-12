package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

public class TreeObjectContainer implements ObjectContainer, IMapObject {

	@Override
    public MapObject getMapObject() {
	    return MapTreeObject.getInstance();
    }
	
	public static ObjectContainer getInstance() {
	    return new TreeObjectContainer();
    }

	@Override
    public EMapObjectType getObjectType() {
	    return EMapObjectType.TREE_ADULT;
    }

	@Override
    public float getStateProgress() {
	    return 0;
    }

	@Override
    public IMapObject getNextObject() {
	    return null;
    }

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
		        new RelativePoint(0, 0),
		        new RelativePoint(1, 0),
		        new RelativePoint(1, 1),
		        new RelativePoint(0, 1),
		        new RelativePoint(-1, 0),
		        new RelativePoint(-1, -1),
		        new RelativePoint(0, -1),
		};
	}

}
