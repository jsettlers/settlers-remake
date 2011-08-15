package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

public class StandardMapObject extends AbstractHexMapObject {

	private final EMapObjectType type;
	private final boolean blocking;

	public StandardMapObject(EMapObjectType type, boolean blocking) {
		this.type = type;
		this.blocking = blocking;
	}

	@Override
	public EMapObjectType getObjectType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return blocking;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

}
