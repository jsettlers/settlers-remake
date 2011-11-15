package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.stack.RequestStack;

/**
 * This is the barrack building. It requests bearers to become soilders.
 * 
 * @author michael
 */
public class Barrack extends Building {
	private static final long serialVersionUID = -6541972855836598068L;

	private boolean stoppedWorking = false;

	private int requestedBearer = 0;

	public Barrack(byte player) {
		super(EBuildingType.BARRACK, player);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		stoppedWorking = stop;
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {

	}

	@Override
	protected void subTimerEvent() {
		if (!stoppedWorking) {
			int availableWeapons = 0;

			for (RequestStack stack : super.stacks) {
				if (stack.getMaterialType() == EMaterialType.BOW || stack.getMaterialType() == EMaterialType.SWORD
						|| stack.getMaterialType() == EMaterialType.SPEAR) {
					availableWeapons += stack.getStackSize();
				}
			}

			while (requestedBearer < availableWeapons) {
				grid.requestSoilderable(this);
				requestedBearer++;
			}
		}
	}

	@Override
	protected void constructionFinishedEvent() {
		placeFlag(true);
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	/**
	 * Gets the flag position.
	 * 
	 * @return The point in map space.
	 */
	public ISPosition2D getFlag() {
		RelativePoint flag = getBuildingType().getFlag();
		return calculateRealPoint(flag.getDx(), flag.getDy());
	}

	public EMovableType popWeaponForBearer() {
		for (RequestStack stack : super.stacks) {
			if (stack.getMaterialType() == EMaterialType.BOW || stack.getMaterialType() == EMaterialType.SWORD
					|| stack.getMaterialType() == EMaterialType.SPEAR) {
				stack.pop();
				requestedBearer--;
				return getSoldierType(stack.getMaterialType());
			}
		}

		return null;
	}

	private EMovableType getSoldierType(EMaterialType materialType) {
		switch (materialType) {
		case SWORD:
			return EMovableType.SWORDSMAN_L1;
		case BOW:
			return EMovableType.BOWMAN_L1;
		case SPEAR:
			return EMovableType.PIKEMAN_L1;
		default:
			return null;
		}
	}

	@Override
	public ISPosition2D getDoor() {
		return super.getDoor();
	}

}
