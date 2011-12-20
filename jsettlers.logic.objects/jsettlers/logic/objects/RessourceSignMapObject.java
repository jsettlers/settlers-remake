package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.map.newGrid.objects.AbstractObjectsManagerObject;
import random.RandomSingleton;

public class RessourceSignMapObject extends AbstractObjectsManagerObject {
	private static final long serialVersionUID = -7248748388147081545L;

	private static final float MINIMUM_LIVETIME = 2 * 60;
	private static final float MAX_RANDOM_LIVETIME = 3 * 60;

	private final float amount;
	private final EMapObjectType type;

	public RessourceSignMapObject(ISPosition2D pos, EResourceType resourceType, float amount) {
		super(pos);
		this.amount = amount;

		switch (resourceType) {
		case COAL:
			type = EMapObjectType.FOUND_COAL;
			break;
		case IRON:
			type = EMapObjectType.FOUND_IRON;
			break;
		case GOLD:
			type = EMapObjectType.FOUND_GOLD;
			break;
		default:
			throw new IllegalArgumentException("Can't create ressource sign for: " + resourceType);
		}
	}

	@Override
	public EMapObjectType getObjectType() {
		return type;
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
