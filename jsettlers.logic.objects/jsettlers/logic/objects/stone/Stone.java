package jsettlers.logic.objects.stone;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

public class Stone extends AbstractHexMapObject {

	private static final int MAX_CAPACITY = 12;
	private byte imageIdx = 0;

	public Stone() {
		this(MAX_CAPACITY);
	}

	public Stone(int capacity) {
		imageIdx = (byte) capacity;
    }

	@Override
	public boolean cutOff() {
		if (!canBeCut()) {
			return false;
		}

		this.imageIdx--;
		return true;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
	public float getStateProgress() {
		return imageIdx;
	}

	@Override
	public boolean isBlocking() {
		return canBeCut();
	}

	@Override
	public boolean canBeCut() {
		return imageIdx > 0;
	}

}
