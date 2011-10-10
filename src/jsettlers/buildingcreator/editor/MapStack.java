package jsettlers.buildingcreator.editor;

import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.IStack;

public class MapStack implements IStack {

	private final RelativeStack stack;

	public MapStack(RelativeStack stack) {
		this.stack = stack;
    }

	@Override
	public EMaterialType getMaterial() {
		return stack.getType();
	}

	@Override
	public byte getNumberOfElements() {
		return (byte) (stack.requiredForBuild() == 0 ? 8 : 3);
	}

	@Override
	public IStack getNextStack() {
		return null;
	}

}
