package jsettlers.common.map.object;

import jsettlers.common.mapobject.EMapObjectType;

public class MapDecorationObject implements MapObject {
	private final EMapObjectType type;

	public MapDecorationObject(EMapObjectType type) {
		this.type = type;
	}

	public EMapObjectType getType() {
		return type;
	}
}
