package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

public class StoneObjectContainer implements ObjectContainer, IMapObject {
	MapStoneObject peer;

	public StoneObjectContainer(int stones) {
		this(MapStoneObject.getInstance(stones));
	}

	public StoneObjectContainer(MapStoneObject object) {
		this.peer = object;
	}

	@Override
	public MapObject getMapObject() {
		return peer;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
	public float getStateProgress() {
		return peer.getCapacity();
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
		        new RelativePoint(0, 0),
		        //inner circle
		        new RelativePoint(1, 0),
		        new RelativePoint(1, 1),
		        new RelativePoint(0, 1),
		        new RelativePoint(-1, 0),
		        new RelativePoint(-1, -1),
		        new RelativePoint(0, -1),
		        //outer circle
		        new RelativePoint(2, 0),
		        new RelativePoint(1, 0),
		        new RelativePoint(2, 2),
		        new RelativePoint(1, 2),
		        new RelativePoint(0, 2),
		        new RelativePoint(-1, 1),
		        new RelativePoint(-2, 0),
		        new RelativePoint(-2, -1),
		        new RelativePoint(-2, -2),
		        new RelativePoint(-1, -2),
		        new RelativePoint(0, -2),
		        new RelativePoint(1, -1),
		};
	}

}
