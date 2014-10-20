package jsettlers.graphics.test;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;

public class TestStone implements IMapObject {

	private final int i;

	public TestStone(int i) {
		this.i = i;
	}

	@Override
	public float getStateProgress() {
		return this.i;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
    public IMapObject getNextObject() {
	    return null;
    }

}
