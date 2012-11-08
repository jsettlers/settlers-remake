package jsettlers.logic.buildings.military;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.stack.RequestStack;

/**
 * This is the barrack building. It requests bearers to become soldiers.
 * 
 * @author Andreas Eberle
 */
public class Barrack extends Building implements IBarrack {
	private static final long serialVersionUID = -6541972855836598068L;

	private boolean stoppedWorking = false;

	private int requestedBearer = 0;

	public Barrack(byte player) {
		super(EBuildingType.BARRACK, player);
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		stoppedWorking = stop;
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {

	}

	@Override
	protected void subTimerEvent() {
		if (!stoppedWorking) {
			int availableWeapons = 0;

			for (RequestStack stack : super.getStacks()) {
				if (stack.getMaterialType() == EMaterialType.BOW || stack.getMaterialType() == EMaterialType.SWORD
						|| stack.getMaterialType() == EMaterialType.SPEAR) {
					availableWeapons += stack.getStackSize();
				}
			}

			while (requestedBearer < availableWeapons) {
				super.getGrid().requestSoilderable(this);
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
	public ShortPoint2D getFlag() {
		RelativePoint flag = getBuildingType().getFlag();
		return calculateRealPoint(flag.getDx(), flag.getDy());
	}

	@Override
	public EMovableType popWeaponForBearer() {
		List<RequestStack> stacks = super.getStacks();
		for (RequestStack stack : stacks) {
			if (stack.getMaterialType() == EMaterialType.BOW || stack.getMaterialType() == EMaterialType.SWORD
					|| stack.getMaterialType() == EMaterialType.SPEAR) {
				if (stack.hasMaterial()) {
					stack.pop();
					requestedBearer--;
					return getSoldierType(stack.getMaterialType());
				}
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
	public final boolean isOccupied() {
		return true;
	}

	@Override
	public void bearerRequestFailed() {
		requestedBearer--;
	}

}
