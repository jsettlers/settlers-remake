package jsettlers.logic.buildings.military;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.IRequestStackListener;
import jsettlers.logic.stack.RequestStack;

/**
 * This is the barrack building. It requests weapons and bearers to make them to soldiers.
 * 
 * @author Andreas Eberle
 */
public final class Barrack extends WorkAreaBuilding implements IBarrack, IRequestStackListener {
	private static final long serialVersionUID = -6541972855836598068L;

	public Barrack(Player player) {
		super(EBuildingType.BARRACK, player);
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		EPriority priority = stop ? EPriority.STOPPED : EPriority.LOW;

		for (RequestStack curr : super.getStacks()) {
			curr.setPriority(priority);
		}
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public EMovableType popWeaponForBearer() {
		List<RequestStack> stacks = super.getStacks();
		for (RequestStack stack : stacks) {
			if (stack.getMaterialType() == EMaterialType.BOW || stack.getMaterialType() == EMaterialType.SWORD
					|| stack.getMaterialType() == EMaterialType.SPEAR) {
				if (stack.hasMaterial()) {
					stack.pop();
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
		super.getGrid().requestSoilderable(this);
	}

	@Override
	protected void constructionFinishedEvent() {
		for (RequestStack curr : super.getStacks()) {
			curr.setListener(this);
		}
	}

	@Override
	protected void subTimerEvent() {
	}

	@Override
	public ShortPoint2D getSoldierTargetPosition() {
		return super.getWorkAreaCenter();
	}

	@Override
	public void materialDelivered(RequestStack stack) {
		getGrid().requestSoilderable(Barrack.this);
	}
}
