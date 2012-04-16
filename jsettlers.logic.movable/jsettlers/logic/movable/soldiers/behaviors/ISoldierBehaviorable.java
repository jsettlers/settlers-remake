package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.movable.IHexMovable;
import jsettlers.logic.movable.IMovableGrid;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISoldierBehaviorable {

	IMovableGrid getGrid();

	void goToTile(ShortPoint2D first);

	byte getPlayer();

	void setAction(EAction action, float duration);

	void setVisible(boolean visible);

	IBuildingOccupyableMovable getBuildingOccupier();

	void executeHit(IHexMovable movable);

	void calculatePathTo(ShortPoint2D door);

	void setDontMove(boolean dontMove);

	ESoldierType getSoldierType();

}
