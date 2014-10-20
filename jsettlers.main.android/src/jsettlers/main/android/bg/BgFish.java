package jsettlers.main.android.bg;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;

public class BgFish implements IMapObject {

	private EMapObjectType type;

	public BgFish(EMapObjectType type) {
		super();
		this.type = type;
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
	public IMapObject getNextObject() {
		return null;
	}

}
