package jsettlers.logic.buildings.military;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;

public interface IOccupyableBuilding {

	ShortPoint2D addSoldier(IBuildingOccupyableMovable soldier);

	ShortPoint2D getDoor();

	void requestFailed(EMovableType movableType);

	ShortPoint2D getPosition(IBuildingOccupyableMovable soldier);

	boolean isNotDestroyed();

	byte getPlayer();

	/**
	 * This method is called by the soldier when he finished defending the tower.
	 * 
	 * @param soldier
	 *            The soldier that defended the tower.
	 */
	void towerDefended(IBuildingOccupyableMovable soldier);

}
