package jsettlers.logic.movable.soldiers.behaviors;

import java.io.Serializable;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.movable.IHexMovable;
import jsettlers.logic.movable.IMovableGrid;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class SoldierBehavior implements Serializable {
	private static final long serialVersionUID = 8554360919442226371L;

	protected final ISoldierBehaviorable soldier;

	public abstract SoldierBehavior calculate(ShortPoint2D pos, IPathCalculateable pathCalcable);

	protected SoldierBehavior(ISoldierBehaviorable soldier) {
		this.soldier = soldier;
	}

	public final IMovableGrid getGrid() {
		return getSoldier().getGrid();
	}

	public final void goToTile(ShortPoint2D first) {
		soldier.goToTile(first);
	}

	protected final ISoldierBehaviorable getSoldier() {
		return soldier;
	}

	public abstract void pathRequestFailed();

	public boolean checkGoStepPrecondition() {
		return true;
	}

	public final byte getPlayer() {
		return soldier.getPlayer();
	}

	protected final void setAction(EAction action, int duration) {
		soldier.setAction(action, duration);
	}

	public void killedEvent(@SuppressWarnings("unused") EMovableType type) {
	}

	public final static SoldierBehavior getDefaultSoldierBehavior(ISoldierBehaviorable soldier) {
		return new WatchingBehavior(soldier);
	}

	public final static boolean isGotoJobable(SoldierBehavior behavior) {
		return !(behavior instanceof GoToTowerBehavior || behavior instanceof InfantryInTowerBehavior);
	}

	public final static boolean isInTower(SoldierBehavior behavior) {
		return behavior instanceof InfantryInTowerBehavior;
	}

	public final static boolean isPathStopable(SoldierBehavior behavior) {
		return !(behavior instanceof GoToTowerBehavior);
	}

	public final static SoldierBehavior getGoToTowerBehavior(ISoldierBehaviorable soldier, IOccupyableBuilding building) {
		return new GoToTowerBehavior(soldier, building);
	}

	protected final void setVisible(boolean visible) {
		soldier.setVisible(visible);
	}

	protected final IBuildingOccupyableMovable getBuildingOccupier() {
		return soldier.getBuildingOccupier();
	}

	public static SoldierBehavior getWatchingBehavior(ISoldierBehaviorable soldier) {
		return new WatchingBehavior(soldier);
	}

	public static SoldierBehavior getFlockingBehavior(ISoldierBehaviorable soldier, IHexMovable leader) {
		return new FlockingBehavior(soldier, leader);
	}

}
