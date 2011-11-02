package jsettlers.logic.movable.soldiers;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.spawn.Barrack;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import jsettlers.logic.movable.bearer.BearerStrategy;

/**
 * Lets the bearer go to a weapon stack at a barrack, pick it up and then go to
 * its door and become a soldier.
 * 
 * @author michael
 */
public class BecomeSoldierStrategy extends PathableStrategy {

	private static final EMaterialType[] WEAPONS = new EMaterialType[] {
	        EMaterialType.BOW, EMaterialType.SWORD, EMaterialType.SPEAR,
	};

	private final ISPosition2D weaponPosition;

	private EState nextState = EState.GO_TO_BARRACK;

	private final Barrack barrack;

	public BecomeSoldierStrategy(IMovableGrid grid, Movable movable,
	        ISPosition2D weaponPosition, Barrack barrack) {
		super(grid, movable);
		this.weaponPosition = weaponPosition;
		this.barrack = barrack;
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			nextState();
		}
		return true;
	}

	private void nextState() {
		switch (nextState) {
			case TAKE_DROP:
				nextState = EState.CONVERT;
				super.setAction(EAction.DROP,
				        Constants.MOVABLE_TAKE_DROP_DURATION);
				break;

			case CONVERT:
				EMaterialType took = null;
				for (EMaterialType type : WEAPONS) {
					if (super.getGrid().popMaterial(weaponPosition, type)) {
						took = type;
						break;
					}
				}
				if (took != null) {
					barrack.requestFullfilled(took);
					super.convertTo(getTypeForMaterial(took));
				} else {
					abort();
				}
				break;

			default:
				break;
		}
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			switch (nextState) {
				case GO_TO_BARRACK:
					nextState = EState.TAKE_DROP;
					ISPosition2D flag = barrack.getFlag();
					super.calculatePathTo(flag);
					return true;
				default:
					return false;
			}
		}
		return true;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected void pathFinished() {
		nextState();
	}

	@Override
	protected void pathRequestFailed() {
		abort();
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	private EMovableType getTypeForMaterial(EMaterialType material) {
		if (material == EMaterialType.BOW) {
			return EMovableType.BOWMAN_L1;
		} else if (material == EMaterialType.SPEAR) {
			return EMovableType.PIKEMAN_L1;
		} else {
			return EMovableType.SWORDSMAN_L1;
		}
	}

	private void abort() {
		// become a bearer again
		barrack.abortedForPosition(weaponPosition);
		this.movable.setStrategy(new BearerStrategy(getGrid(), movable));
	}

	private enum EState {
		GO_TO_BARRACK,
		// drop with no material
		TAKE_DROP,
		TAKE_TAKE,
		GO_TO_DOOR,
		CONVERT,
		ABORTED
	}
}
