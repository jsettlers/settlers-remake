package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.player.Player;

/**
 * This interface defines the methods needed by a tower that it can request soldiers to get in.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IOccupyableBuilding {

	/**
	 * 
	 * @param soldier
	 * @return
	 */
	OccupyerPlace addSoldier(IBuildingOccupyableMovable soldier);

	ShortPoint2D getDoor();

	void requestFailed(EMovableType movableType);

	ShortPoint2D getPosition(IBuildingOccupyableMovable soldier);

	boolean isNotDestroyed();

	/**
	 * This method is called by the soldier when he finished defending the tower.
	 * 
	 * @param soldier
	 *            The soldier that defended the tower.
	 */
	void towerDefended(IBuildingOccupyableMovable soldier);

	public ShortPoint2D getTowerBowmanSearchPosition(OccupyerPlace place);

	/**
	 * 
	 * @return The player of this building object.
	 */
	Player getPlayer();

	/**
	 * Removes the given soldier from this building.
	 * 
	 * @param soldier
	 *            The soldier that will be removed.
	 */
	void removeSoldier(IBuildingOccupyableMovable soldier);

}
