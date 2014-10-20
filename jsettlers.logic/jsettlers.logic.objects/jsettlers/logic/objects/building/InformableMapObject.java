package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IInformable;

/**
 * This map object can be used to get informed if an attackable movable enters a given area.
 * 
 * @author Andreas Eberle
 * 
 */
public class InformableMapObject extends AbstractHexMapObject implements IInformable {
	private static final long serialVersionUID = 1770958775947197434L;
	private final IInformable informable;

	public InformableMapObject(IInformable informable) {
		this.informable = informable;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.INFORMABLE_MAP_OBJECT;
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
	public boolean canBeCut() {
		return false;
	}

	@Override
	public void informAboutAttackable(IAttackable attackable) {
		this.informable.informAboutAttackable(attackable);
	}

}
