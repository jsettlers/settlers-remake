package jsettlers.logic.map.newGrid.landscape;

import jsettlers.common.mapobject.EMapObjectType;

/**
 * These are the types of available resources.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EResourceType {
	COAL(EMapObjectType.FOUND_COAL),
	GOLD(EMapObjectType.FOUND_GOLD),
	IRON(EMapObjectType.FOUND_IRON),
	FISH(null);

	private final EMapObjectType mapObjectType;

	private EResourceType(EMapObjectType mapObjectType) {
		this.mapObjectType = mapObjectType;
	}

	public final EMapObjectType getMapObjectType() {
		return mapObjectType;
	}
}
