package jsettlers.graphics.test;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;

public class TestStack implements IMapObject {

	private final EMaterialType material;
	private final int count;

	public TestStack(EMaterialType material, int count) {
		this.material = material;
		this.count = count;
	}

	public EMaterialType getMaterial() {
		return this.material;
	}

	public IMapObject getNextStack() {
		return null;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STACK_OBJECT;
	}

	@Override
	public float getStateProgress() {
		return (byte) this.count;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

}
