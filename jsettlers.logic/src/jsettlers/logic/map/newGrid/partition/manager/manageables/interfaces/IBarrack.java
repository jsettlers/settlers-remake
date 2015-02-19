package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * Interface for a building that can request beares to go there and become soldiers.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBarrack extends ILocatable {

	/**
	 * 
	 * @return position of the door.
	 */
	ShortPoint2D getDoor();

	/**
	 * Get a weapon from the barrack for this bearer.
	 * 
	 * @return The movable type this bearer should convert to.
	 */
	EMovableType popWeaponForBearer();

	/**
	 * This method is called when a movable wasn't able to become a soldier, that means when it wasn't able to fullfil the request.
	 */
	void bearerRequestFailed();

	/**
	 * 
	 * @return Returns the position where the newly created soldier should walk.
	 */
	ShortPoint2D getSoldierTargetPosition();
}
