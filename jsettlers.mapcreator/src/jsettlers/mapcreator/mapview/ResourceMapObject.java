package jsettlers.mapcreator.mapview;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;

public class ResourceMapObject implements IMapObject {

	private final byte resourceAmount;
	private final EResourceType resourceType;

	public ResourceMapObject(EResourceType resourceType, byte resourceAmount) {
		this.resourceType = resourceType;
		this.resourceAmount = resourceAmount;
	    
    }

	public static IMapObject get(EResourceType resourceType, byte resourceAmount) {
	    return new ResourceMapObject(resourceType, resourceAmount);
    }

	@Override
    public EMapObjectType getObjectType() {
		switch (resourceType) {
			case COAL:
				return EMapObjectType.FOUND_COAL;
				
			case GOLD:
				return EMapObjectType.FOUND_GOLD;
				
			case IRON:
				return EMapObjectType.FOUND_IRON;
				
			case FISH:
				return EMapObjectType.FISH_DECORATION;
		}
	    return EMapObjectType.FOUND_NOTHING;
    }

	@Override
    public float getStateProgress() {
	    return (float) resourceAmount / Byte.MAX_VALUE;
    }

	@Override
    public IMapObject getNextObject() {
	    return null;
    }

}
