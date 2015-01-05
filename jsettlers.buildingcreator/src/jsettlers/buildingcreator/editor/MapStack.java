package jsettlers.buildingcreator.editor;

import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;

public class MapStack implements IStackMapObject {

	private final RelativeStack stack;

	public MapStack(RelativeStack stack) {
		this.stack = stack;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EMaterialType getMaterialType() {
		return stack.getMaterialType();
	}

	@Override
	public byte getSize() {
		return (byte) (stack.requiredForBuild() == 0 ? 8 : 3);
	}

}
