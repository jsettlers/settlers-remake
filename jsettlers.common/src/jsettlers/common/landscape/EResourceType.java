package jsettlers.common.landscape;

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

	public static final EResourceType[] values = EResourceType.values();

	public final byte ordinal;
	private final EMapObjectType mapObjectType;

	private EResourceType(EMapObjectType mapObjectType) {
		this.mapObjectType = mapObjectType;
		this.ordinal = (byte) super.ordinal();
	}

	public final EMapObjectType getMapObjectType() {
		return mapObjectType;
	}
}
