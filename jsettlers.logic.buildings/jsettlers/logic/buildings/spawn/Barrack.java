package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
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
	private boolean stoppedWorking = false;

	private int requestedSwordmen = 0;
	private int requestedBowmen = 0;
	private int requestedPikemen = 0;

	public Barrack(byte player) {
		super(EBuildingType.BARRACK, player);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public int getActionImgIdx() {
		return 0;
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
			for (RequestStack stack : super.stacks) {
				int stacksize = stack.getStackSize();
				if (stack.getMaterialType() == EMaterialType.BOW) {
					while (requestedBowmen < stacksize) {
						grid.requestSoilderable(stack.getPosition(), this);
						requestedBowmen++;
					}
				} else if (stack.getMaterialType() == EMaterialType.SWORD) {
					while (requestedSwordmen < stacksize) {
						grid.requestSoilderable(stack.getPosition(), this);
						requestedSwordmen++;
					}
				} else if (stack.getMaterialType() == EMaterialType.SPEAR) {
					while (requestedPikemen < stacksize) {
						grid.requestSoilderable(stack.getPosition(), this);
						requestedPikemen++;
					}
				}
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
	 * Called when a material was taken.
	 * 
	 * @param took
	 */
	public void requestFullfilled(EMaterialType took) {
		if (took == EMaterialType.BOW) {
			requestedBowmen--;
		} else if (took == EMaterialType.SWORD) {
			requestedSwordmen--;
		} else if (took == EMaterialType.SPEAR) {
			requestedPikemen--;
		}
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

	/**
	 * Called by a bearer that aborted to become a soilder.
	 * 
	 * @param weaponPosition
	 */
	public void abortedForPosition(ISPosition2D weaponPosition) {
		for (RequestStack stack : super.stacks) {
			if (stack.getPosition() == weaponPosition) {
				requestFullfilled(stack.getMaterialType());
			}
		}
	}

}
