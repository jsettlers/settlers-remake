package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.map.newGrid.objects.AbstractObjectsManagerObject;
import random.RandomSingleton;

public final class RessourceSignMapObject extends AbstractObjectsManagerObject {
	private static final long serialVersionUID = -7248748388147081545L;

	private static final float MINIMUM_LIVETIME = 2 * 60;
	private static final float MAX_RANDOM_LIVETIME = 3 * 60;

	private final float amount;
	private final byte objectType;

	public RessourceSignMapObject(ISPosition2D pos, EResourceType resourceType, float amount) {
		super(pos);
		this.amount = amount;

		switch (resourceType) {
		case COAL:
			objectType = EMapObjectType.FOUND_COAL.ordinal;
			break;
		case IRON:
			objectType = EMapObjectType.FOUND_IRON.ordinal;
			break;
		case GOLD:
			objectType = EMapObjectType.FOUND_GOLD.ordinal;
			break;
		default:
			throw new IllegalArgumentException("Can't create ressource sign for: " + resourceType);
		}
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.values[objectType];
	}

	@Override
	public float getStateProgress() {
		return amount;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		throw new UnsupportedOperationException();
	}

	public static final float getLivetime() {
		return RandomSingleton.nextF() * MAX_RANDOM_LIVETIME + MINIMUM_LIVETIME;
	}

}
