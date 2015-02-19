package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IAttackableMovable;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBuildingOccupyableMovable {

	boolean setOccupyableBuilding(IOccupyableBuilding building);

	EMovableType getMovableType();

	ESoldierType getSoldierType();

	IAttackableMovable getMovable();

	void leaveOccupyableBuilding(ShortPoint2D newPosition);

	void setSelected(boolean selected);

	void informAboutAttackable(IAttackable attackable);

	/**
	 * Sets the position of the movable but does not display it.
	 * 
	 * @param door
	 */
	void setPosition(ShortPoint2D door);

	/**
	 * This method is called when this movable has to defend it's building at the given position.
	 * 
	 * @param pos
	 *            The position the defending movable is standing.
	 */
	void setDefendingAt(ShortPoint2D pos);

}
