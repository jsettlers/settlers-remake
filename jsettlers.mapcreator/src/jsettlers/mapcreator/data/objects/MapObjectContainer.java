package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

public class MapObjectContainer implements ObjectContainer, IMapObject {

	private final MapDecorationObject object;

	public MapObjectContainer(MapDecorationObject object) {
		this.object = object;
	}

	@Override
	public MapObject getMapObject() {
		return object;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
				new RelativePoint(0, 0)
		};
	}

	@Override
	public EMapObjectType getObjectType() {
		return object.getType();
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

}
