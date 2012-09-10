package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.objects.StandardMapObject;

public class AttackableTowerMapObject extends StandardMapObject implements IAttackable {

	private static final long serialVersionUID = -5137593316096740750L;
	private final OccupyingBuilding tower;

	public AttackableTowerMapObject(OccupyingBuilding tower) {
		super(EMapObjectType.ATTACKABLE_TOWER, false, tower.getPlayer());
		this.tower = tower;
	}

	@Override
	public ShortPoint2D getPos() {
		// TODO Auto-generated method stub
		return tower.getDoor();
	}

	@Override
	public void hit(float strength) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isAttackable() {
		// TODO Auto-generated method stub
		return false;
	}
}
