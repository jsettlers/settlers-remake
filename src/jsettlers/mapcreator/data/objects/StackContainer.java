package jsettlers.mapcreator.data.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.RelativePoint;

public class StackContainer implements ObjectContainer, IStackMapObject {

	private final StackObject object;

	public StackContainer(StackObject object) {
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
		return EMapObjectType.STACK_OBJECT;
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
	public EMaterialType getMaterialType() {
		return object.getType();
	}

	@Override
	public byte getSize() {
		return (byte) object.getCount();
	}

}
