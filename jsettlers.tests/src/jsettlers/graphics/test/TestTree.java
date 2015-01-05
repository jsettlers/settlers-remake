package jsettlers.graphics.test;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;

public class TestTree implements IMapObject {

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.TREE_ADULT;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

}
