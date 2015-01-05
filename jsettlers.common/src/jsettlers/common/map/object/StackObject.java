package jsettlers.common.map.object;

import jsettlers.common.material.EMaterialType;

public class StackObject implements MapObject {

	private final int count;
	private final EMaterialType type;

	public StackObject(EMaterialType type, int count) {
		this.type = type;
		this.count = count;
	}

	public EMaterialType getType() {
		return type;
	}

	public int getCount() {
		return count;
	}

}
