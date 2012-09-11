package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.objects.StandardMapObject;

/**
 * This map object lies at the door of a tower and is used to signal soldiers that there is something to attack.
 * 
 * @author Andreas Eberle
 * 
 */
public class AttackableTowerMapObject extends StandardMapObject implements IAttackable, IAttackableTowerMapObject {

	private static final long serialVersionUID = -5137593316096740750L;
	private final OccupyingBuilding tower;

	public AttackableTowerMapObject(OccupyingBuilding tower) {
		super(EMapObjectType.ATTACKABLE_TOWER, false, tower.getPlayer());
		this.tower = tower;
	}

	@Override
	public ShortPoint2D getPos() {
		return tower.getDoor();
	}

	@Override
	public void hit(float strength) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isAttackable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMovable getMovable() {
		// if (!tower.getOccupyers().isEmpty()) {
		// return tower.getOccupyers().get(0).getMovable();
		// } else {
		return null;
		// }
	}

	@Override
	public EMovableType getMovableType() {
		assert false : "This should never have been called";
		return EMovableType.SWORDSMAN_L1;
	}
}
